# My Bazaar Design Doc

Authors: Chih-Che Fang, Shivam Srivastava, Shiyang Wang

# Problem description

This project is a simple distributed program implement a peer-to-peer market, The bazaar contains two types of people (i.e., computing nodes): buyers and sellers. Each seller sells one of the following goods: fish, salt, or boars. Each buyer in the bazaar is looking to buy one of these three items.
Buyers find sellers by announcing what they wish to buy or sell. All  announcements must follow the peer-to-peer model. Each buyer shall communicate their needs to all her neighbors, who will then propagate the message to their neighbors and so on, until a seller is found or the maximum limit on the number of hops a message can traverse is reached.
If a seller is found, then the seller sends back a response that traverses in the reverse direction back to the buyer. At this point, the buyer and the seller directly enter into a transaction (without using intermediate peers).

# System Design

## UML Class Diagram
![UML diagram](./UML.png "UML")

## Class Discription  
**Person:** A person represent a peer. It's a abstract (Super-class) class of buyers, sellers, and peer with no role, define all required attributes and action (lookup/buy/sell) each peer must have.  
**Buyer:** A buyer peer, it implement buy/sell/lookup and inherit all attributes from person class.  
**Seller:** A seller peer, it implement buy/sell/lookup and inherit all attributes from person class.  
**NoRole:** Peer with no role, it implement buy/sell/lookup and inheritall attributes from person class.  
**Server:** A server represent the RPC server resides in each peer.  
**Client:** A client represent the RPC client used to access remote server.    
**SystemMonitor:** A class used to store and claculate the latency / averge respond time of client requests.  
**AddressLookUp:** A (neighbor, ip) mapping lookup owned by each peer, allowing a peer to send RPC requests to other peer.  
**Logger:** A class used to output important output for each test cases. We can therefore veify the correctness of each test case.  
**MessageHandler:** The class defines how RPC server handle a message  
**MessageHandlerThread:** The message handler will create a new message handler thread to process each new request. This class defines the specific logic of how to handle each type of message.  


## Interface Discription:  
**Buy:** Interface that defines how buyer respond to the seller to buy a product.  
**LookUp:** Interface that defines how a buyer to search the network; all matching sellers respond to this message with their IDs using a reply(buyerID, sellerID) call.  
**Sell:** Interface that defines how a seller respond to a buyer if the seller has the product the buyer like.  

## Sequence Diagram
![WorkFlow diagram](./WorkFlow.PNG "WorkFlow")


# How it Works
 ## Bootstraping & Communication
