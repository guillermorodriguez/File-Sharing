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
 *	sends data by the defined port to another peer and to a central indexing
 *  server.
 */
public class peer extends Thread {

  protected String sink = "";
  protected int port = 0;
  protected int id = -1;
  protected String result_file_name = "";
  protected BufferedWriter write = null;
  protected FileWriter file_write = null;
  protected File result = null;
  protected String ip = "";
  protected int server_port = 0;

  public peer(String sink, int port, int id){

    // Variable Instantiations
    this.sink = sink;
    this.port = port;
    this.id = id;
    this.result_file_name = "./" + String.valueOf(id) + "/log/log.log";
    this.ip = "127.0.0.1";
    this.server_port = 9000 + this.id;

    // Set Results Output Stream
    try{
      this.result = new File(this.result_file_name);
      if( !this.result.exists() ){ this.result.createNewFile(); }

      this.file_write = new FileWriter(this.result.getAbsoluteFile());
      this.write = new BufferedWriter(this.file_write);
      this.write.write("Test Results" + '\n');

      this.write.close();
      this.file_write.close();
    }
    catch(IOException ex){
      System.out.println("Error Creating Results File.");
      System.exit(1);
    }

    // Start Client Thread
    start();

    System.out.println("Peer Online .... ");
  }

  // Thread Invocation
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
        Manage data stream to server
    */
    try{

      long _start = System.nanoTime();
      String line = "";
      String input = "";

      writer.println("Login:" + String.valueOf(id) + "," + this.ip + "-" + String.valueOf(this.server_port) );
      System.out.println(">>Commands: Sync, Get");
      System.out.print(">> ");

      BufferedReader command_line = new BufferedReader(new InputStreamReader(System.in));
      while( (input = command_line.readLine()) != null ){
        System.out.println("Processing Command: " + input);

        String parameters = "";
        if (input.equals("Sync")){
          System.out.println(">>Synchronizing Files");
          // Read through directory structure
          for( File _file : (new File("./" + String.valueOf(id) + "/")).listFiles() ){
            if( _file.isFile() ){
              System.out.println("Processing File: " + _file.getName() );
            }
          }

        }
        else if(input.equals("Get")){
          System.out.println(">>File Name: ");
          input = command_line.readLine();
          if( input == null){
            break;
          }



        }

        writer.println(input);

        String server_response = reader.readLine();
        if( server_response == "GOOD"){
          System.out.println("Respnse: GOOD");
        }
        else{
          System.out.println("Response: " + server_response);
        }

        System.out.print(">> ");
      }



      /*
      BufferedReader file_reader = new BufferedReader(new FileReader("./data/input"));
      while( (line = file_reader.readLine()) != null ){
        System.out.println(">> " +line);
        writer.println(line);
        System.out.println("<< " + reader.readLine());
      }

      long _end = System.nanoTime();
      // Write Processing Time Details
      BufferedWriter write = null;
      FileWriter fwrite = null;
      try{
        File _result = new File(result_file_name);
        fwrite = new FileWriter(_result.getAbsoluteFile(), true);
        write = new BufferedWriter(fwrite);
        write.write("Started : " + _start + '\n');
        write.write("Ended   : " + _end + '\n');

        write.close();
        fwrite.close();
      }
      catch(IOException ex){
        System.out.println("Error Creating Results File.");
        System.exit(1);
      }

      file_reader.close();

      writer.close();
      reader.close();
      */

      sock.close();
    }
    catch(Exception ex){
      System.out.println("Error Processing Client Socket");
    }

    /*
        Manage data stream to clients
    */
    try{

    }
    catch(Exception ex){
      System.out.println("Error Processing Server Socket");
    }
  }

  /**
   *	Main entry point for producer instance
   *	@param	args	command line argument parameters
   */
  public static void main(String[] args){
    String SINK = "127.0.0.1";
    int SINK_PORT = 9000;

    /*
        Handle Input Validation
    */
    String expression_numeric = "\\d+";
  	if( args.length != 1 || !args[0].matches(expression_numeric) ) {
      System.out.println("Fatal Exception. Invalid Input.");
  		System.out.println("First Argument Must Be An Id - An Integer Value.");

      System.exit(1);
    }

    // Create Thread
    new peer(SINK, SINK_PORT, Integer.parseInt(args[0]));


  }
}
