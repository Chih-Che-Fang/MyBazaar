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
**Buyer:** A buyer peer, it implement buy/sell/lookup and inherite all attributes from person class.  
**Seller:** A seller peer, it implement buy/sell/lookup and inherite all attributes from person class.  
**NoRole:** Peer with no role, it implement buy/sell/lookup and inherite all attributes from person class.  
**Server:** A server represent the RPC server resides in each peer.  
**Client:** A client represent the RPC client used to access remote server.    
**SystemMonitor:** A class used to store and claculate the latency / averge respond time of client requests.  
**AddressLookUp:** A (neighbor, ip) mapping lookup owned by each peer, allowing a peer to send RPC requests to other peer.  
**Logger:** A class used to output important output for each test cases. We can therefore veify the correctness of each test case.  
**MessageHandler:** The class defines how RPC server handle a message  
**MessageHandlerThread:** The message handler will create a new message handler thread to process each new request. This calss defines the specific logic of how to handle each type of message.  


## Interface Discription:  
**Buy:** Interface that defines how buyer respond to the seller to buy a product.  
**LookUp:** Interface that defines how a buyer to search the network; all matching sellers respond to this message with their IDs using a reply(buyerID, sellerID) call.  
**Sell:** Interface that defines how a seller respond to a buyer if the seller has the product the buyer like.  

# Test Cases
**Test1 (Milestone1):** Assign one peer to be a buyer of fish and another to be a seller of fish. Ensure that all fish is sold and restocked forever.  
**Test2 (Milestone1):** Assign one peer to be a buyer of fish and another to be a seller of boar. Ensure that nothing is sold.  
**Test3 (Milestone1):** Randomly assign buyer and seller roles. Ensure that items keep being sold throughout  
**Test4 (Milestone2):** One seller of boar, 3 buyers of boars, the remaining peers have no role. Fix the neighborhood structure so that buyers and sellers are 2-hop away in the peer-to-peer overlay network. Ensure that all items are sold and restocked and that all buyers can buy forever. **(This case also simulate race condition)**  
**Test5 (Milestone3):** Run test1~test4 again, but deploy peers on different AWS EC2 instances.



# How it Works
 ## Bootstraping & Communication
We applied XML-RPC framework as peer communication way. Each peer is at the same time a RPC server and RPC client. When a peering is created, it lauch a listening RPC server to keep receive client request from remote peers. Since each peer has global knowledge (Ex. Other peer's ip and port address, what neighor it has, etc...) of the network topology, it can sends search request to discover neighbors and wait for their response. In contrast, if a peer receive a rqeuest from peers, it knows the ip/port address and is able to respond to the peer. 

Server maps its message handler to a class. In our system, it mapps its message handler to MessageHandler class and the class will implement the logic of how to handle each type of mesaage. For each new request, the MessageHandler will lauch a new thread to process it.

## RPC Message Format
Format = [Action arg1 (arg2) msgPath sentTo]  
Action: Indicate whether it is a buy/sell/lookup request  
arg1, arg2: Argument of the request  
msgPath: The path of the message, used by reply message to traverse original route back to the buyer.  
sentTo: Indicate what peer the message is sending to. The information is used by RPC server to deliever this request to the right peer.  

## Concurency / Race Condition Protection
When a RPC server receive a new client rqeuest, its message handler will launch a new thread to process the message. To enable concurrent message processing, our peer to peer distributed system use a shared file to store the information of each peer (Ex. product, type, item count, etc...). Therfore, the information of each peer need to be proctected and we used a lock to protect the shared peer information. When a peer read/write its data, we ensured the whole operation and process is atomic and therefore avoid the race condition. To be more specific, it avoids that a seller with only 1 item sell multiple products to products to buyer (Since it is possible that a seller will send multiple reply to different buyers)

## Automatic multiple server deployment

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

# How to Run It

See [README.md #How to run?](https://github.com/Chih-Che-Fang/MyBazaar#how-to-run "How to run")

# Possible Improvements and Extensions

1. We assume the id of hosts has to be ascending sequence, otherwise it will cause errors.
2. The MaxHopCount currently is hardcoded, we could customize this number in the future for buyers.
3. We are using thread per request model currently we could be optimized by using thread pool or even coroutine.