We applied XML-RPC framework as peer communication way. Each peer is at the same time a RPC server and RPC client. When a peering is created, it lauch a listening RPC server to keep receive client request from remote peers. Since each peer has global knowledge (Ex. Other peer's ip and port address, what neighor it has, etc...) of the network topology, it can sends search request to discover neighbors and wait for their response. In contrast, if a peer receive a rqeuest from peers, it knows the ip/port address and is able to respond to the peer. 

Server maps its message handler to a class. In our system, it mapps its message handler to MessageHandler class and the class will implement the logic of how to handle each type of mesaage. For each new request, the MessageHandler will lauch a new thread to process it.

## RPC Message Format
We used our customized RPC message as follows:  
Format = [Action arg1 (arg2) msgPath sentTo]  

**Action:** Indicate whether it is a buy/sell/lookup request  
**arg1, arg2:** Argument of the request  
**msgPath:** The path of the message, used by reply message to traverse original route back to the buyer.  
**sentTo:** Indicate what peer the message is sending to. The information is used by RPC server to deliever this request to the right peer.  

Here is one example of RPC message that sellerID 1 sent a reply message back to buyerID 0 along with path 0-1:
[Reply 1 0 1 01 0 ]

## Global IP/Port Address Configuration
To allow peers to communicate with each other, we need to give them other peer's address and port, we use a file - config.txt to ecord the information.
Format = [PeerID, IPAddress:Port]  

**PeerID:** ID of the peer  
**IPAddress:Port:** The peer's ipv4 address and listening port  

Here is one example of configt file:  
0,127.0.0.1:8080  
1,127.0.0.1:8081  
2,127.0.0.1:8082  
3,127.0.0.1:8083  
4,127.0.0.1:8084  
4,127.0.0.1:8085  



## Concurency / Race Condition Protection
When a RPC server receive a new client rqeuest, its message handler will launch a new thread to process the message. To enable concurrent message processing, our peer to peer distributed system use a shared file to store the information of each peer (Ex. product, type, item count, etc...). Therfore, the information of each peer need to be proctected and we used a lock to protect the shared peer information. When a peer read/write its data, we ensured the whole operation and process is atomic and therefore avoid the race condition. To be more specific, it avoids that a seller with only 1 item sell multiple products to products to buyer (Since it is possible that a seller will send multiple reply to different buyers)

## Peer Shared Information Format
We store the shared peer information and named it as info-id with the format:  
Format = [type peerID Product NeighborID Count TestName]

**type:** Indicate whether the peer is a buyer, seller, or ro role. When the value is "na", system ramdomly assign a type to this peer.  
**peerID:** The peer's ID  
**Product:** The product the buyer want to buy or a seller want to sell. When the value is "na", system ramdomly assign a product to this peer.  
**NeighborID:** Indicate all peer IDs that are neighbored to the peer. Neighbor ID is split by ",".  
**Count:** Number of product left to sell.  
**TestName:** Indicate the test name where the peer belongs to. It is used to mark what test the output log belongs to.  

Here is one example of shared peer information that a buyer want to buy fish and is neighbored with peer 2 & 3:
[b 1 fish 2,3 0 test1]

## Automatic Multiple Server Deployment
### Dynamic server ceation
We have pr-created Amazon AMI image that have Java SDK 8 installed. We dynamically create security group that allows RPC access permission. We create EC2 instance from the pre-created AMI image and attached it with the created security group. We tag each EC2 instance with a tag MyBazaar32144321" so that we can later access them and release them.

### Dynamic code mgration and compliation
We migrate the latest code to remote server using scp and invoke script linux_complie.sh to compile the code using ssh.

### Run test 1 ~ test 4
We write peer inital state (Ex. type, product, neighbors, etc...) to info-id and generate gloabal topology knowledge (Each peer's IP/port address) into config.txt using . Then we deploy the peers in each server (EC2 instance) using ssh. We wait a certain amount of time and kill all the peers after each test. Doing the same routine until all the tests finished.

### Gather test output(log) for validation
We use scp to pull test output under output folder from all remote server. We store the output from each sever to the local machine's output folder. Ex. If server1's ip address is 128.0.35.1, we store the output to output\128.0.35.1. Since all output is tagged with test name, we know which test and what is the machine the output belongs to. We used these information to validate if the distibuted system act as we expect.

### Release AWS resource
We terminate all EC2 instances and delete security group created in previous in the end of the test

# Validation & Test
## Test Cases
**Test1 (Milestone1):** Assign one peer to be a buyer of fish and another to be a seller of fish. Ensure that all fish is sold and restocked forever.  
**Test2 (Milestone1):** Assign one peer to be a buyer of fish and another to be a seller of boar. Ensure that nothing is sold.  
**Test3 (Milestone1):** Randomly assign buyer and seller roles. Ensure that items keep being sold throughout  
**Test4 (Milestone2, Simulation of Race Condition):** One seller of boar, 3 buyers of boars, the remaining peers have no role. Fix the neighborhood structure so that buyers and sellers are 2-hop away in the peer-to-peer overlay network. Ensure that all items are sold and restocked and that all buyers can buy forever. **(This case also simulate race condition)**  
**Test5 (Milestone3):** Run test1~test4 again, but deploy peers on different AWS EC2 instances.  

## Automatic Test Scripts
**run_local_test.bat:** This script will atomatically compile the code and perform test 1 ~ test 4 in order on local machine. Finally store output under output foler for validation.  

**run_distributed_test.bat:**  This script will atomatically create Amazon EC2 instances, migrating & compling the code and config file to remote servers, deploying peers on remote server, perform test 1 ~ test 4 in order on remote EC2 instances. Finally store output under output foler for validation and relase all cloud reources. For more detail please see the chapter, "How it Works/Automatic Multiple Server Deployment".  

## Test Output (Log)  
We store all testing output under output folder and use them to validate the correctness of each test case. For local testing, each file is named with testID.out (Ex. test4.out). It will print all peers' log on that machine, like which buyer baught a product or which seller sold a product. For distributed testing on different server, we store all remote server's output under output/ipaddress (Ex.output/127.35.6.1). In this way, we know which test and machine this log/output belongs to and easy to debug. Here is one output example of test1:  

SellerID:1 start to sell fish  
BuyerID:0 start to buy fish  
SellerID:1 start to sell fish  
BuyerID:0 bought fish from 1  
BuyerID:0 start to buy fish  
SellerID:1 start to sell boars  
BuyerID:0 bought fish from 1  
BuyerID:0 start to buy fish  

## Verification of All Test Cases  
**Test1 output:**  
BuyerID:0 start to buy fish  
SellerID:1 start to sell fish  
SellerID:1 start to sell fish  
BuyerID:0 bought fish from 1  
BuyerID:0 start to buy salt  
**Result:** Pass, buyer 1 successfully buy out fish from seller 1  

**Test2 output:**  
BuyerID:0 start to buy fish  
SellerID:1 start to sell boars  
**Result:** Pass, buyer 1 buy nothing, seller 1 sells nothing  


**Test3 output:**  
BuyerID:0 start to buy boars  
BuyerID:1 start to buy salt  
**Result:** Pass, buyer 1 buy nothing, seller 1 sells nothing  


**Test4 output:**  
SellerID:1 start to sell boars  
PeerID:5 with no role start to work  
BuyerID:2 start to buy boars  
BuyerID:0 start to buy boars  
BuyerID:3 start to buy boars  
PeerID:4 with no role start to work  
SellerID:1 replied buyerID:0  
SellerID:1 replied buyerID:2  
SellerID:1 replied buyerID:3  
SellerID:1 start to sell boars  
SellerID:1 start to sell fish  
BuyerID:2 bought boars from 1  
BuyerID:2 start to buy fish  
SellerID:1 replied buyerID:2  
SellerID:1 start to sell salt  
BuyerID:2 bought fish from 1  
BuyerID:2 start to buy fish  
**Result:** Pass, buyer 0,2,3 want to buy boars from seller 1, and seller 1 also replied all of them (race condition), only buyer 2 baught boars from seller 1 successfully  

**Test5 output: (Run on distributed servers, log is collect from different servers)**  
BuyerID:2 start to buy boars  
BuyerID:0 start to buy boars  
SellerID:1 start to sell boars  
PeerID:5 with no role start to work  
PeerID:4 with no role start to work  
BuyerID:3 start to buy boars  
SellerID:1 replied buyerID:0  
SellerID:1 replied buyerID:3  
SellerID:1 replied buyerID:2  
SellerID:1 start to sell boars  
BuyerID:3 bought boars from 1  
SellerID:1 start to sell fish  
BuyerID:3 start to buy boars   
SellerID:1 start to sell salt  
SellerID:1 replied buyerID:2  
SellerID:1 start to sell salt  
BuyerID:2 bought salt from 1  
BuyerID:2 start to buy fish  
**Result:** Pass, buyer 0,2,3 want to buy boars from seller 1, and seller 1 also replied all of them (race condition), only buyer 3 baught boars from seller 1 successfully  

# Evaluation and Measurements
## 1.	Compare the latencies to process a RPC call between peers on different servers, as well as latencies between peers on your local machine(s)  

Latency (multiple servers) | Latency (single server)
------------ | -------------
100ms | 13ms  

*PS: latency calculated from 1000 sampled RPC requests

Results show latency on single machine is much less than the latency when peers are dployed on multiple different servers. It's reasoable due to the network transportation time and marchsalling/unmarshalling process of RPC call. In single machine, the latency doesn't include network transportation time while in multiples servers network trasnportime time matters.

## 2.	Compare the response times when multiple clients are concurrently making requests to a peer, for instance, you can vary the number of neighbors for each peer and observe how the average response time changes, make necessary plots to support your conclusions.  


Avg Response Time (1 neighbor) | Avg Response Time (3 neighbor) |  Avg Response Time (5 neighbor) |  Avg Response Time (9 neighbor)
------------ | ------------- | ------------- | -------------
5.1327ms | 5.12ms | 5.26ms  |  5.28ms

PS: all response time sampled from 1000 requests
PS: We defines response time as the time the client receive response from remote servers, the time doesn't imply the message is being processed since we use asyncrounous RPC call design, the server will launch a new thread whenever it receive a request from client, sending message to background processing, and respond to client immediately.  

Results show averged response time are almost the same (only slight increase) as more multiple clients are making requests to a peer. It matches what we expected since our system design will lauch a new thread whenever receive a client requests. The response time shouldn't be affacted by the number of concurrent request since the server respond to clients as soon as it receive the request. However, we still see a little increase in avergage response time, I think it might be affacted by the time used to launch new thread. As more request recive concurrently, the server spends some time launching new thread, which cause the slight difference.  


# Design Tradeoffs
**RPC/RMI Call V.S RPC Call**  
We must choose one of way for communication among peers. The pros of RPC/RMI is:    
1. Allow user to define communicate interface, more human-readable and concise  
2. Don't need to worry about low-level newtorking commucaiton implmentation  
3. Remove the complexity of low-level newtorking commucaiton implmentation  

The cons of RPC/RMI:  
1. Recuced flexibility on low-leve networking implementation and commication interface  

We finally choose to use RPC as our peer communication since we want to hide the complexity of lowe-lever networking commucation, making the system more simple, concise, and easy to debug. Also, this assignment doesn't require us to implement deiffcult connection falut tolerance or mechanism, we don't need socket for flexbility.  

**Syncronous RPC Call V.S Asyncrounous RPC Call**  
The pros of using Syncronous RPC is:
1. Don't need to worry about concurrcency issues caused by multi-thread  
2. Lower complexity of system design. 
The cons of Syncronous RPC is:  
1. Impaired performance (throughput) if multiple rqeuest happens concurrently.  
2. Higher latency 

We finally chose Asyncrounous RPC Call since we want better performance(throughput) and shorter respond time of message processing. We used lock and shared data to overcome the concurrent issue caused by multi-thread.  

**Thread Pool V.S Dynamically Creating New Thread**  
To handle client RPC request, we can choose either lauch new thread every time or use existing thread pool to allocate thread to message processing task. The pros of Thread Pool is:  
1.Shorter respond time of client request since we don't need to create new thread dynamically  
The cons is:  
1. Higher complexity of system since we need to handle the creation and recyle of threads  
2. Higher memeory usage since we must maintain certain amount of thread  
3. Hard to debug  

We finally choose Dynamically Creating New Thread since the message handler thread dosen't have too much data attribute and creating is fast. Given that performance doesn't have too much difference and we want to simplify our design, we can dynamically creating new thread to handle RPC client request.

**Dynamic Creation of EC2 Instances V.S Hot Stand-By EC2 Instances**  
When launching multiple server for peer delpyment, we must choose between whether to dynamically creaing new instances or deploy peers on hot stand-by servers. The pros of dynamic creation of EC2 Instances is:  
1. Lower cost of AWS EC2 instance (EC2 bills by running time of instances)  
The cons of hot stand-by EC2 instance is:  
1. Longer delpoyment time since we need to wait for instances to be created  
2. Need to re-migrate and compile code every time we update our code  

We finally chose Dynamic Creation of EC2 Instances since cost is significat if we maintain a lot of running EC2 instances. We write a script to quickly creating security group and instances when deploying.  


**Open All TCP Port between Different Remote Server V.S Open only certain range of TCP Port between Different Remote Server**
To allow RPC access permission bewtween different servers so that peer can communicate with each other. We attached Amazon security group to each Amazon EC2 instances to implement this permission control. The pros of opening all TCP Port between Different Remote Serve is:  
1. Don't need to worry about port range change (Ex. Add/Deletion) as we may want to add new port to a peer  
2. Easy to configure  
The cons is:  
1.  Impaired security since if one of server ismalicious, it can exoloit and attack the opened port  

We finally chose to open only certain range of TCP port bewteen Different Remote Server. We use script to automcaitally create sercurity group to save the effort of chaning port in the future.


# How to Run It

See [README.md #How to run?](https://github.com/Chih-Che-Fang/MyBazaar#how-to-run "How to run")

# Possible Improvements and Extensions

1. We assume the id of hosts has to be ascending sequence, otherwise it will cause errors.
2. The MaxHopCount currently is hardcoded, we could customize this number in the future for buyers.
3. We are using thread per request model currently we could be optimized by using thread pool or even coroutine.
