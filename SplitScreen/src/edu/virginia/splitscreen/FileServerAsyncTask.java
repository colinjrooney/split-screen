package edu.virginia.splitscreen;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class FileServerAsyncTask extends AsyncTask<Object,String,String>{
	
	private Context context;
	private int clients;
	private HashMap<String, String> resolutionMap = new HashMap<String, String>();
	public FileServerAsyncTask(Context c, int cl){
		context = c;
		clients = cl;
	}
	
	@Override
	protected String doInBackground(Object... params) {
		Log.d("Splitscreen", "Background");
		ServerSocket serverSocket;
		try {
			
			//Create server socket and wait for connection
			serverSocket = new ServerSocket(8666);
			Log.d("Splitscreen","Waiting for client");
			
			int counter = clients;
			while (counter-- != 0) {
				Socket client = serverSocket.accept();
				
//				
				
				
				Log.d("Splitscreen","MAKE-A-DA STREAMS");
				InputStream inputStream = context.getAssets().open("sinteltrimmed.mp4");
				OutputStream oStream = client.getOutputStream();
				int len;
				byte buf[] = new byte[1024];
				Log.d("Splitscreen","Preparing to send video");
				int writes = 0;
				while ((len = inputStream.read(buf)) != -1){
					oStream.write(buf, 0, len);
					oStream.flush();
					writes++;
					if(writes%10 == 0){
						Log.d("Splitscreen",Integer.toString(writes));
					}
				}
				Log.d("Splitscreen",Integer.toString(writes));
//				publishProgress("Finished Loop");
				Log.d("Splitscreen", "Finished loop");
				
				Log.d("Splitscreen","Connected to a client");
//				PrintWriter write = new PrintWriter(client.getOutputStream());
//				BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
//				Log.d("Splitscreen","write to stream");
//				resolutionMap.put(client.getInetAddress().toString(), in.readLine());
//				write.println(clients + "," + counter);
//				write.println("end");
//				write.flush();
//				Log.d("Splitscreen", "" + resolutionMap.toString());
//				
				oStream.close();
				inputStream.close();
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
	}
	
	protected void onProgressUpdate(String s){
		Toast.makeText(context, s, Toast.LENGTH_SHORT);
	}
}