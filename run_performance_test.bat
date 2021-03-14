REM dir /s /B src\*.java > sources.txt
REM dir /s /B libs\*.jar > libs.txt
REM javac -cp ".\libs\*" -d bin -s bin -h bin @sources.txt
REM rmdir output /S /Q
REM mkdir output
REM del config.txt

REM Create security group and EC2 instances
REM aws ec2 create-security-group --group-name MyBazaar32144321 --description "SG for 677 lab1"
REM aws ec2 authorize-security-group-ingress --group-name MyBazaar32144321 --protocol tcp --port 0-65535 --cidr 0.0.0.0/0
REM aws ec2 run-instances --image-id ami-023ba056901e16c76 --instance-type t2.micro --key-name 677kp --tag-specifications "ResourceType=instance,Tags=[{Key=MyBazaar32144321,Value=m1}]" > instance.json
REM aws ec2 run-instances --image-id ami-023ba056901e16c76 --instance-type t2.micro --key-name 677kp --tag-specifications "ResourceType=instance,Tags=[{Key=MyBazaar32144321,Value=m2}]" > instance.json
REM aws ec2 run-instances --image-id ami-023ba056901e16c76 --instance-type t2.micro --key-name 677kp --tag-specifications "ResourceType=instance,Tags=[{Key=MyBazaar32144321,Value=m3}]" > instance.json
REM 
REM timeout 45
REM 
REM REM Access EC2 public ip address and instane ids
REM aws ec2 describe-instances  --filter "Name=tag-key,Values=MyBazaar32144321" "Name=instance-state-name,Values=running" --query "Reservations[*].Instances[*].InstanceId" --output text> ids.txt
REM aws ec2 describe-instances  --filter "Name=tag-key,Values=MyBazaar32144321" "Name=instance-state-name,Values=running" --query "Reservations[*].Instances[*].PublicIpAddress" --output text> ips.txt
REM 
REM 
REM Generate Address Mapping for each peer
setlocal ENABLEDELAYEDEXPANSION
REM @echo off 
REM set /a count = 0
REM set /a port = 8080
REM for /f "tokens=*" %%a in (ips.txt) do (
REM   echo !count!,%%a:!port!>> config.txt
REM   set /a count += 1
REM   set /a port += 1
REM )
REM for /f "tokens=*" %%a in (ips.txt) do (
REM   echo !count!,%%a:!port!>> config.txt
REM   set /a count += 1
REM   set /a port += 1
REM )
REM 

REM REM Migrate latest code and complie code
REM for /f "tokens=*" %%a in (ips.txt) do (
REM 	ssh -o "StrictHostKeyChecking no" -i 677kp.pem ec2-user@%%a "rm -rf MyBazaar"
REM 	scp -o "StrictHostKeyChecking no" -i 677kp.pem -r %cd%  ec2-user@%%a:~/MyBazaar
REM 	ssh -o "StrictHostKeyChecking no" -i 677kp.pem ec2-user@%%a "cd ~/MyBazaar;sh compile_linux.sh"
REM )



