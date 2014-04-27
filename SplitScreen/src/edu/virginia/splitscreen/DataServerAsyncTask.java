package edu.virginia.splitscreen;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

public class DataServerAsyncTask extends AsyncTask<Object,String,String>{
	
	int minutes;
	int seconds;
	HostActivity activity;
	private Context context;
	private int clients;
	Calendar date;
	private HashMap<String, String> resolutionMap = new HashMap<String, String>();
	public DataServerAsyncTask(HostActivity activity, Context c, int cl){
		context = c;
		clients = cl;
		this.activity = activity;
	}
	
	@Override
	protected String doInBackground(Object... params) {
		Log.d("Splitscreen", "Background");
		ServerSocket serverSocket;
		try {
			
			//Create server socket and wait for connection
			serverSocket = new ServerSocket(8667);
			Log.d("Splitscreen","Waiting for client");
			
			int counter = 0;
			while (counter++ != clients) {
				Socket client = serverSocket.accept();
				
//				
				Log.d("Splitscreen","Connected to a client");
				PrintWriter write = new PrintWriter(client.getOutputStream());
				BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
				Log.d("Splitscreen","write to stream");
				
				//Gets the value that was immediately sent by the client
				resolutionMap.put(client.getInetAddress().toString(), in.readLine());
				
				
				//This is what is sends in return
				date = Calendar.getInstance();
				date.add(Calendar.SECOND,10);
				seconds = date.get(Calendar.SECOND);
				minutes = date.get(Calendar.MINUTE);
				write.println((clients + 1) + "," + (counter + 1) + "," + minutes + "," + seconds);
				write.flush();
				Log.d("Splitscreen", "" + resolutionMap.toString());
				
				write.close();
				in.close();
			}
			serverSocket.close();
			Log.d("Splitscreen","Socket closed");
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.d("Splitscreen",e.toString());
			return "failure";
		}
		
		
		return "success";
	}
	@Override
	protected void onPostExecute(String result){
		Log.d("Splitscreen",result);
		/*if(result != null){
			Intent intent = new Intent();
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(android.content.Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse("file://" + result), "image/*");
            context.startActivity(intent);
		}*/
		Intent intent = new Intent(activity, PlayActivity.class);
		intent.putExtra("numOfClients", clients + 1);
		intent.putExtra("clientNumber", 1);
		intent.putExtra("minutes", minutes);
		intent.putExtra("seconds", seconds);
		activity.startActivity(intent);
	}
	
	protected void onProgressUpdate(String s){
		Toast.makeText(context, s, Toast.LENGTH_SHORT);
	}
}