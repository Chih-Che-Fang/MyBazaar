# MyBazaar
# Group Members: 
# Name: Chih-Che Fang, SPIRE ID: 32144321, Email: chihchefang@umass.ed;
# Name: Shivam Srivastava, SPIRE ID: 31668793, Email: shivamsrivas@umass.edu; 
# Name: Shiyang Wang, SPIRE ID: 32542622, Email: shiyangwang@umass.edu;

*I Aggree to share this source code with my group members

#Enviornment: Windows + Java SDK 8 Installed  
#Applications:
[Milestone1]
Assign one peer to be a buyer of fish and another to be a seller of fish. Ensure that all fish is sold and restocked forever.
Assign one peer to be a buyer of fish and another to be a seller of boar. Ensure that nothing is sold.
Randomly assign buyer and seller roles. Ensure that items keep being sold throughout.


# How to run?  
1. Switch to the root directory of this project (Ex. cd /mapreduce-Cih-Che-Fang) and confirm the path contains no "blank"  
2. Perform run_test.bat on Windows OS (With JDK installed and with JDK environment variable set), and will automatically launch multiple peers and construct the topology, finally run the peer-to-peer system
3. See the testing result on console, it will tell you if the output is equal to the Spark output. Logs like:  

Output info to loc:info-id-0
ServerId:0 start!!
Reply 0 1 0 0
ServerID:0 receive msg:Reply 0 1 0 0 with path:0
Output info to loc:info-id-0
BuyerID:0 bought fish from 1
BuyerID:0 start to buy boars
Output info to loc:info-id-0
Output info to loc:info-id-0
Output info to loc:info-id-0
Output info to loc:info-id-0


# Directory/Files Description
-	Bin: Complied JAVA class
-	Src: Project source code
-	Run_test.bat: testing script
-	libs: All dependency libs
-	Local_refresh: For local debugging use
-	Docs: Design documents
-	Read.md: Readme file
