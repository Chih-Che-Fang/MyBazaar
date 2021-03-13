dir /s /B src\*.java > sources.txt
dir /s /B libs\*.jar > libs.txt
javac -cp ".\libs\*" -d bin -s bin -h bin @sources.txt
del "output\*.out"
del config.txt

echo 0,127.0.0.1:8080>>config.txt
echo 1,127.0.0.1:8081>>config.txt
echo 2,127.0.0.1:8082>>config.txt
echo 3,127.0.0.1:8083>>config.txt
echo 4,127.0.0.1:8084>>config.txt
echo 4,127.0.0.1:8085>>config.txt

 REM REM run test case 1: one peer to be a buyer of fish and another to be a seller of fish. Ensure that all fish is sold and restocked forever.
  
   echo b 0 fish 1 1 test1 > info-id-0
   echo s 1 fish 0 1 test1 > info-id-1
   
   start cmd /k java -cp ".\bin;.\libs\*" roles.Person 1
   start cmd /k java -cp ".\bin;.\libs\*" roles.Person 0
   TIMEOUT 210
   taskkill /F /IM java.exe
   TIMEOUT 2
  
REM  REM run test case 2:  one peer to be a buyer of fish and another to be a seller of boar. Ensure that nothing is sold.
REM   
REM    echo b 0 fish 1 1 test2 > info-id-0
REM    echo s 1 boars 0 1 test2 > info-id-1
REM    start cmd /k java -cp ".\bin;.\libs\*" roles.Person 1
REM    start cmd /k java -cp ".\bin;.\libs\*" roles.Person 0
REM    TIMEOUT 12
REM    taskkill /F /IM java.exe
REM    TIMEOUT 2
REM  
REM  REM run test case 3: Randomly assign buyer and seller roles. Ensure that items keep being sold throughout.
REM   
REM    echo na 0 na 1 1 test3 > info-id-0
REM    echo na 1 na 0 1 test3 > info-id-1
REM    start cmd /k java -cp ".\bin;.\libs\*" roles.Person 1
REM    start cmd /k java -cp ".\bin;.\libs\*" roles.Person 0
REM    TIMEOUT 12
REM    taskkill /F /IM java.exe
REM    TIMEOUT 2
REM 
REM  REM run test case 4: One seller of boar, 3 buyers of boars, the remaining peers have no role. Fix the neighborhood structure so that buyers and sellers are 2-hop away in the peer-to-peer overlay network.
REM   echo b 0 boars 4 1 test4 > info-id-0
REM   echo s 1 boars 4,5 1 test4 > info-id-1
REM   echo b 2 boars 5 1 test4 > info-id-2
REM   echo b 3 boars 5 1 test4 > info-id-3
REM   echo n 4 boars 0,1 1 test4 > info-id-4
REM   echo n 5 boars 1,2,3 1 test4 > info-id-5
REM  
REM    start cmd /k java -cp ".\bin;.\libs\*" roles.Person 1
REM    start cmd /k java -cp ".\bin;.\libs\*" roles.Person 0
REM    start cmd /k java -cp ".\bin;.\libs\*" roles.Person 2
REM    start cmd /k java -cp ".\bin;.\libs\*" roles.Person 3
REM    start cmd /k java -cp ".\bin;.\libs\*" roles.Person 4
REM    start cmd /k java -cp ".\bin;.\libs\*" roles.Person 5
REM    TIMEOUT 12
REM    taskkill /F /IM java.exe
REM pause