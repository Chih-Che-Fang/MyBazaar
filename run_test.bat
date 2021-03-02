dir /s /B src\*.java > sources.txt
dir /s /B libs\*.jar > libs.txt
javac -cp ".\libs\*" -d bin -s bin -h bin @sources.txt



REM REM run test case 1: one peer to be a buyer of fish and another to be a seller of fish. Ensure that all fish is sold and restocked forever.

 echo b 0 fish 1 1 > info-id-0
 echo s 1 fish 0 1 > info-id-1
 
 start cmd /k java -cp ".\bin;.\libs\*" roles.Person 1
 start cmd /k java -cp ".\bin;.\libs\*" roles.Person 0
 TIMEOUT 7
 taskkill /F /IM java.exe
 
REM REM run test case 2:  one peer to be a buyer of fish and another to be a seller of boar. Ensure that nothing is sold.

 echo b 0 fish 1 1 > info-id-0
 echo s 1 boars 0 1 > info-id-1
 start cmd /k java -cp ".\bin;.\libs\*" roles.Person 1
 start cmd /k java -cp ".\bin;.\libs\*" roles.Person 0
 TIMEOUT 7
 taskkill /F /IM java.exe

REM run test case 3: Randomly assign buyer and seller roles. Ensure that items keep being sold throughout.

 echo na 0 na 1 1 > info-id-0
 echo na 1 na 0 1 > info-id-1
 start cmd /k java -cp ".\bin;.\libs\*" roles.Person 1
 start cmd /k java -cp ".\bin;.\libs\*" roles.Person 0
 TIMEOUT 10
 taskkill /F /IM java.exe

pause