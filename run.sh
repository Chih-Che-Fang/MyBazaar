#!/bin/bash
workdir="$( cd "$( dirname "${BASH_SOURCE[0]}" )" > /dev/null && pwd )"

find "${workdir}"/src -type f -name "*.java"  > sources-linux.txt
find "${workdir}"/libs -type f -name "*.jar" > libs-linux.txt

javac -cp "${workdir}/libs/*" -d "${workdir}/bin" -s "${workdir}/bin" -h "${workdir}/bin" @sources-linux.txt

echo "run test case 1....."
echo "b 0 fish 1 1" > info-id-0
echo "s 1 fish 0 1" > info-id-1

(java -Xmx500m -cp "${workdir}/bin:${workdir}/libs/*" roles.Person 1) & pid1=$!
(sleep 7 && kill -9 $pid1) &

(java -Xmx500m -cp "${workdir}/bin:${workdir}/libs/*" roles.Person 0) & pid2=$!
(sleep 7 && kill -9 $pid2) &

wait $pid1
wait $pid2


echo "run test case 2....."
echo "b 0 fish 1 1" > info-id-0
echo "s 1 boars 0 1" > info-id-1
(java -Xmx500m -cp "${workdir}/bin:${workdir}/libs/*" roles.Person 1) & pid3=$!
(sleep 7 && kill -9 $pid3) &

(java -Xmx500m -cp "${workdir}/bin:${workdir}/libs/*" roles.Person 0) & pid4=$!
(sleep 7 && kill -9 $pid4) &

wait $pid3
wait $pid4

echo "run test case 3....."

echo "na 0 na 1 1" > info-id-0
echo "na 1 na 0 1" > info-id-1
(java -Xmx500m -cp "${workdir}/bin:${workdir}/libs/*" roles.Person 1) & pid5=$!
(sleep 7 && kill -9 $pid5) &

(java -Xmx500m -cp "${workdir}/bin:${workdir}/libs/*" roles.Person 0) & pid6=$!
(sleep 7 && kill -9 $pid6) &

wait $pid5
wait $pid6
