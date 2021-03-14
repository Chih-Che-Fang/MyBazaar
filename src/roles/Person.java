package roles;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import action.Buy;
import action.LookUp;
import action.Reply;
import utils.AddressLookUp;
import utils.Client;
import utils.Logger;
import utils.Server;

/*A person represent a peer. It's a abstract (Super-class) class of buyers, sellers, 
 * and peer with no role, define all required attributes and action (lookup/buy/sell) 
 * each peer must have.
 */
public class Person implements LookUp, Reply, Buy {
	/** different product list for seller and buyer **/
	static final String[] productList = {"fish", "salt"/*, "boars"*/};
	/** different roles for person */
	static final String[] roleList = {"b", "s"};
	/** roles type for Person 1:buyer 0:seller **/
	public String type = ""; //1:buyer 0:seller
	/** Person (Peer) id also read from info-id config file **/
	public String id = "";
	/** Product person seller hold or buyer trying to buy **/
	public String product = "";
	/** atomicInteger used to make count decreasing/increasing atomic **/
	private final AtomicInteger count;
	/** random variable **/
	public Random r;
	/** HashMap to save all the neighbors **/
	public HashMap<String, Client> clients = new HashMap<>();
	/** AddressLookUp class to parse out address information **/
	public AddressLookUp addressLookUp;
	/** logger for printing logs **/
	public Logger logger = null;

	/**
	 * get item count atomically
	 * @return Int: number of items.
	 */
	int getItemNum() {
		return count.get();
	}

	/**
	 * decrease count number using CAS
	 * @return  true means decrease success, false means failed.
	 */
	boolean decrementItemNum() {

		while(true) {
			int oldNum = getItemNum();
			int newNum = oldNum - 1;
			if(oldNum == 0) return false;
			if(count.compareAndSet(oldNum, newNum)) {
				return true;
			}
		}
	}

	/**
	 * reset count number when seller sold out all the items switch to a new product.
	 */
	void resetItemNum() {
		while(true) {
			if(count.compareAndSet(0, Seller.m)) {
				return;
			}
		}
	}

	/**
	 * @param type  Buyer(b) or Seller(s) or NoRole(na).
	 * @param id id of a person.
	 * @param product product string buy or sell (salt, boar, fish).
	 * @param neighbors neighbors array to save all the neighbors close to person.
	 * @param count count of items seller hold.
	 * @param output output place for logging.
	 */
	public Person(String type, String id, String product, String[] neighbors, int count, String output) { /*s 1 fish 0 0*/
		this.addressLookUp = new AddressLookUp("config.txt");
		this.count = new AtomicInteger(count);
		this.id = id;
		this.r = new Random();
		this.product = product.equals("na")? productList[r.nextInt(productList.length)] : product;
		this.type = type;
		this.logger = new Logger(output);
		
		//Initialize all neighbor connector
		for(String nbr : neighbors) {
			this.clients.put(nbr, new Client(this.addressLookUp.get(nbr)));
		}
	}

	/**
	 * Message handlers, will call implemented function interfaces: lookup, buy, or reply
	 * @param product the specific product buyer looking for
	 * @param maxHop  the maxHop of the message, decrease by one every time pass through neighbors
	 * @param msgPath msgPath is the information to track the propagation path from sender to receiver
	 */
	public void handleLookUpMsg(String product, int maxHop, String msgPath) {
		
		String senderId = (msgPath.charAt(msgPath.length() - 1) - '0') + "";
		--maxHop;
		
		if(this.lookUp(product, maxHop)) {//cases where seller has the product and reply to buyer
			Client c = clients.get(senderId);
			String buyerID = msgPath.charAt(0) + "";
			
			c.execute("MessageHandler.handleMsg", 
					new Object[] {String.format("%s %s %s %s %s", "Reply", 
							buyerID, this.id, msgPath, msgPath.charAt(msgPath.length() - 1) + "")});
			
			logger.log(String.format("SellerID:%s replied buyerID:%s", this.id, buyerID));
		}
		
		//Peer dosen't have the product the buyer want, simply relay message to neighbors
		if(maxHop > 0) {
			for(String cId : clients.keySet()) {
				if(cId.compareTo(senderId) == 0) continue;
				Client c = clients.get(cId);
				Integer ret = c.execute("MessageHandler.handleMsg", 
						new Object[] {String.format("%s %s %s %s %s", "LookUp", product, maxHop, msgPath + id, cId)});
			}
		}
	}

	/**
	 * @param buyerID  id of the buyer
	 * @param sellerID sellerId is the id of origin seller of the message
	 * @param msgPath msgPath is the information to track the propagation path from sender to receiver
	 */
	public void handleReplyMsg(String buyerID, String sellerID, String msgPath) {
		
		
		if(this.reply(buyerID, sellerID)) { //case where buyer receive reply from seller and start a p2p connection to buy the product
			Client c = new Client(addressLookUp.get(sellerID));
			Integer ret = c.execute("MessageHandler.handleMsg", 
					new Object[] {String.format("%s %s %s %s", "Buy", sellerID, msgPath + product, sellerID)});
		} else { //Peer dosen't the target of the reply message, simply relay the reply message to the target buyer
		
			int receiverId = msgPath.charAt(msgPath.length() - 2) - '0';
			Client c = clients.get(Integer.toString(receiverId));
			Integer ret = c.execute("MessageHandler.handleMsg", 
					new Object[] {String.format("%s %s %s %s %s", "Reply", buyerID, sellerID, msgPath.substring(0, msgPath.length() - 1), receiverId)});
		}
		
	}