REM REM run test case 1: Test latency and averge repponse time when only 1 neighbor
REM 
REM echo b 0 fish 1 1 test1> info-id-0
REM echo s 1 fish 0 1 test1> info-id-1
REM set /a count = 0
REM REM Copy config and inital peer state to remote servers
REM for /f "tokens=*" %%a in (ips.txt) do (
REM   if !count! == 2 (goto :BREAK1)
REM   scp -o "StrictHostKeyChecking no" -i 677kp.pem config.txt ec2-user@%%a:~/MyBazaar
REM   scp -o "StrictHostKeyChecking no" -i 677kp.pem info-id-0 ec2-user@%%a:~/MyBazaar
REM   scp -o "StrictHostKeyChecking no" -i 677kp.pem info-id-1 ec2-user@%%a:~/MyBazaar
REM   start cmd /k ssh -o "StrictHostKeyChecking no" -i 677kp.pem ec2-user@%%a "cd ~/MyBazaar;java -cp "./bin:./libs/*" roles.Person !count!"
REM   set /a count += 1
REM )
REM :BREAK1
REM TIMEOUT 210
REM 
REM REM Kill all peers
REM set /a count = 0
REM for /f "tokens=*" %%a in (ips.txt) do (
REM   if !count! == 2 (goto :BREAK11)
REM   ssh -o "StrictHostKeyChecking no" -i 677kp.pem ec2-user@%%a "killall java"
REM   set /a count += 1
REM )
REM :BREAK11
REM TIMEOUT 2

REM Kill all peers
set /a count = 0
for /f "tokens=*" %%a in (ips.txt) do (
  if !count! == 2 (goto :BREAK21)
  ssh -o "StrictHostKeyChecking no" -i 677kp.pem ec2-user@%%a "killall java"
  set /a count += 1
)
:BREAK21
TIMEOUT 2


REM run test case 2: Test latency and averge repponse time when 3 neighbor
echo b 0 fish 1 1 test2> info-id-0
echo s 1 fish 0,2,3 1 test2> info-id-1
echo b 2 fish 1 1 test2> info-id-2
echo b 3 fish 1 1 test2> info-id-3
echo b 4 fish 1 1 test2> info-id-4
echo b 5 fish 1 1 test2> info-id-5

set /a count = 0
REM Copy config and inital peer state to remote servers
for /f "tokens=*" %%a in (ips.txt) do (
  if !count! == 2 (goto :BREAK2)
  set server!count!_addr=%%a
  scp -i 677kp.pem config.txt ec2-user@%%a:~/MyBazaar
  scp -i 677kp.pem info-id-0 ec2-user@%%a:~/MyBazaar
  scp -i 677kp.pem info-id-1 ec2-user@%%a:~/MyBazaar
  scp -i 677kp.pem info-id-2 ec2-user@%%a:~/MyBazaar
  scp -i 677kp.pem info-id-3 ec2-user@%%a:~/MyBazaar
  scp -i 677kp.pem info-id-4 ec2-user@%%a:~/MyBazaar
  scp -i 677kp.pem info-id-5 ec2-user@%%a:~/MyBazaar
  start cmd /k ssh -o "StrictHostKeyChecking no" -i 677kp.pem ec2-user@%%a "cd ~/MyBazaar;java -cp "./bin:./libs/*" roles.Person !count!"
  set /a count += 1
)
:BREAK2

start cmd /k ssh -o "StrictHostKeyChecking no" -i 677kp.pem ec2-user@%server0_addr% "cd ~/MyBazaar;java -cp "./bin:./libs/*" roles.Person 2"
start cmd /k ssh -o "StrictHostKeyChecking no" -i 677kp.pem ec2-user@%server0_addr% "cd ~/MyBazaar;java -cp "./bin:./libs/*" roles.Person 3"
start cmd /k ssh -o "StrictHostKeyChecking no" -i 677kp.pem ec2-user@%server0_addr% "cd ~/MyBazaar;java -cp "./bin:./libs/*" roles.Person 4"
start cmd /k ssh -o "StrictHostKeyChecking no" -i 677kp.pem ec2-user@%server0_addr% "cd ~/MyBazaar;java -cp "./bin:./libs/*" roles.Person 5"
TIMEOUT 210

REM Kill all peers
set /a count = 0
for /f "tokens=*" %%a in (ips.txt) do (
  if !count! == 2 (goto :BREAK21)
  ssh -o "StrictHostKeyChecking no" -i 677kp.pem ec2-user@%%a "killall java"
  set /a count += 1
)
:BREAK21
TIMEOUT 2



REM REM run test case 3: Randomly assign buyer and seller roles. Ensure that items keep being sold throughout.
REM echo na 0 na 1 1 test3> info-id-0
REM echo na 1 na 0 1 test3> info-id-1
REM set /a count = 0
REM REM Copy config and inital peer state to remote servers
REM for /f "tokens=*" %%a in (ips.txt) do (
REM   if !count! == 2 (goto :BREAK3)
REM   scp -i 677kp.pem config.txt ec2-user@%%a:~/MyBazaar
REM   scp -i 677kp.pem info-id-0 ec2-user@%%a:~/MyBazaar
REM   scp -i 677kp.pem info-id-1 ec2-user@%%a:~/MyBazaar
REM   start cmd /k ssh -o "StrictHostKeyChecking no" -i 677kp.pem ec2-user@%%a "cd ~/MyBazaar;java -cp "./bin:./libs/*" roles.Person !count!"
REM   set /a count += 1
REM )
REM :BREAK3
REM TIMEOUT 12
REM 
REM REM Kill all peers
REM set /a count = 0
REM for /f "tokens=*" %%a in (ips.txt) do (
REM   if !count! == 2 (goto :BREAK31)
REM   ssh -o "StrictHostKeyChecking no" -i 677kp.pem ec2-user@%%a "killall java"
REM   set /a count += 1
REM )
REM :BREAK31
REM TIMEOUT 2
REM 
REM 
REM 
REM REM run test case 4: One seller of boar, 3 buyers of boars, the remaining peers have no role. Fix the neighborhood structure so that buyers and sellers are 2-hop away in the peer-to-peer overlay network.
REM echo b 0 boars 4 1 test4> info-id-0
REM echo s 1 boars 4,5 1 test4> info-id-1
REM echo b 2 boars 5 1 test4> info-id-2
REM echo b 3 boars 5 1 test4> info-id-3
REM echo n 4 boars 0,1 1 test4> info-id-4
REM echo n 5 boars 1,2,3 1 test4> info-id-5
REM set /a count = 0
REM 
REM for /f "tokens=*" %%a in (ips.txt) do (
REM   scp -i 677kp.pem config.txt ec2-user@%%a:~/MyBazaar
REM   scp -i 677kp.pem info-id-0 ec2-user@%%a:~/MyBazaar
REM   scp -i 677kp.pem info-id-1 ec2-user@%%a:~/MyBazaar
REM   scp -i 677kp.pem info-id-2 ec2-user@%%a:~/MyBazaar
REM   scp -i 677kp.pem info-id-3 ec2-user@%%a:~/MyBazaar
REM   scp -i 677kp.pem info-id-4 ec2-user@%%a:~/MyBazaar
REM   scp -i 677kp.pem info-id-5 ec2-user@%%a:~/MyBazaar
REM   
REM   set /a id1=!count!
REM   set /a id2=!count! + 3
REM   
REM   start cmd /k ssh -o "StrictHostKeyChecking no" -i 677kp.pem ec2-user@%%a "cd ~/MyBazaar;java -cp "./bin:./libs/*" roles.Person !id1!"
REM   start cmd /k ssh -o "StrictHostKeyChecking no" -i 677kp.pem ec2-user@%%a "cd ~/MyBazaar;java -cp "./bin:./libs/*" roles.Person !id2!"
REM   set /a count += 1
REM )
REM 
REM TIMEOUT 12
REM 
REM REM Kill all peers
REM for /f "tokens=*" %%a in (ips.txt) do (
REM   ssh -o "StrictHostKeyChecking no" -i 677kp.pem ec2-user@%%a "killall java"
REM   mkdir output\%%a
REM   scp -i 677kp.pem -r ec2-user@%%a:~/MyBazaar/output/*.out output\%%a\
REM )
REM 
REM aws ec2 delete-security-group --group-name MyBazaar32144321
REM for /f "tokens=*" %%a in (ids.txt) do (
REM   aws ec2 terminate-instances --instance-ids %%a
REM )
pause