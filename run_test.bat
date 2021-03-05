dir /s /B src\*.java > sources.txt
dir /s /B libs\*.jar > libs.txt
javac -cp ".\libs\*" -d bin -s bin -h bin @sources.txt
del "output\*.out"


REM REM REM run test case 1: one peer to be a buyer of fish and another to be a seller of fish. Ensure that all fish is sold and restocked forever.
REM 
REM  echo b 0 fish 1 1 > info-id-0
REM  echo s 1 fish 0 1 > info-id-1
REM  
REM  start cmd /k java -cp ".\bin;.\libs\*" roles.Person 1
REM  start cmd /k java -cp ".\bin;.\libs\*" roles.Person 0
REM  TIMEOUT 7
REM  taskkill /F /IM java.exe
REM  
REM REM REM run test case 2:  one peer to be a buyer of fish and another to be a seller of boar. Ensure that nothing is sold.
REM 
REM  echo b 0 fish 1 1 > info-id-0
REM  echo s 1 boars 0 1 > info-id-1
REM  start cmd /k java -cp ".\bin;.\libs\*" roles.Person 1
REM  start cmd /k java -cp ".\bin;.\libs\*" roles.Person 0
REM  TIMEOUT 7
REM  taskkill /F /IM java.exe
REM 
REM REM run test case 3: Randomly assign buyer and seller roles. Ensure that items keep being sold throughout.
REM 
REM  echo na 0 na 1 1 > info-id-0
REM  echo na 1 na 0 1 > info-id-1
REM  start cmd /k java -cp ".\bin;.\libs\*" roles.Person 1
REM  start cmd /k java -cp ".\bin;.\libs\*" roles.Person 0
REM  TIMEOUT 10
REM  taskkill /F /IM java.exe


REM run test case 4:
 echo b 0 boars 4 1 test4 > info-id-0
 echo s 1 boars 4,5 1 test4 > info-id-1
 echo b 2 boars 5 1 test4 > info-id-2
 echo b 3 boars 5 1 test4 > info-id-3
 echo n 4 boars 0,1 1 test4 > info-id-4
 echo n 5 boars 1,2,3 1 test4 > info-id-5

  start cmd /k java -cp ".\bin;.\libs\*" roles.Person 1
  start cmd /k java -cp ".\bin;.\libs\*" roles.Person 0
  start cmd /k java -cp ".\bin;.\libs\*" roles.Person 2
  start cmd /k java -cp ".\bin;.\libs\*" roles.Person 3
  start cmd /k java -cp ".\bin;.\libs\*" roles.Person 4
  start cmd /k java -cp ".\bin;.\libs\*" roles.Person 5
  TIMEOUT 10
  taskkill /F /IM java.exe
pause