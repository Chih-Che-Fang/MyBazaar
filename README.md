# MyBazaar
# Group Members: 
# Name: Chih-Che Fang, SPIRE ID: 32144321, Email: chihchefang@umass.ed;
# Name: Shivam Srivastava, SPIRE ID: 31668793, Email: shivamsrivas@umass.edu; 
# Name: Shiyang Wang, SPIRE ID: 32542622, Email: shiyangwang@umass.edu;

*I Aggree to share this source code with my group members

#Enviornment: Windows + Java SDK 8 Installed  
#Applications:  
[Milestone1]  
test1:Assign one peer to be a buyer of fish and another to be a seller of fish. Ensure that all fish is sold and restocked forever.  
test2:Assign one peer to be a buyer of fish and another to be a seller of boar. Ensure that nothing is sold.  
test3:Randomly assign buyer and seller roles. Ensure that items keep being sold throughout.  
[Milestone2]  
test4 (Race condition included):One seller of boar, 3 buyers of boars, the remaining peers have no role. Fix the neighborhood structure so that buyers and sellers are 2-hop away in the peer-to-peer overlay network. Ensure that all items are sold and restocked and that all buyers can buy forever.



# How to run?  
1. Switch to the root directory of this project (Ex. cd /MyBazaar) and confirm the path contains no "blank"  
2. Perform run_local_test.bat on Windows OS (With JDK installed and with JDK environment variable set), and will automatically launch multiple peers and construct the topology, finally run the peer-to-peer system  
3. See the testing result on console, it will tell you if the buyer bought the products or not. For every test case, it will jump two console windows, one represent buyer process and another represent seller process, they will individually print logs like:  

Output info to loc:info-id-0  
ServerId:0 start!!  
Reply 0 1 0 0  
ServerID:0 receive msg:Reply 0 1 0 0 with path:0  
Output info to loc:info-id-0  
BuyerID:0 bought fish from 1  
BuyerID:0 start to buy boars  
Output info to loc:info-id-0  

4.To verify the correctness, check the log output of test1.out ~ test4.out under output folder  


# Directory/Files Description
-	Bin: Complied JAVA class
-	Src: Project source code
-	Run_local_test.bat: local testing script for mileston 1 and 2 (windows)
-	Run_test.bat: testing script for milestone 3 (windows)
-	output: output file of all test cases
-	run.sh: testing script (linux)
-	libs: All dependency libs
-	Docs: Design documents
-	Read.md: Readme file
