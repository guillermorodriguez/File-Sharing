
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import java.net.ServerSocket;
import java.net.Socket;

import java.util.*;
import java.util.concurrent.Semaphore;

/**
 *	The purpose of this class is to function as a server instance that
 *	accepts data connections by the defined port from the command
 *	line and then processes data captured by client.
 */
public class consumer extends Thread {


  protected Socket sock = null;
  protected int max_threads = 0;
  protected HashMap map = null;
  protected int thread_count = 0;
  protected Semaphore gate_keeper = null;

  private consumer(Socket sock, int max_threads){
  	 System.out.println("Server Connection Established");

	   this.sock = sock;
     this.max_threads = max_threads;
     this.map = new HashMap();
     this.thread_count += 1;
     this.gate_keeper = new Semaphore(max_threads, true);

	   start();
  }

  public void run(){

  	InputStream input = null;
	  OutputStream output = null;
    BufferedReader reader = null;

  	try{
      input = this.sock.getInputStream();
      output = this.sock.getOutputStream();
      reader = new BufferedReader(new InputStreamReader(input));

      String data = "";
      while( ( data = reader.readLine() ) != null ){

        try{
          gate_keeper.acquire();

          String name = Thread.currentThread().getName() + this.thread_count;

          System.out.println("Current Thread: " + name);
          System.out.println("Input Stream: " + data);

          this.map.put(name, data);

          output.write((data+'\n').getBytes());

          System.out.println(this.map);
        }
        catch(Exception ex){
          System.out.println("Error Acquiring Thread Lock");
        }
        finally{
          gate_keeper.release();
        }
      }

  	}
  	catch(Exception ex){
  		System.out.println("Exception: " + ex.getMessage());
  	}
    finally{
      try{
        input.close();
        output.close();

        sock.close();
      }
      catch(Exception ex){
        System.out.println("Error With Object Scrubbing");
      }
    }
  }

  /**
   *	Main entry point for consumer instance
   *	@param	args	command line argument parameters
   */
  public static void main(String[] args) {

  	String expression = "\\d+";

  	if( args.length != 2 ) {
      System.out.println("Fatal Exception. Invalid Input.");
  		System.out.println("First argument must be integer for port");
      System.out.println("Second argument must be integer for max thread count");

      System.exit(1);
    }
    else if( !args[0].matches(expression) ){
      System.out.println("Port Value Invalid");
      System.exit(1);
    }
    else if( !args[1].matches(expression) ){
      System.out.println("Thread Count Value Invalid");
      System.exit(1);
    }

		System.out.println("Server Listening on Port: " + args[0] );
    System.out.println("Thread Count Maximum: " + args[1]);


    ServerSocket listener = null;

  	try{
  		listener = new ServerSocket(Integer.parseInt(args[0]));
      while(true){
        // Instantiate Connection Pools
        new consumer(listener.accept(), Integer.parseInt(args[1]));
      }

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

}
