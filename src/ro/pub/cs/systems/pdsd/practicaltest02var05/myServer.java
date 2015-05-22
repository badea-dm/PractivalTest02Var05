package ro.pub.cs.systems.pdsd.practicaltest02var05;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
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
		
		private Integer getTime() throws IOException{
			Integer time = 0;
			
			URL url = new URL("http://www.timeapi.org/utc/now");
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			
			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
			
			String inputLine;
			StringBuffer response = new StringBuffer();
	 
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			
			String[] timeS = inputLine.split("- T:+");
			
			time = Integer.parseInt(timeS[3]) * 3600 + Integer.parseInt(timeS[4]) * 60 + Integer.parseInt(timeS[5]); 
			
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
	      String response = null;
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
		    	  
		    	  parts = command.split(",\n");
		    	  
		    	  if (parts[0].compareTo("get") == 0){
		    		  if (myMap.containsKey(parts[1])){
		    			  try {
							Integer now = getTime();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		    			  Integer last = myMap.get(parts[1]).time;
		    			  
		    			  if (now - last > 60){
		    				  response = new String("none");
		    			  } else {
		    				  response = new String(myMap.get(parts[1]).value);
		    			  }
		    		  } else {
		    			  response = new String("none\n");
		    		  }
		    	  } else if (parts[0].compareTo("put") == 0){
		    		  try {
						whatever.time = getTime();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		    		  whatever.value = parts[2];
		    		  
		    		  if (myMap.containsKey(parts[1])){
		    			  response = new String("modified\n");
		    		  } else {
		    			  response = new String("inserted\n");
		    		  }
		    		  myMap.put(parts[1], whatever);
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
