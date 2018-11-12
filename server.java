import java.io.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

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
  private static String SESSION = "./database/users.txt";
  private static String FILES = "./files/";

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
	  PrintWriter output = null;
    BufferedReader reader = null;

  	try{
      input = this.sock.getInputStream();
      output = new PrintWriter(this.sock.getOutputStream(), true);
      reader = new BufferedReader(new InputStreamReader(input));

      String data = "";
      while( ( data = reader.readLine() ) != null ){

        try{
          gate_keeper.acquire();

          String name = Thread.currentThread().getName() + this.thread_count;

          System.out.println("Current Thread: " + name);
          System.out.println("Input Stream: " + data);

          // Parse Input Stream
          String[] data_stream = data.split(":");
          System.out.println("Processing Command: " + data_stream[0]);

          if( data_stream.length == 2 && data_stream[0].equals("Login") ){
            // Login

            String[] command_detail = data_stream[1].split(",");
            this.users.put(Integer.parseInt(command_detail[0]), command_detail[1]);
            System.out.println(this.users);

            // Determine if Session exists
            Boolean session_exists = false;
            String entry = "";

            File session_store = new File(SESSION);
            if (session_store.exists() && !session_store.isDirectory() ){
              BufferedReader sessions = new BufferedReader(new FileReader(SESSION));
              while( (entry = sessions.readLine()) != null ){
                if( entry.equals(command_detail[1]) ){
                  System.out.println("Session Exists!");
                  session_exists = true;
                  break;
                }
              }
            }

            if( !session_exists ){
              // Write User Session to Database.
              BufferedWriter session = new BufferedWriter(new FileWriter(SESSION, true));
              session.write(command_detail[1]+"\n");
              session.close();
            }
          }
          else if( data_stream.length == 3 && data_stream[0].equals("Get") ){
            // Get File
            System.out.println("Peer: " + this.users.get( Integer.parseInt(data_stream[1]) ) );

          }
          else if( data_stream.length == 3 && data_stream[0].equals("Sync") ){
            // Synchronize Content

          }
          else{
            System.out.println("Invalid Command");
          }

          // Provide Server Confirmation of Data Input stream
          output.println("GOOD");
        }
        catch(Exception ex){
          System.out.println("Error Acquiring Thread Lock");

          // Send Error Notification to client
          output.println("BAD");
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
