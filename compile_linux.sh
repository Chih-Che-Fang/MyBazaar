# /bin/bash
find "./src" -type f -name "*.java"> sources-linux.txt
find "./libs" -type f -name "*.jar"> libs-linux.txt
rm -f "output/*"
rm -f "config.txt"
javac -cp "./libs/*" -d "./bin" -s "./bin" -h "./bin" @sources-linux.txt