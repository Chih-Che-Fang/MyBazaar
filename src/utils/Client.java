package utils;


  import java.net.MalformedURLException;
  import java.net.URL;

  import org.apache.xmlrpc.XmlRpcException;
  import org.apache.xmlrpc.client.XmlRpcClient;
  import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
  import org.apache.xmlrpc.client.XmlRpcCommonsTransportFactory;
  import org.apache.xmlrpc.client.util.ClientFactory;


  public class Client {
	  
	  XmlRpcClient client;
	  public Client(String addr) {

          try {
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

	        // make the a regular call
	        /*
	        Object[] params = new Object[] { " ", " ", " " };
			Integer ret = (Integer) client.execute("Person.recvMsg", params);
			System.out.println(ret);
			*/
	          
		  } catch (MalformedURLException e) {
			e.printStackTrace();
		  }

	  }
	  
	  public Integer execute(String method, Object[] params) {
			Integer res = null;
			try {
				res = (Integer) client.execute(method, params);
			} catch (XmlRpcException e) {
				e.printStackTrace();
			}
			return res;
	  }
  }