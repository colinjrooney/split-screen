package edu.virginia.splitscreen;


import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

public class MainActivity extends Activity 
{
	WiFiDirectBroadcastReceiver wifiDirectBroadcastReceiver;
	WifiP2pManager wifiP2pManager;
	Channel channel;
	IntentFilter intentFilter;
	List<WifiP2pDevice> listOfP2pDevices = new ArrayList<WifiP2pDevice>();
	String macAddress;
	public static MainActivity mainActivity;
	List<WifiP2pDevice> listOfDevicesToConnect = new ArrayList<WifiP2pDevice>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		wifiP2pManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
		channel = wifiP2pManager.initialize(this, getMainLooper(), null);
		wifiDirectBroadcastReceiver = new WiFiDirectBroadcastReceiver(wifiP2pManager, channel,this);
		
		intentFilter = new IntentFilter();
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
		macAddress = ((WifiManager)getSystemService(Context.WIFI_SERVICE)).getConnectionInfo().getMacAddress();
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		mainActivity = this;
	}
	public void onButtonHost(View v)
	{
		Intent intent = new Intent(this, HostActivity.class);
		startActivity(intent);
	}
	public void onButtonSetup(View v)
	{
		wifiP2pManager.createGroup(channel, new WifiP2pManager.ActionListener() 
		{
			@Override
			public void onSuccess() 
			{
				startActivityForResult(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS),0);
			}
			@Override
			public void onFailure(int reason) {Log.d("SPLITSCREEN", "NO GROUP");}
		});
	}
	public void onButtonReceive(View v)
	{
		Intent intent = new Intent(this, ReceiveActivity.class);
		startActivity(intent);
	}
	public void onButtonSetupR(View v)
	{
		startActivityForResult(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS),0);
	}
	@Override
	protected void onResume(){
		super.onResume();
		wifiP2pManager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
			
			@Override
			public void onSuccess() {
				Log.i("Splitscreen","P2P set up");
				wifiDirectBroadcastReceiver.refresh();
			}
			
			@Override
			public void onFailure(int reason) {
				// TODO Auto-generated method stub
				Log.i("Splitscreen","No peers found.");
			}
		});
		
		registerReceiver(wifiDirectBroadcastReceiver, intentFilter);
	}
	
	@SuppressLint("NewApi")
	//STOPPEERDISCOVERY REQUIRES API 16
	@Override
	protected void onPause(){
		super.onPause();
		wifiP2pManager.stopPeerDiscovery(channel, new WifiP2pManager.ActionListener() {
			
			@Override
			public void onSuccess() {
				
				
			}
			
			@Override
			public void onFailure(int reason) {
				// TODO Auto-generated method stub
			}
		});
		unregisterReceiver(wifiDirectBroadcastReceiver);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
 
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
