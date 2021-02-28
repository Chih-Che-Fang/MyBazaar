package roles;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

import action.Buy;
import action.LookUp;
import action.Reply;
import utils.AddressLookUp;
import utils.Client;
import utils.Server;

public class Person implements LookUp, Reply, Buy {

	static final String[] productList = {"fish", "salt", "boars"};
	static final String[] roleList = {"b", "s"};
	public String type = ""; //1:buyer 0:seller
	public String id = "";
	public String product = "";
	public int count = 0;
	Random r = null;

	public HashMap<String, Client> clients = new HashMap<String, Client>();
	public AddressLookUp addresLookUp = null;

	
	public Person(String type, String id, String product, String[] neighbors, int count) { /*s 1 fish 0 0*/
		
		this.addresLookUp = new AddressLookUp("config.txt");
		this.count = count;
		this.id = id;
		this.r = new Random();
		this.product = product.equals("na")? productList[r.nextInt(productList.length)] : product;
		this.type = product.equals("na")? roleList[r.nextInt(roleList.length)] : type;
		
		
		for(String nbr : neighbors) {
			this.clients.put(nbr, new Client(this.addresLookUp.get(nbr)));
		}
		this.dump();
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
			Client c = new Client(addresLookUp.get(sellerID));
			Integer ret = c.execute("MessageHandler.handleMsg", 
					new Object[] {String.format("%s %s %s %s", "Buy", sellerID, msgPath, sellerID)});
		} else {
		
			int receiverId = msgPath.charAt(msgPath.length() - 2) - '0';
			Client c = clients.get(receiverId);
			Integer ret = c.execute("MessageHandler.handleMsg", 
					new Object[] {String.format("%s %s %s %s %s", "Reply", buyerID, sellerID, msgPath.substring(0, msgPath.length() - 1), receiverId)});
		}
		
	}
	
	public void handleBuyMsg(String sellerId, String msgPath) {
		buy(sellerId);
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
			
			out = new BufferedWriter(fstream);
			out.write(String.format("%s %s %s %s %s", type, id, product, getNeighbors(), count));
			out.newLine();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//Dump peer information to disk
	public static void dump(String[] args) {
		FileWriter fstream;
		BufferedWriter out;
 		
		try {
			String outFile = String.format("info-id-%s", args[1]);
			System.out.println("Output info to loc:" + outFile);
			fstream =  new FileWriter(outFile, false);
			
			out = new BufferedWriter(fstream);
			out.write(String.format("%s %s %s %s %s", args[0], args[1], args[2], args[3], args[4]));
			out.newLine();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Person accessPerson(String id) {
		String personFile = String.format("info-id-%s", id);
		String[] personInfo = {"", "", "", "", "", ""};
		
		//Get person information
		try {
			BufferedReader reader = new BufferedReader(new FileReader(personFile));
			String line = reader.readLine();
			personInfo = line.split("\\s+");
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//Access person information
		String type = personInfo[0];
		return (type.equals("b"))? 
				new Buyer(type, personInfo[1], personInfo[2], personInfo[3].split(","), Integer.valueOf(personInfo[4])) :
					new Seller(type, personInfo[1], personInfo[2], personInfo[3].split(","), Integer.valueOf(personInfo[4]));
	}
	
	public static void main(String[] args) {
		
		//Start server and message handler
		String id = args[0];
		Person p = accessPerson(id);
		Server server = new Server(args[0]);
		server.start();

		//Buyer continue sending lookup msg
		if(p.type.equals("b")){
			
			while(true) {
				
				//Send lookup message to neighbors
				for(String nbrID : p.clients.keySet()) {
					try {
						Client c = p.clients.get(nbrID);
						int maxHop = p.addresLookUp.addres.size() - 1;
						Integer ret = c.execute("MessageHandler.handleMsg", 
									new Object[] {String.format("%s %s %s %s %s", "LookUp", p.product, maxHop, id, nbrID)});
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				//Add duration for next lookup message
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				//Update person information
				p = accessPerson(id);
			}
		}

		server.join();
	}

	
	//virtual functions, will be overrided by sub-class
	@Override
	public void buy(String sellerID) {
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
