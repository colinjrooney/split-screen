package edu.virginia.splitscreen;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

public class PlayActivity extends Activity
{
	MainActivity main;
	//    Uri path = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.insertdisk);
	private TextureVideoView mTextureVideoView;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		setContentView(R.layout.activity_play);
		Toast.makeText(getApplicationContext(), "Onplay?", Toast.LENGTH_SHORT);
		mTextureVideoView = (TextureVideoView) findViewById(R.id.cropTextureView);
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		Intent i = getIntent();
		int numOfClients = i.getIntExtra("numOfClients", 0);
		int clientNumber = i.getIntExtra("clientNumber", 0);
		int minutes = i.getIntExtra("minutes", 0);
		int seconds = i.getIntExtra("seconds", 0);
		Calendar date = Calendar.getInstance();
		date.set(Calendar.SECOND, seconds);
		date.set(Calendar.MINUTE, minutes);
		String path = i.getStringExtra("path");
		if(path == null)
		{
			try {
				mTextureVideoView.setDataSource(getApplicationContext().getAssets().openFd("sinteltrimmed.mp4"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
		{
			mTextureVideoView.setDataSource(path);
		}
		main = MainActivity.mainActivity;
		Timer timer = new Timer("timer");
		int phonePosition = clientNumber;
		switch (phonePosition) 
		{
			case 0:
				mTextureVideoView.setScaleType(TextureVideoView.ScaleType.SOLO);
				break;
			case 1:
				mTextureVideoView.setScaleType(TextureVideoView.ScaleType.LEFTTWO);
				break;
			case 2:
				mTextureVideoView.setScaleType(TextureVideoView.ScaleType.RIGHTTWO);
				break;
			case 3:
				mTextureVideoView.setScaleType(TextureVideoView.ScaleType.RIGHTPORTRAIT);
				break;
		}
		timer.schedule(new TimerTask() 
		{
			@Override
			public void run() 
			{
				mTextureVideoView.play();
			}
			
		}, date.getTime());
	}
}
