package utils;

/**
 * The class defines how RPC server handle a message  
 */
public class MessageHandler {

	/**
	 * RPC message handler constructor and initalization, will launch new thread for each new RPC request
	 * @param msg information pass by RPC call.
	 * @return
	 */
	public Integer handleMsg(String msg) {
		
		HandlerThread hThread = new HandlerThread(msg, Server.lock);
		hThread.start();
		return 0;
	}
}
