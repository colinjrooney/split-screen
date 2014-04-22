package edu.virginia.splitscreen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class FileServerAsyncTask extends AsyncTask<Object,Object,String>{
	
	private Context context;
	
	public FileServerAsyncTask(Context c){
		context = c;
	}
	
	@Override
	protected String doInBackground(Object... params) {
		Log.d("Splitscreen", "Background");
		ServerSocket serverSocket;
		try {
			
			//Create server socket and wait for connection
			serverSocket = new ServerSocket(8666);
			Log.d("Splitscreen","Waiting for client");
			Socket client = serverSocket.accept();
			
			final File f = new File(Environment.getExternalStorageDirectory()+"/DCIM/"+System.currentTimeMillis()+".jpg");
			
			
			File dirs = new File(f.getParent());
			
			if(!dirs.exists())
				dirs.mkdirs();
			
			PrintWriter write = new PrintWriter(client.getOutputStream());
			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			
			String message;
			message = in.readLine();
			
			serverSocket.close();
			Log.d("Splitscreen","Socket closed"+f.getAbsolutePath());
			return(message);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.d("Splitscreen",e.toString());
		}
		
		
		return null;
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
}
