package utils;

/**
 * MessageHandler for XML-RPC call setup.
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