	/**
	 * @param sellerId sellerId is the id of origin seller of the message
	 * @param msgPath msgPath is the information to track the propagation path from sender to receiver
	 */
	public void handleBuyMsg(String sellerId, String msgPath) {
		System.out.println(product);
		if(msgPath.substring(1).equals(product) && buy(sellerId)) { //case where seller have product and sold to buyer, sending a buy ACK back to buyer to notify that the buyer succeffuly baught the product
			String senderID = msgPath.charAt(0) + "";
			Client c = new Client(addressLookUp.get(senderID));
			Integer ret = c.execute("MessageHandler.handleMsg", 
					new Object[] {String.format("%s %s %s %s", "Buy", sellerId, msgPath, senderID)});
		}
	}

	/**
	 * @return return neighbors of peers in a string formats
	 */
	//Return neighbors of the peer
	public String getNeighbors() {
		StringBuilder res = new StringBuilder();
		for(String nbrID : clients.keySet()) {
			res.append(nbrID).append(",");
		}
		res.setLength(Math.max(0, res.length() - 1));
		return res.toString();
	}

	/**
	 * Dump shared peer information to disk
	 */
	public void dump() {
		FileWriter fstream;
		BufferedWriter out;
 		
		try {
			String outFile = String.format("info-id-%s", id);
			System.out.println("Output info to loc:" + outFile);
			fstream =  new FileWriter(outFile, false);
			String winfo = String.format("%s %s %s %s %s %s", type, id, product, getNeighbors(), count, logger.output);
			System.out.println("Write:"+winfo);
			out = new BufferedWriter(fstream);
			out.write(String.format("%s %s %s %s %s %s", type, id, product, getNeighbors(), count, logger.output));
			out.newLine();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Read info config file to init different Roles (Buyer,Seller,NoRole)
	 * @param id extract from info-id-%s config file
	 * @return Child Class one of (Buyer,Seller,NoRole) from Person Parent Class.
	 */
	public static Person accessPerson(String id) {
		
		String personFile = String.format("info-id-%s", id);
		String[] personInfo = new String[6];
		FileLock lock = null;
		FileOutputStream fos = null;
		
		//Get person information
		try {

			FileInputStream fInputStream = new FileInputStream(personFile);
			FileChannel inputChannel = fInputStream.getChannel();

			//BufferedReader reader = new BufferedReader(new FileReader(personFile));
			BufferedReader reader = new BufferedReader(new InputStreamReader(fInputStream));
			String line = reader.readLine();
			
			personInfo = line.split("\\s+");

			reader.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//Access person information
		Random r = new Random();
		String type = personInfo[0].equals("na")? Person.roleList[r.nextInt(roleList.length)] : personInfo[0];
		Person p = null;
		switch(type) {
		case "b":
			p = new Buyer(type, personInfo[1], personInfo[2], personInfo[3].split(","), Integer.valueOf(personInfo[4]), personInfo[5]);
			break;
		case "s":
			p = new Seller(type, personInfo[1], personInfo[2], personInfo[3].split(","), Integer.valueOf(personInfo[4]), personInfo[5]);
			break;
		case "n":
			p = new NoRole(type, personInfo[1], personInfo[2], personInfo[3].split(","), Integer.valueOf(personInfo[4]), personInfo[5]);
			break;
		}

		return p;
	}


	/**
	 * program main entry
	 */
	public static void main(String[] args) {
		
		//Start server and message handler
		String id = args[0];
		Person p = accessPerson(id);
		Server server = new Server(args[0]);
		server.start();
		p.logStatus();
		p.dump();
		
		//Buyer continue sending lookup msg
		if(p.type.equals("b")){
			
			while(true) {

				//Add duration for next lookup message
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				//Send lookup message to neighbors
				for(String nbrID : p.clients.keySet()) {
					Client c = p.clients.get(nbrID);
					int maxHop = p.addressLookUp.address.size() - 1;
					Integer ret = c.execute("MessageHandler.handleMsg", 
							new Object[] {String.format("%s %s %s %s %s", "LookUp", p.product, maxHop, id, nbrID)});
					System.out.println(String.format("BuyerID:%s sending neighbors lookup message to buy %s", p.id, p.product));
				}
				
				//Update person information
				p = accessPerson(id);
			}
		}

		server.join();
	}


	/**
	 * virtual functions, will be overrided by sub-class
	 */
	public void logStatus() {
		
	}

	/**
	 * virtual functions, will be overrided by sub-class
	 */
	@Override
	public boolean buy(String sellerID) {
		return false;
	}

	/**
	 * virtual functions, will be overrided by sub-class
	 */
	@Override
	public boolean reply(String buyerID, String sellerID) {
		return false;
	}

	/**
	 * virtual functions, will be overrided by sub-class
	 */
	@Override
	public boolean lookUp(String product, int hopCount) {
		return false;
	}

}
