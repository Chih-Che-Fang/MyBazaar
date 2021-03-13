# My Bazaar Design Doc

Authors: Chih-Che Fang, Shivam Srivastava, Shiyang Wang

# Problem description

This project is a simple distributed program implement a peer-to-peer market, The bazaar contains two types of people (i.e., computing nodes): buyers and sellers. Each seller sells one of the following goods: fish, salt, or boars. Each buyer in the bazaar is looking to buy one of these three items.
Buyers find sellers by announcing what they wish to buy or sell. All  announcements must follow the peer-to-peer model. Each buyer shall communicate their needs to all her neighbors, who will then propagate the message to their neighbors and so on, until a seller is found or the maximum limit on the number of hops a message can traverse is reached.
If a seller is found, then the seller sends back a response that traverses in the reverse direction back to the buyer. At this point, the buyer and the seller directly enter into a transaction (without using intermediate peers).

# Design considerations

This project is written in Java and it has strictly implemented three interfaces 
```
lookup (product_name,hopcount)
reply(buyerID, sellerID)
buy(sellerID)
```
in `src/action/LookUp.java`, `src/action/Reply.java` and `src/action/Buy.java` 
and in roles we define a class called Person to implement those three interfaces and 
also extended two child class from Person called Buyer and Seller.
Person could be both server and client since both Buyer and Seller need to either 
do reply or lookup and relay messages, so we defined another 5 classes included in the Person class.

```
AddressLookUp: read global hosts IP/Port configuration in config.txt.
Client: xml-rpc client.
MessageHandler: xml-rpc reciver handler. (included in the Server class)
HandlerThread: used to handle multithread for MessageHandler.
Server: xml-rpc server implementation also to register MessageHandler. 
```


The overall diagram shows below:
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
**MessageHandle:**
**MessageHandleThread:**


## Interface Discription:  
**Buy:** Interface that defines how buyer respond to the seller to buy a product.  
**LookUp:** Interface that defines how a buyer to search the network; all matching sellers respond to this message with their IDs using a reply(buyerID, sellerID) call.  
**Sell:** Interface that defines how a seller respond to a buyer if the seller has the product the buyer like.  


# Evaluation and Measurements


# Design tradeoffs

# How to run it

See [README.md #How to run?](https://github.com/Chih-Che-Fang/MyBazaar#how-to-run "How to run")

# Possible improvements and extensions

1. We assume the id of hosts has to be ascending sequence, otherwise it will cause errors.
2. The MaxHopCount currently is hardcoded, we could customize this number in the future for buyers.
3. We are using thread per request model currently we could be optimized by using thread pool or even coroutine.
