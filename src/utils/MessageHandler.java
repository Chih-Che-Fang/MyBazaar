package utils;

public class MessageHandler {
	
	public Integer handleMsg(String msg) {
		
		HandlerThread hThread = new HandlerThread(msg, Server.lock);
		hThread.start();
		return 0;
	}
}
