package utils;

/**
 * The class defines how RPC server handle a message  
 */
public class MessageHandler {

	/**
	 * @param msg information pass by rpc call.
	 * @return
	 */
	public Integer handleMsg(String msg) {
		
		HandlerThread hThread = new HandlerThread(msg, Server.lock);
		hThread.start();
		return 0;
	}
}
