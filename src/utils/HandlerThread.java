package utils;

import java.util.concurrent.locks.ReentrantLock;

import roles.Person;

public class HandlerThread implements Runnable {
	String msg = "";
	ReentrantLock lock;
	
	protected Thread t;//Message Handler thread
	 
	public HandlerThread(String msg, ReentrantLock lock) {
		this.msg = msg;
		this.lock = lock;
	}
	
	@Override
	public void run() {
		//Parse message
		String[] msgTokens = msg.split("\\s+");
		System.out.println(msg);
		
		String personID = msgTokens[msgTokens.length - 1];
		String msgPath = msgTokens[msgTokens.length - 2];
		String method = msgTokens[0];
		System.out.println(String.format("ServerID:%s receive msg:%s with path:%s", personID, msg, msgPath));
		
		//Access person information
		Person p = Person.accessPerson(personID);
		
		switch(method) {
			case "LookUp":
				p.handleLookUpMsg(msgTokens[1], Integer.valueOf(msgTokens[2]), msgPath);
				break;
			case "Reply":
				p.handleReplyMsg(msgTokens[1], msgTokens[2], msgPath);
				break;
			case "Buy":
				lock.lock();
				p.handleBuyMsg(msgTokens[1], msgPath);
				p.dump();
				lock.unlock();
				break;
		}
	}
	
	/* Function:
	 * Join the listener thread 
	 * Input:
	 * None
	 * Output:
	 * None
	 */
	public int join() {

		if (t != null) {
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return -1;
	}
	
	/* Function:
	 * Start listening a worker
	 * Input:
	 * None
	 * Output:
	 * None
	 */
	public void start() {
		t = new Thread (this);
		t.start ();
	}
}
