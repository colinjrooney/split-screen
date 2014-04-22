package edu.virginia.splitscreen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class FileClientAsyncTask extends AsyncTask{
	
	private Context context;
	private InetAddress address;
	
	public FileClientAsyncTask(Context c,InetAddress a){
		context = c;
		address = a;
	}
	
	//String host = "84:7a:88:50:38:7b";
	int port = 8666;
	int len;
	
	@Override
	protected String doInBackground(Object... params) {
		Socket socket = new Socket();
		byte buf[] = new byte[1024];
		try{
			Log.d("Splitscreen", "Trying connection");
			socket.bind(null);
			
			socket.connect(new InetSocketAddress(address,port), 500);
			Log.d("Splitscreen","Connected");
			PrintWriter write = new PrintWriter(socket.getOutputStream(),true);
			ContentResolver cr = context.getContentResolver();
			InputStream inputStream = null;
			BufferedReader in =new BufferedReader(new InputStreamReader(socket.getInputStream()));
			//File file = new File("/storage/sdcard0/DCIM/heavybreathing.jpg");
			//Log.d("Splitscreen",Boolean.toString(file.exists()));
			//inputStream = context.getAssets().open("heavybreathing.jpg");//Uri.parse("/storage/emulated/0/DCIM/heavybreathing.jpg"));
			
			//while((len = inputStream.read(buf)) != -1){
			//	outputStream.write(buf, 0, len);
			//}
			
			write.append("Client message");
			
			Log.d("Splitscreen","Data transferred!");
			write.close();
			inputStream.close();
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
	
	protected void onPostExecute(String result){
		if(result != null){
			Intent intent = new Intent();
            intent.setAction(android.content.Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse("file://" + result), "image/*");
            context.startActivity(intent);
		}
	}
}
