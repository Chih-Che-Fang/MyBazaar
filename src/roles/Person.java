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

public class Person implements LookUp, Reply, Buy {

	static final String[] productList = {"fish", "salt", "boars"};
	static final String[] roleList = {"b", "s"};
	public String type = ""; //1:buyer 0:seller
	public String id = "";
	public String product = "";
	private final AtomicInteger count;

	public Random r;

	public HashMap<String, Client> clients = new HashMap<>();
	public AddressLookUp addressLookUp;
	public Logger logger = null;

	int getItemNum() {
		return count.get();
	}

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

	void resetItemNum() {
		while(true) {
			if(count.compareAndSet(0, Seller.m)) {
				return;
			}
		}
	}


	public Person(String type, String id, String product, String[] neighbors, int count, String output) { /*s 1 fish 0 0*/
		
		this.addressLookUp = new AddressLookUp("config.txt");
		this.count = new AtomicInteger(count);
		this.id = id;
		this.r = new Random();
		this.product = product.equals("na")? productList[r.nextInt(productList.length)] : product;
		this.type = type;
		this.logger = new Logger(output);
		
		for(String nbr : neighbors) {
			this.clients.put(nbr, new Client(this.addressLookUp.get(nbr)));
		}
		//this.dump();
	}
	
	//Message handlers, will call implemented function interfaces: lookup, buy, or reply
	public void handleLookUpMsg(String product, int maxHop, String msgPath) {
		
		String senderId = (msgPath.charAt(msgPath.length() - 1) - '0') + "";
		--maxHop;
		
		if(this.lookUp(product, maxHop)) {
			Client c = clients.get(senderId);
			String buyerID = msgPath.charAt(0) + "";
			
			c.execute("MessageHandler.handleMsg", 
					new Object[] {String.format("%s %s %s %s %s", "Reply", 
							buyerID, this.id, msgPath, msgPath.charAt(msgPath.length() - 1) + "")});
		}
		
		if(maxHop > 0) {
			for(String cId : clients.keySet()) {
				if(cId.compareTo(senderId) == 0) continue;
				Client c = clients.get(cId);
				Integer ret = c.execute("MessageHandler.handleMsg", 
						new Object[] {String.format("%s %s %s %s %s", "LookUp", product, maxHop, msgPath + id, cId)});
			}
		}
	}
	
	public void handleReplyMsg(String buyerID, String sellerID, String msgPath) {
		
		
		if(this.reply(buyerID, sellerID)) {
			Client c = new Client(addressLookUp.get(sellerID));
			Integer ret = c.execute("MessageHandler.handleMsg", 
					new Object[] {String.format("%s %s %s %s", "Buy", sellerID, msgPath + product, sellerID)});
		} else {
		
			int receiverId = msgPath.charAt(msgPath.length() - 2) - '0';
			Client c = clients.get(Integer.toString(receiverId));
			Integer ret = c.execute("MessageHandler.handleMsg", 
					new Object[] {String.format("%s %s %s %s %s", "Reply", buyerID, sellerID, msgPath.substring(0, msgPath.length() - 1), receiverId)});
		}
		
	}
	
	public void handleBuyMsg(String sellerId, String msgPath) {
		if(msgPath.substring(1).equals(product) && buy(sellerId)) {
			String senderID = msgPath.charAt(0) + "";
			Client c = new Client(addressLookUp.get(senderID));
			Integer ret = c.execute("MessageHandler.handleMsg", 
					new Object[] {String.format("%s %s %s %s", "Buy", sellerId, msgPath, senderID)});
		}
	}
	
	//Return neighbors of the peer
	public String getNeighbors() {
		StringBuilder res = new StringBuilder();
		for(String nbrID : clients.keySet()) {
			res.append(nbrID).append(",");
		}
		res.setLength(Math.max(0, res.length() - 1));
		return res.toString();
	}
	
	//Dump peer information to disk
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
	
	
	public static void main(String[] args) {
		
		//Start server and message handler
		String id = args[0];
		Person p = accessPerson(id);
		Server server = new Server(args[0]);
		server.start();
		p.logStatus();
		
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

	
	//virtual functions, will be overrided by sub-class
	public void logStatus() {
		
	}
	
	@Override
	public boolean buy(String sellerID) {
		return false;
	}

	@Override
	public boolean reply(String buyerID, String sellerID) {
		return false;
	}

	@Override
	public boolean lookUp(String product, int hopCount) {
		return false;
	}

}
