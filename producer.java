import java.io.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintWriter;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import java.util.concurrent.TimeUnit;

/**
 *	The purpose of this class is to function as a client instance that
 *	sends data by the defined port from the command line and then
 *	accepts a response from the server.
 */
public class producer extends Thread {

  protected String sink;
  protected int port;

  public producer(String sink, int port){
    System.out.println("Client Ready to Communicate .... ");

    this.sink = sink;
    this.port = port;

    start();
  }

  public void run(){
    Socket sock = null;
    PrintWriter writer = null;
    BufferedReader reader = null;

    /*
        Set System Input / Output Configuration
    */
    try{
      sock = new Socket(this.sink, this.port);
      writer = new PrintWriter(sock.getOutputStream(), true);
      reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
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

      long _start = System.nanoTime();
      String line = "";
      BufferedReader file_reader = new BufferedReader(new FileReader("./data/input"));
      while( (line = file_reader.readLine()) != null ){
        System.out.println(">> " +line);
        writer.println(line);
        System.out.println("<< " + reader.readLine());
      }
      long _end = System.nanoTime() - _start;
      System.out.println("Processing Time " +  TimeUnit.NANOSECONDS.toSeconds(_end) + "s");

      file_reader.close();

      writer.close();
      reader.close();

      sock.close();
    }
    catch(Exception ex){
      System.out.println("Error Processing Client");
    }
  }

  /**
   *	Main entry point for producer instance
   *	@param	args	command line argument parameters
   */
  public static void main(String[] args){
    String SINK = "127.0.0.1";

    /*
        Handle Input Validation
    */
    String expression_numeric = "\\d+";
  	if( args.length != 3 ) {
      System.out.println("Fatal Exception. Invalid Input.");
  		System.out.println("First argument must be sink value");
      System.out.println("Second argument must be port value");
      System.out.println("Third argument must specify thread count");

      System.exit(1);
    }
    else if( !args[0].equals(SINK) ){
      System.out.println("Sink Value Invalid");
      System.exit(1);
    }
    else if( !args[1].matches(expression_numeric) ){
      System.out.println("Port Value Invalid");
      System.exit(1);
    }
    else if( !args[2].matches(expression_numeric) ){
      System.out.println("Thread Value Invalid");
      System.exit(1);
    }

    for( int counter = 0; counter < Integer.parseInt(args[2]);  counter++){
      new producer(args[0], Integer.parseInt(args[1]));
    }


  }
}
