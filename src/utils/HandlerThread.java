package utils;

import java.util.concurrent.locks.ReentrantLock;

import roles.Person;
/*
 * This class defines the specific logic of how to handle each type of message. 
 * The message handler will create a new message handler thread to process each new request. 
 */
public class HandlerThread implements Runnable {
	/** datagram/data structure for rpc calls **/
	String msg = "";
	/** ReentrantLock used for race condition control **/
	ReentrantLock lock;
	/** message handler thread **/
	protected Thread t;//Message Handler thread

	/**
	 * @param msg datagram/data structure for rpc calls.
	 * @param lock ReentrantLock used for race condition control.
	 */
	public HandlerThread(String msg, ReentrantLock lock) {
		this.msg = msg;
		this.lock = lock;
	}

	/**
	 * Thread execution function.
	 */
	@Override
	public void run() {
		//Parse message
		String[] msgTokens = msg.split("\\s+");
		System.out.println(msg);
		
		String personID = msgTokens[msgTokens.length - 1]; //peerID that the message send to
		String msgPath = msgTokens[msgTokens.length - 2]; //path of the message
		String method = msgTokens[0]; //action of the message request
		System.out.println(String.format("ServerID:%s receive msg:%s with path:%s", personID, msg, msgPath));
		
		//Access person information
		lock.lock();
		Person p = Person.accessPerson(personID);
		switch(method) {
			case "LookUp"://handle a lookup message
				p.handleLookUpMsg(msgTokens[1], Integer.valueOf(msgTokens[2]), msgPath);
				break;
			case "Reply"://handle a rpc message
				p.handleReplyMsg(msgTokens[1], msgTokens[2], msgPath);
				break;
			case "Buy": //handle a buy message
				p.handleBuyMsg(msgTokens[1], msgPath);
				p.dump();
				break;
		}
		lock.unlock();
	}

	/**
	 * Join the handler thread
	 * @return -1 if thread is None.
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

	/**
	 * Start listening a peer.
	 */
	public void start() {
		t = new Thread (this);
		t.start ();
	}
}
