package edu.virginia.splitscreen;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Calendar;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

public class DataClientAsyncTask extends AsyncTask<Object,Object,String>
{
	ReceiveActivity activity;
	Calendar date;
	int numOfClients;
	int clientNumber;
	private Context context;
	private InetAddress address;
	private double xInches, yInches;
	int minutes;
	int seconds;
	
	
	public DataClientAsyncTask(ReceiveActivity ac, Context c, InetAddress a, double x, double y){
		context = c;
		address = a;
		xInches = x;
		yInches = y;
		activity = ac;
	}
	
	//String host = "84:7a:88:50:38:7b";
	int port = 8667;
	int len;
	
	@Override
	protected String doInBackground(Object... params) {
		Socket socket = new Socket();
		try{
			Log.d("Splitscreen", "Trying connection");
			socket.bind(null);
			
			socket.connect(new InetSocketAddress(address,port), 500);
			Log.d("Splitscreen","Connected");
			
			
			PrintWriter write = new PrintWriter(socket.getOutputStream(),true);
			ContentResolver cr = context.getContentResolver();
			BufferedReader in =new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			//This writes to the server to start with
			//So far: Width_In_Inches,Height_In_Inches
			//Needs a time element
			write.println(Double.toString(xInches)+","+Double.toString(yInches));
			write.flush();
			
			String message;
			message = in.readLine();
			Log.d("Splitscreen", "Message: " + message);
			
			String[] values = message.split(",");
			numOfClients = Integer.parseInt(values[0]);
			if((Integer)numOfClients == null)
			{
				Log.d("Splitscreen", "YOU FUCKED UP");
			}
			clientNumber = Integer.parseInt(values[1]);
			minutes = Integer.parseInt(values[2]);
			seconds = Integer.parseInt(values[3]);
			date = Calendar.getInstance();
			date.set(Calendar.SECOND, seconds);
			date.set(Calendar.MINUTE, minutes);
			
			Log.d("Splitscreen","Data transferred!");
			
		}
		catch(FileNotFoundException e){
			
		}
		catch(IOException e){
			
		}
		finally{
			if(socket != null){
				if(socket.isConnected()){
					try{
						socket.close();
					}
					catch(IOException e){
						
					}
				}
			}
		}
		
		
		return null;
	}
	
	@Override
	protected void onPostExecute(String result)
	{
		Log.d("Splitscreen","FINISHED BITCH");
		Intent intent = new Intent(activity, PlayActivity.class);
		intent.putExtra("numOfClients", numOfClients);
		intent.putExtra("clientNumber", clientNumber);
		intent.putExtra("minutes", minutes);
		intent.putExtra("seconds", seconds);
		intent.putExtra("path", Environment.getExternalStorageDirectory()+"/DCIM/sinteltrimmed.mp4");
		activity.startActivity(intent);
	}
}
