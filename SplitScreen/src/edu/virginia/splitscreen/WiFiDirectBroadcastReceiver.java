package edu.virginia.splitscreen;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.util.Log;

public class WiFiDirectBroadcastReceiver  extends BroadcastReceiver{
	
	private WifiP2pManager mManager;
	private Channel mChannel;
	private MainActivity mActivity;
	private WifiP2pDeviceList devices;
	
	private List peers = new ArrayList<WifiP2pDevice>();
	
	private PeerListListener mPeerListListener = new PeerListListener(){
		@Override
		public void onPeersAvailable(WifiP2pDeviceList peerList){
			peers.clear();
			peers.addAll(peerList.getDeviceList());
			
			//((WiFiPeerListAdapter) getListAdapter()).notifyDataSetChanged();
			if(peers.size() == 0){
				Log.d("Splitscreen", "No devices found.");
			}
		}
	};
	
	
	public WiFiDirectBroadcastReceiver(WifiP2pManager manager, Channel channel, MainActivity activity){
		super();
		this.mManager = manager;
		this.mChannel = channel;
		this.mActivity = activity;
	}
	
	@Override
	public void onReceive(Context context, Intent intent){
		String action = intent.getAction();
		
		if(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)){
			int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
			if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED){
				//Log.i("Splitscreen","WIIIIFIII");
			} else {
				//Log.i("Splitscreen","No wifi.");
			}
		} else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)){
			// call requestpeers() here to get current peers
			refresh();
			
		} else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)){
			// respond to new connection/disconnections
		} else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)){
			// respond to wifi state changes
		}
	}
	public void refresh()
	{
//		Log.i("Splitscreen", "P2P PEERS CHANGED");
		if(mManager != null){
			mManager.requestPeers(mChannel, mPeerListListener);
			String devices = "";
			WifiP2pDevice curDevice;
			mActivity.values.clear();
			for(int i = 0; i < peers.size(); i++){
				curDevice = (WifiP2pDevice) (peers.get(i));
				devices = curDevice.deviceAddress;
//				Log.d("Splitscreen",devices);
				//mActivity.values.add(devices);
				mActivity.devices.add(curDevice);
				mActivity.refreshListView(devices);
				//mActivity.deviceList.setAdapter(mActivity.adapter);
			}
		}
	}
	
	
}
