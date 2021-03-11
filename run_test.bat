dir /s /B src\*.java > sources.txt
dir /s /B libs\*.jar > libs.txt
javac -cp ".\libs\*" -d bin -s bin -h bin @sources.txt
rmdir output /S /Q
mkdir output
del config.txt

REM Create security group and EC2 instances
aws ec2 create-security-group --group-name MyBazaar32144321 --description "SG for 677 lab1"
aws ec2 authorize-security-group-ingress --group-name MyBazaar32144321 --protocol tcp --port 0-65535 --cidr 0.0.0.0/0
aws ec2 run-instances --image-id ami-05d7c468e832704bc --instance-type t2.micro --key-name 677kp --tag-specifications "ResourceType=instance,Tags=[{Key=MyBazaar32144321,Value=m1}]" > instance.json
aws ec2 run-instances --image-id ami-05d7c468e832704bc --instance-type t2.micro --key-name 677kp --tag-specifications "ResourceType=instance,Tags=[{Key=MyBazaar32144321,Value=m2}]" > instance.json
aws ec2 run-instances --image-id ami-05d7c468e832704bc --instance-type t2.micro --key-name 677kp --tag-specifications "ResourceType=instance,Tags=[{Key=MyBazaar32144321,Value=m3}]" > instance.json

timeout 45

REM Access EC2 public ip address and instane ids
aws ec2 describe-instances  --filter "Name=tag-key,Values=MyBazaar32144321" "Name=instance-state-name,Values=running" --query "Reservations[*].Instances[*].InstanceId" --output text> ids.txt
aws ec2 describe-instances  --filter "Name=tag-key,Values=MyBazaar32144321" "Name=instance-state-name,Values=running" --query "Reservations[*].Instances[*].PublicIpAddress" --output text> ips.txt


REM Generate Address Mapping for each peer
setlocal ENABLEDELAYEDEXPANSION
@echo off 
set /a count = 0
set /a port = 8080
for /f "tokens=*" %%a in (ips.txt) do (
  echo !count!,%%a:!port!>> config.txt
  set /a count += 1
  set /a port += 1
)
for /f "tokens=*" %%a in (ips.txt) do (
  echo !count!,%%a:!port!>> config.txt
  set /a count += 1
  set /a port += 1
)


REM Migrate latest code and complie code
for /f "tokens=*" %%a in (ips.txt) do (
	ssh -o "StrictHostKeyChecking no" -i 677kp.pem ec2-user@%%a "rm -rf MyBazaar"
	scp -o "StrictHostKeyChecking no" -i 677kp.pem -r C:\Users\user\677-project1\MyBazaar  ec2-user@%%a:~/MyBazaar
	ssh -o "StrictHostKeyChecking no" -i 677kp.pem ec2-user@%%a "cd ~/MyBazaar;sh compile_linux.sh"
)



REM run test case 1: one peer to be a buyer of fish and another to be a seller of fish. Ensure that all fish is sold and restocked forever.

