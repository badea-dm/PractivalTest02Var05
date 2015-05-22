package ro.pub.cs.systems.pdsd.practicaltest02var05;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;

import android.util.Log;

public class myServer {
	protected class comThread extends Thread {
		private class Values {
			public String value;
			public Integer time;
		}
		
		private ServerSocket socket;
		private HashMap<String, Values> myMap;
		
		private Integer getTime() throws MalformedURLException{
			Integer time = 0;
			
			URL url = new URL("http://www.timeapi.org/utc/now");
			
			return time;
		}
		 
	    @Override
	    public void run() {
	    	myMap = new HashMap<String, myServer.comThread.Values>();
	      try {
	        socket = new ServerSocket(port);
	      } catch (UnknownHostException unknownHostException) {
	        Log.e("log", "An exception has occurred: "+unknownHostException.getMessage());
	        unknownHostException.printStackTrace();
	      } catch (IOException ioException) {
	        Log.e("log", "An exception has occurred: "+ioException.getMessage());
	        ioException.printStackTrace();
	      }
	      
	      // Actually run the server
	      Socket clSock = null;
	      InputStream commandStr = null;
	      BufferedReader commandBr = null;
	      StringBuilder commandBd = null;
	      String line = null;
	      String command = null;
	      String[] parts;
	      Values whatever = new Values();
	      while (true){
	    	  try {
				clSock = socket.accept();
			} catch (IOException e) {
				Log.e("log", "An exception has occurred: "+e.getMessage());
				e.printStackTrace();
			}
	    	  
	    	  if (clSock != null) {
		    	  try {
					commandStr = clSock.getInputStream();
				} catch (IOException e) {
					Log.e("log", "An exception has occurred: "+e.getMessage());
					e.printStackTrace();
				}
		    	  
		    	  commandBr = new BufferedReader(new InputStreamReader(commandStr));
		    	  commandBd = new StringBuilder();
		    	  try {
					while ((line = commandBr.readLine()) != null) {
							commandBd.append(line);
						}
				} catch (IOException e) {
					Log.e("log", "An exception has occurred: "+e.getMessage());
					e.printStackTrace();
				}
		    	  command = commandBd.toString();
		    	  
		    	  parts = command.split(",");
		    	  
		    	  if (parts[0].compareTo("get") == 0){
		    		  myMap.get(parts[1]);
		    	  } else if (parts[0].compareTo("put") == 0){
		    		  whatever.time = getTime();
		    		  whatever.value = parts[2];
		    	  }
	    	  }
	      }
	    }
	    
	    public void stopServer() throws IOException {
	    	socket.close();
	    }
	 }
	
	private int port = 0;
	comThread servThread;
	
	public myServer(int port) {
		this.port = port;
		
		servThread = new comThread();
		servThread.start();
	}
	
	public void stopServer() throws IOException{
		servThread.stopServer();
	}
}
