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
## Bootstraping  

## Concurency


# Evaluation and Measurements
1.	Measure the latencies to process a RPC call between peers on different servers, as well as latencies between peers on your local machine(s)  

Latency on multiple servers: Max Latency from 1000 requests = 100ms  
Latency on 1 local server: Max Latency from 1000 requests = 13ms  


2.	Compute the average response time per client search request by measuring the response time seen by a client for, say, 1000 sequential requests. measure the response times when multiple clients are concurrently making requests to a peer, for instance, you can vary the number of neighbors for each peer and observe how the average response time changes, make necessary plots to support your conclusions.  

(3 different servers, number of neighbors = 1) Nb1: Avg response time from 1000 requests = 4.8327ms  
(3 different servers, number of neighbors = 3) Nb1: Avg response time from 1000 requests = 5.12ms  
(3 different servers, number of neighbors = 5) Nb1: Avg response time from 1000 requests = 5.26ms  
(3 different servers, number of neighbors = 9) Nb1: Avg response time from 1000 requests = 5.28ms  
(3	different servers, number of neighbors = 20) Nb1: Avg response time from 1000 requests = 5.286ms  

# Design Tradeoffs

# How to Run It

See [README.md #How to run?](https://github.com/Chih-Che-Fang/MyBazaar#how-to-run "How to run")

# Possible Improvements and Extensions

1. We assume the id of hosts has to be ascending sequence, otherwise it will cause errors.
2. The MaxHopCount currently is hardcoded, we could customize this number in the future for buyers.
3. We are using thread per request model currently we could be optimized by using thread pool or even coroutine.
