package utils;


  import java.net.MalformedURLException;
  import java.net.URL;

  import org.apache.xmlrpc.XmlRpcException;
  import org.apache.xmlrpc.client.XmlRpcClient;
  import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
  import org.apache.xmlrpc.client.XmlRpcCommonsTransportFactory;
  import org.apache.xmlrpc.client.util.ClientFactory;

import evaluation.SystemMonitor;


/**
 * XmlRPCClient Class for init and making RPC call.
 */
  public class Client {
	  /** XmlRpcClient variable **/
	  XmlRpcClient client;


	/**
	 * Client Constructor for using for call neighbors.
	 * @param addr addr for init neighbors connections.
	 */
	  public Client(String addr) {

          try {
        	//Set client ip/port address
        	XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
			config.setServerURL(new URL(String.format("http://%s/xmlrpc", addr)));
	        config.setEnabledForExtensions(true);  
	        config.setConnectionTimeout(60 * 1000);
	        config.setReplyTimeout(60 * 1000);

	        client = new XmlRpcClient();
	        
	        // use Commons HttpClient as transport
	        client.setTransportFactory(
	            new XmlRpcCommonsTransportFactory(client));
	        // set configuration
	        client.setConfig(config);
	          
		  } catch (MalformedURLException e) {
			e.printStackTrace();
		  }

	  }

	/**
	 * Execute a RPC request
	 * @param method rpc method for xmlrpc client invoke.
	 * @param params parameters for xmlrpc client invoke.
	 * @return xmlrpc client call return value.
	 */
	  public Integer execute(String method, Object[] params) {
			long start = System.currentTimeMillis();
		
			Integer res = null;
			try {
				res = (Integer) client.execute(method, params);
			} catch (XmlRpcException e) {
				//e.printStackTrace();
				System.out.println("Remote client is not ready yet, retry to connect!");
			}
			SystemMonitor.addLatencySample((int)(System.currentTimeMillis() - start));
			return res;
	  }
  }