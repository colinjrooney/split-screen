package edu.virginia.splitscreen;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	WiFiDirectBroadcastReceiver receiver;
	WifiP2pManager manager;
	Channel channel;
	IntentFilter mIntentFilter;
	ListView deviceList;
	ArrayAdapter<String> adapter;
	List<String> values = new ArrayList<String>();
	Collection<WifiP2pDevice> devices = new ArrayList<WifiP2pDevice>();
	TextView deviceName;
	WifiP2pGroup group;
	Context context;
	FileServerAsyncTask server;
	FileClientAsyncTask client;
	int ipAddress;
	InetAddress leaderAddress;
	boolean connected = false;
	double xInches, yInches;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		context = getApplicationContext();
		
		deviceList = (ListView) (findViewById(R.id.devices));
		adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,values);		
		
		deviceName = (TextView) (findViewById(R.id.deviceName));
		
		
		deviceList.setAdapter(adapter);
		deviceList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		
		manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
		ipAddress = ((WifiManager) getSystemService(Context.WIFI_SERVICE)).getConnectionInfo().getIpAddress();
		
		String ipString = String.format(
			       "%d.%d.%d.%d",
			       (ipAddress & 0xff),
			       (ipAddress >> 8 & 0xff),
			       (ipAddress >> 16 & 0xff),
			       (ipAddress >> 24 & 0xff));
		
		deviceName.setText(((WifiManager) getSystemService(Context.WIFI_SERVICE)).getConnectionInfo().getMacAddress()+"\n"+ipString);
		
		channel = manager.initialize(this, getMainLooper(), null);
		receiver = new WiFiDirectBroadcastReceiver(manager, channel,this);
		
		mIntentFilter = new IntentFilter();
		mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
		mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
		mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
		mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
		
		//if (savedInstanceState == null) {
		//	getFragmentManager().beginTransaction()
		//			.add(R.id.container, new PlaceholderFragment()).commit();
		//}
		
		DisplayMetrics dm = new DisplayMetrics();
	    getWindowManager().getDefaultDisplay().getMetrics(dm);
	    xInches = dm.widthPixels/dm.xdpi;
	    yInches = dm.heightPixels/dm.ydpi;
		
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		startActivityForResult(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS), 0);
	}
	@Override
	protected void onResume(){
		super.onResume();
		/*manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
			
			@Override
			public void onSuccess() {
				Log.i("Splitscreen","P2P set up");
				receiver.refresh();
				
			}
			
			@Override
			public void onFailure(int reason) {
				// TODO Auto-generated method stub
				Log.i("Splitscreen","Failed to set up search");
			}
		});*/
		
		
		registerReceiver(receiver, mIntentFilter);
	}
	
	@SuppressLint("NewApi")
	//STOPPEERDISCOVERY REQUIRES API 16
	@Override
	protected void onPause(){
		super.onPause();
		manager.stopPeerDiscovery(channel, new WifiP2pManager.ActionListener() {
			
			@Override
			public void onSuccess() {
				
				
			}
			
			@Override
			public void onFailure(int reason) {
				// TODO Auto-generated method stub
			}
		});
		unregisterReceiver(receiver);
	}
	
	
	@Override
	protected void onDestroy(){
		Log.d("Splitscreen","DESTROYYYY");
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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
	
	public void refreshListView(String deviceIdentifier){
		//MainActivity.this.runOnUiThread(new Runnable(){
		//	public void run(){
				values.add(deviceIdentifier);
				adapter.notifyDataSetChanged();
		//	}
		//});
	}
	
	public void connect(View v) {
		//Log.d("Splitscreen", "button works");
		
		manager.requestConnectionInfo(channel, new WifiP2pManager.ConnectionInfoListener() {
			
			@Override
			public void onConnectionInfoAvailable(WifiP2pInfo info) {
				leaderAddress = info.groupOwnerAddress;
				
			}
		});
		
		manager.requestGroupInfo(channel, new WifiP2pManager.GroupInfoListener(){

			@Override
			public void onGroupInfoAvailable(WifiP2pGroup g) {
				
				Log.d("Splitscreen",Boolean.toString(g.isGroupOwner()));
				group = g;
				devices =  g.getClientList();
				
				if(group.isGroupOwner()){
					
					server = new FileServerAsyncTask(getApplicationContext());
					server.execute();
				}
			}
			
		});
		//Log.d("Splitscreen", Boolean.toString(group != null));
		//if(group.isGroupOwner()){
		//	FileServerAsyncTask server = new FileServerAsyncTask(getApplicationContext());
		//}
	}
	
	public void clientListen(View v){
		
		manager.requestConnectionInfo(channel, new WifiP2pManager.ConnectionInfoListener() {
			
			@Override
			public void onConnectionInfoAvailable(WifiP2pInfo info) {
				leaderAddress = info.groupOwnerAddress;
				
			}
		});
		
		manager.requestGroupInfo(channel, new WifiP2pManager.GroupInfoListener(){

			@Override
			public void onGroupInfoAvailable(WifiP2pGroup g) {
				//Log.d("Splitscreen",g.toString());
				group = g;
				
				devices = g.getClientList();
				
				client = new FileClientAsyncTask(context,leaderAddress,xInches,yInches);
				client.execute();
			}
			
		});
		
	}
	
	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

}