echo b 0 fish 1 1 test1> info-id-0
echo s 1 fish 0 1 test1> info-id-1
set /a count = 0
REM Copy config and inital peer state to remote servers
for /f "tokens=*" %%a in (ips.txt) do (
  if !count! == 2 (goto :BREAK1)
  scp -o "StrictHostKeyChecking no" -i 677kp.pem config.txt ec2-user@%%a:~/MyBazaar
  scp -o "StrictHostKeyChecking no" -i 677kp.pem info-id-0 ec2-user@%%a:~/MyBazaar
  scp -o "StrictHostKeyChecking no" -i 677kp.pem info-id-1 ec2-user@%%a:~/MyBazaar
  start cmd /k ssh -o "StrictHostKeyChecking no" -i 677kp.pem ec2-user@%%a "cd ~/MyBazaar;java -cp "./bin:./libs/*" roles.Person !count!"
  set /a count += 1
)
:BREAK1
TIMEOUT 12

REM Kill all peers
set /a count = 0
for /f "tokens=*" %%a in (ips.txt) do (
  if !count! == 2 (goto :BREAK11)
  ssh -o "StrictHostKeyChecking no" -i 677kp.pem ec2-user@%%a "killall java"
  set /a count += 1
)
:BREAK11
TIMEOUT 2



REM run test case 2:  one peer to be a buyer of fish and another to be a seller of boar. Ensure that nothing is sold.
echo b 0 fish 1 1 test2> info-id-0
echo s 1 boars 0 1 test2> info-id-1
set /a count = 0
REM Copy config and inital peer state to remote servers
for /f "tokens=*" %%a in (ips.txt) do (
  if !count! == 2 (goto :BREAK2)
  scp -i 677kp.pem config.txt ec2-user@%%a:~/MyBazaar
  scp -i 677kp.pem info-id-0 ec2-user@%%a:~/MyBazaar
  scp -i 677kp.pem info-id-1 ec2-user@%%a:~/MyBazaar
  start cmd /k ssh -o "StrictHostKeyChecking no" -i 677kp.pem ec2-user@%%a "cd ~/MyBazaar;java -cp "./bin:./libs/*" roles.Person !count!"
  set /a count += 1
)
:BREAK2
TIMEOUT 12

REM Kill all peers
set /a count = 0
for /f "tokens=*" %%a in (ips.txt) do (
  if !count! == 2 (goto :BREAK21)
  ssh -o "StrictHostKeyChecking no" -i 677kp.pem ec2-user@%%a "killall java"
  set /a count += 1
)
:BREAK21
TIMEOUT 2



REM run test case 3: Randomly assign buyer and seller roles. Ensure that items keep being sold throughout.
echo na 0 na 1 1 test3> info-id-0
echo na 1 na 0 1 test3> info-id-1
set /a count = 0
REM Copy config and inital peer state to remote servers
for /f "tokens=*" %%a in (ips.txt) do (
  if !count! == 2 (goto :BREAK3)
  scp -i 677kp.pem config.txt ec2-user@%%a:~/MyBazaar
  scp -i 677kp.pem info-id-0 ec2-user@%%a:~/MyBazaar
  scp -i 677kp.pem info-id-1 ec2-user@%%a:~/MyBazaar
  start cmd /k ssh -o "StrictHostKeyChecking no" -i 677kp.pem ec2-user@%%a "cd ~/MyBazaar;java -cp "./bin:./libs/*" roles.Person !count!"
  set /a count += 1
)
:BREAK3
TIMEOUT 12

REM Kill all peers
set /a count = 0
for /f "tokens=*" %%a in (ips.txt) do (
  if !count! == 2 (goto :BREAK31)
  ssh -o "StrictHostKeyChecking no" -i 677kp.pem ec2-user@%%a "killall java"
  set /a count += 1
)
:BREAK31
TIMEOUT 2



REM run test case 4: One seller of boar, 3 buyers of boars, the remaining peers have no role. Fix the neighborhood structure so that buyers and sellers are 2-hop away in the peer-to-peer overlay network.
echo b 0 boars 4 1 test4> info-id-0
echo s 1 boars 4,5 1 test4> info-id-1
echo b 2 boars 5 1 test4> info-id-2
echo b 3 boars 5 1 test4> info-id-3
echo n 4 boars 0,1 1 test4> info-id-4
echo n 5 boars 1,2,3 1 test4> info-id-5
set /a count = 0

for /f "tokens=*" %%a in (ips.txt) do (
  scp -i 677kp.pem config.txt ec2-user@%%a:~/MyBazaar
  scp -i 677kp.pem info-id-0 ec2-user@%%a:~/MyBazaar
  scp -i 677kp.pem info-id-1 ec2-user@%%a:~/MyBazaar
  scp -i 677kp.pem info-id-2 ec2-user@%%a:~/MyBazaar
  scp -i 677kp.pem info-id-3 ec2-user@%%a:~/MyBazaar
  scp -i 677kp.pem info-id-4 ec2-user@%%a:~/MyBazaar
  scp -i 677kp.pem info-id-5 ec2-user@%%a:~/MyBazaar
  
  set /a id1=!count!
  set /a id2=!count! + 3
  
  start cmd /k ssh -o "StrictHostKeyChecking no" -i 677kp.pem ec2-user@%%a "cd ~/MyBazaar;java -cp "./bin:./libs/*" roles.Person !id1!"
  start cmd /k ssh -o "StrictHostKeyChecking no" -i 677kp.pem ec2-user@%%a "cd ~/MyBazaar;java -cp "./bin:./libs/*" roles.Person !id2!"
  set /a count += 1
)

TIMEOUT 12

REM Kill all peers
for /f "tokens=*" %%a in (ips.txt) do (
  ssh -o "StrictHostKeyChecking no" -i 677kp.pem ec2-user@%%a "killall java"
  mkdir output\%%a
  scp -i 677kp.pem -r ec2-user@%%a:~/MyBazaar/output/*.out output\%%a\
)

aws ec2 delete-security-group --group-name MyBazaar32144321
for /f "tokens=*" %%a in (ids.txt) do (
  aws ec2 terminate-instances --instance-ids %%a
)
pause