package utils;


import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.common.TypeConverterFactoryImpl;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.server.XmlRpcServerConfigImpl;
import org.apache.xmlrpc.webserver.WebServer;

import roles.Person;

/**
 * Multi-threading XmlRPC Server.
 * each instance will initiate a xml webserver to listen peer's RPC message.
 */
  public class Server implements Runnable {
      private static final int port = 8080; //RPC server's listening port
      protected String id = ""; //Server ID
      protected WebServer webServer; //RPC Server instance
  	  protected Thread t;//Server thread
  	  public static ReentrantLock lock = new ReentrantLock(); //lock to prevent race condition in multi-thread

	/**
	 * XmlRPC Server Constructor
	 * @param id for instance id from config file
	 */
      public Server(String id) {

          try {
        	  this.id = id;
              webServer = new WebServer(port + Integer.parseInt(id));
              
              XmlRpcServer xmlRpcServer = webServer.getXmlRpcServer();
            
              PropertyHandlerMapping phm = new PropertyHandlerMapping();
  
              phm.addHandler("MessageHandler", MessageHandler.class);//Mapped message handler to MessageHandler class

              xmlRpcServer.setHandlerMapping(phm);
            
              XmlRpcServerConfigImpl serverConfig =
                  (XmlRpcServerConfigImpl) xmlRpcServer.getConfig();
              serverConfig.setEnabledForExtensions(true);
              serverConfig.setContentLengthOptional(false);

		} catch (XmlRpcException e) {
			e.printStackTrace();
		}
    	  
      }

	/**
	 * start a RPC listening server
	 */
	@Override
	public void run() {
		 try {
			this.webServer.start();
		 } catch (IOException e) {
			e.printStackTrace();
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
	 * Start listening a peer
	 * Input:
	 * None
	 * Output:
	 * None
	 */
	public void start() {
		t = new Thread (this);
		t.start ();
		System.out.printf("ServerId:%s start!!%n", id);
	}
  }