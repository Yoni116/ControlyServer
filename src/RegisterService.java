import com.apple.dnssd.*;




public class RegisterService implements RegisterListener 
{
	
	
	 public DNSSDRegistration r;
	 public boolean isRegisterd;
	 public CFServiceRegisterListener delegate;
	 

	public void operationFailed(DNSSDService service, int errorCode)
	    {
	    System.out.println("Registration failed " + errorCode);
	    delegate.serviceFailed();
	    }

	  // Display registered name on success
	  public void serviceRegistered(DNSSDRegistration registration, int flags,
	    String serviceName, String regType, String domain)
	    {
	    System.out.println("Registered Name  : " + serviceName);
	    System.out.println("           Type  : " + regType);
	    System.out.println("           Domain: " + domain);
	    delegate.serviceStarted();
	    }

	  // Do the registration
	  public RegisterService(String name, int port, CFServiceRegisterListener listener)  
	    {
		isRegisterd = false; 
		delegate = listener;
	    System.out.println("Registration Starting");
	    System.out.println("Requested Name: " + name);
	    System.out.println("          Port: " + port);
	    try {
			r = DNSSD.register(name, "_keyboardcommands._udp.", port, this);
		} catch (DNSSDException e) {
			
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			
		}
	   }
	  
	    //stop service
	    public void stopService() {
	    	r.stop();
	    }

	   
	  
	

}
