package utils;

public class MessageHandler {
	
	public Integer handleMsg(String msg) {
		HandlerThread hThread = new HandlerThread(msg);
		hThread.start();
		return 0;
	}
}
