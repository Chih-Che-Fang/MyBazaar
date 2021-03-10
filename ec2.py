import os
import socket
import boto3
import paramiko
import time
import string
import random
import re
from paramiko import AuthenticationException, BadHostKeyException, SSHException


def retry_ssh(ip, user, key_file, initial_wait=0, interval=0, retries=1):
    ssh = paramiko.SSHClient()
    ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())

    time.sleep(initial_wait)

    for x in range(retries):
        try:
            ssh.connect(ip, username=user, pkey=key_file)
            print("ssh connected")
            return ssh
        except (BadHostKeyException, AuthenticationException, SSHException, socket.error) as e:
            print(e)
            time.sleep(interval)
    return None


class BazzarWrapper:
    ec2r = boto3.resource('ec2')
    ec2c = boto3.client('ec2')
    ec2_instances = []
    # This AMI is an Amazon Linux 2 with docker pre install.
    ami_id = 'ami-0fe9395c1b5817f9b'
    ec2_instance_ids = []
    ec2_instances_count = 0
    ec2_keypair_name = 'ec2-keypair-' + ''.join(random.choices(string.ascii_uppercase + string.digits, k=4))
    ec2_keypair_file_name = 'ec2-keypair.pem'

    def __init__(self, count):
        self.ec2_instances_count = count
        pass

    def __del__(self):
        self.clean_up()

    def create_key_pair(self):
        # remove pem file first if exist.
        if os.path.isfile(self.ec2_keypair_file_name):
            os.remove(self.ec2_keypair_file_name)

        # create a file to store the key locally
        outfile = open(self.ec2_keypair_file_name, 'w')

        # call the boto ec2 function to create a key pair
        key_pair = self.ec2r.create_key_pair(KeyName=self.ec2_keypair_name)

        # capture the key and store it in a file
        key_pair_out = str(key_pair.key_material)
        print(key_pair_out)
        outfile.write(key_pair_out)
        os.chmod("./{}".format(self.ec2_keypair_file_name), mode=0o400)

    def create_ec2_instances(self):
        self.ec2_instances = self.ec2r.create_instances(
            ImageId=self.ami_id,
            MinCount=self.ec2_instances_count,
            MaxCount=self.ec2_instances_count,
            InstanceType='t2.micro',
            KeyName=self.ec2_keypair_name
        )
        for ins in self.ec2_instances:
            self.ec2_instance_ids.append(ins.id)

    def spin_up_mybazzar(self):
        while True:
            running_instances = self.ec2r.instances.filter(
                Filters=[{'Name': 'instance-state-name', 'Values': ['running']}])

            instance_check = []
            for instance in running_instances:
                instance_check.append(instance.id)

            if set(instance_check) == set(self.ec2_instance_ids):
                break

            print("running instances: {}, \nexpected{}, waiting...".format(instance_check, self.ec2_instance_ids))
            time.sleep(5)

        for instance in running_instances:
            print("\n\ninstance: {} type: {}".format(instance.id, instance.instance_type))
            print("--------------------------------------------\n")
            priv_key = paramiko.RSAKey.from_private_key_file(os.path.join('.', format(self.ec2_keypair_file_name)))
            ssh = retry_ssh(instance.public_dns_name, 'ec2-user', priv_key, 5, 3, 3)
            if ssh is None:
                print("ssh established failed...")
                return
            input_line = input("Please input person id you want to deploy on instance {}, e.g. 1,2,4".format(instance))
            for seq in re.findall(r'[0-9]+', input_line):
                stdin, stdout, stderr = ssh.exec_command("docker run -ti cs677/lab1:v0.1 {}".format(seq))
                stdin.flush()
                data = stdout.read().splitlines()
                for line in data:
                    print(line.decode())
                    ssh.close()

    def clean_up(self):
        if os.path.isfile(self.ec2_keypair_file_name):
            os.remove(self.ec2_keypair_file_name)
        self.ec2r.KeyPair(self.ec2_keypair_name).delete()
        self.ec2c.terminate_instances(InstanceIds=[ec2.id for ec2 in self.ec2_instances])


def print_intro():
    prompt = 'bp> '
    intro = "Welcome! Type 1-3 to run commands"
    content = """
    1. Create an EC2 cluster
    2. Spin up Bazaar Clients/Servers on EC2 cluster
    3. Delete EC2 cluster

    q or ctrl-c to quit.
    """
    print(intro)
    print(content)
    return input(prompt)


if __name__ == '__main__':
    bz = None
    while True:
        choice = print_intro()
        try:
            if choice == "1":
                ecn = int(input("How many ec2 instances do you want?"))
                if ecn <= 0:
                    print("please make sure to spin up above 0 instances.\n\n")
                    continue
                bz = BazzarWrapper(ecn)
                bz.create_key_pair()
                bz.create_ec2_instances()
            elif choice == "2":
                if bz is None:
                    print("No EC2 cluster created yet.")
                    continue
                bz.spin_up_mybazzar()
            elif choice == "3":
                bz.clean_up()
            elif choice == 'q':
                exit(0)
            else:
                print("Invalid number, please choose 1-3.")
        except Exception as e:
            print(e)
            exit(1)
