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

  public class Server implements Runnable {
      private static final int port = 8080;
      protected String id = "";
      protected WebServer webServer;
  	  protected Thread t;//Server thread
  	  public static ReentrantLock lock = new ReentrantLock();
  	  public static int total = 0;
  	  
      public Server(String id) {
 
          try {
        	  this.id = id;
              webServer = new WebServer(port + Integer.parseInt(id));
              
              XmlRpcServer xmlRpcServer = webServer.getXmlRpcServer();
            
              PropertyHandlerMapping phm = new PropertyHandlerMapping();
              /* Load handler definitions from a property file.
               * The property file might look like:
               *   Calculator=org.apache.xmlrpc.demo.Calculator
               *   org.apache.xmlrpc.demo.proxy.Adder=org.apache.xmlrpc.demo.proxy.AdderImpl
               
              phm.load(Thread.currentThread().getContextClassLoader(),
                       "MyHandlers.properties");
              */
              phm.addHandler("MessageHandler", MessageHandler.class);
              // phm.addHandler("Adder", AdderImpl.class);

              xmlRpcServer.setHandlerMapping(phm);
            
              XmlRpcServerConfigImpl serverConfig =
                  (XmlRpcServerConfigImpl) xmlRpcServer.getConfig();
              serverConfig.setEnabledForExtensions(true);
              serverConfig.setContentLengthOptional(false);
			  //webServer.start();
		} catch (XmlRpcException e) {
			e.printStackTrace();
		}
    	  
      }
      
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
	 * Start listening a worker
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