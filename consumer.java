
import java.io.BufferedReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *	The purpose of this class is to function as a server instance that
 *	accepts data connections by the defined port from the command 
 *	line and then processes data captured by client.
 */
public class consumer extends Thread {


  protected Socket sock = null;

  private consumer(Socket sock){
  	
  	System.out.println("Server Connection Established");

	this.sock = sock;

	start();
  }

  public void run(){
  
  	InputStream in = null;
	OutputStream out = null;

	try{
	
	
	}
	catch(Exception ex){
		System.out.println("Exception: " + ex.getMessage());
	}
  }


  /**
   *	Main entry point for server instance
   *	@param	args	command line argument parameters
   */
  public static void main(String[] args) {
	
	String expression = "\\d+";

	if( args.length > 0 && args[0].matches(expression) ) {
    		System.out.println("Server Listening on Port: " + args[0] );
		
		ServerSocket listener = null;

		try{
			listener = new ServerSocket(Integer.parseInt(args[0]));


		}
		catch(Exception ex){
			System.out.println("Exception: " + ex.getMessage());
		}
		finally{
			try{
				if( listener != null ){ listener.close(); }
			}
			catch(Exception ex){
				System.out.println("Exception: " + ex.getMessage());
			}
		}

		System.out.println("Server Shut Down");

	}
	else{
		System.out.println("Fatal Exception. Invalid Input.");
		System.out.println("First argument must be integer");
	}
  }
  
}
