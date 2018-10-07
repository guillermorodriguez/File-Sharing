

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintWriter;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 *	The purpose of this class is to function as a client instance that
 *	sends data by the defined port from the command line and then
 *	accepts a response from the server.
 */
public class producer extends Thread {

  protected String sink;
  protected int port;

  public producer(String sink, int port){
    this.sink = sink;
    this.port = port;

  }

  /**
   *	Main entry point for producer instance
   *	@param	args	command line argument parameters
   */
  public static void main(String[] args){
    String SINK = "127.0.0.1";
    Socket sock = null;
    PrintWriter writer = null;
    BufferedReader reader = null;
    BufferedReader console = null;

    System.out.println("Client Ready to Communicate .... ");
    System.out.println("Print quit() to terminate.");

    /*
        Handle Input Validation
    */
    String expression_port = "\\d+";
  	if( args.length != 2 ) {
      System.out.println("Fatal Exception. Invalid Input.");
  		System.out.println("First argument must be sink value");
      System.out.println("Second argument must be port value");

      System.exit(1);
    }
    else if( !args[0].equals(SINK) ){
      System.out.println("Sink Value Invalid");
      System.exit(1);
    }
    else if( !args[1].matches(expression_port) ){
      System.out.println("Port Value Invalid");
      System.exit(1);
    }

    /*
        Set System Input / Output Configuration
    */
    try{
      sock = new Socket(args[0], Integer.parseInt(args[1]));
      writer = new PrintWriter(sock.getOutputStream(), true);
      reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));

      console = new BufferedReader(new InputStreamReader(System.in));
    }
    catch(UnknownHostException ex){
      System.out.println("Unknown Host");
      System.exit(1);
    }
    catch(IOException ex){
      System.out.println("Input / Output Exception");
      System.exit(1);
    }

    /*
        Manage data streams
    */
    try{
      while(true){
          System.out.print("> ");
          String input = console.readLine();
          if( input.equals("quit()")) {
            System.out.println("Terminated ...");
            break;
          }

          writer.println(input);
          System.out.println("Response: " + reader.readLine());
      }

      writer.close();
      reader.close();
      console.close();

      sock.close();
    }
    catch(Exception ex){
      System.out.println("Error Processing Client");
    }
  }
}
