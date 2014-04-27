package edu.virginia.splitscreen;

import java.net.InetAddress;

import android.app.Activity;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.Button;

public class ReceiveActivity extends Activity
{
	MainActivity parent;
	ReceiveActivity activity;
	InetAddress leaderAddress;
	FileClientAsyncTask client;
	DataClientAsyncTask dataClient;
	double xInches, yInches;
	public void onCreate(Bundle savedInstanceState) 
	{
		activity = this;
		parent = MainActivity.mainActivity;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_receive);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		xInches = dm.widthPixels/dm.xdpi;
		yInches = dm.heightPixels/dm.ydpi;
		
		parent.wifiP2pManager.requestConnectionInfo(parent.channel, new WifiP2pManager.ConnectionInfoListener()
		{
			@Override
			public void onConnectionInfoAvailable(WifiP2pInfo info)
			{
				leaderAddress = info.groupOwnerAddress;
				client = new FileClientAsyncTask(getApplicationContext(), leaderAddress);
				dataClient = new DataClientAsyncTask(activity, getApplicationContext(), leaderAddress, xInches, yInches);
				client.execute();
				dataClient.execute();
			}
		});
	}
}
