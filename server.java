
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
public class server extends Thread {


  protected Socket sock = null;
  protected int max_threads = 0;
  protected HashMap map = null;
  protected int thread_count = 0;
  protected Semaphore gate_keeper = null;
  protected HashMap<Integer, String> users = null;

  private server(Socket sock, int max_threads){
  	 System.out.println("Peer Connection Established");

	   this.sock = sock;
     this.max_threads = max_threads;
     this.thread_count += 1;
     this.gate_keeper = new Semaphore(max_threads, true);
     this.users = new HashMap<Integer, String>();

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

          // Parse Input Stream
          output.write(data.getBytes());
          String[] data_stream = data.split(":");
          System.out.println("Processing Command: " + data_stream[0]);

          if( data_stream[0] == "Login" ){
            String[] command_detail = data_stream[1].split(",");
            this.users.put(Integer.parseInt(command_detail[0]), command_detail[1]);
            System.out.println(this.users);
          }


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

  	int port = 9000;
    int threads = 1;

		System.out.println("Server Listening on Port: " + Integer.toString(port) );
    System.out.println("Thread Count Maximum: " + Integer.toString(threads));


    ServerSocket listener = null;

  	try{
  		listener = new ServerSocket(port);
      while(true){
        // Instantiate Connection Pools
        new server(listener.accept(), threads);
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
