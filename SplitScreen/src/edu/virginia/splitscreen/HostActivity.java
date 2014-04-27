package edu.virginia.splitscreen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.app.Activity;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class HostActivity extends Activity
{
	HostActivity activity;
	ListView deviceList;
	ArrayAdapter<String> adapter;
	List<String> names = new ArrayList<String>();
	Collection<WifiP2pDevice> localListOfP2pDevices = new ArrayList<WifiP2pDevice>();
	MainActivity parent;
	WifiP2pGroup group;
	FileServerAsyncTask server;
	DataServerAsyncTask dataServer;
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		activity = this;
		parent = MainActivity.mainActivity;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_host);
		
		/*
		deviceList = (ListView) (findViewById(R.id.devices));
		adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice, names);
		localListOfP2pDevices = parent.listOfP2pDevices;
		names.clear();
		for(WifiP2pDevice curDevice: localListOfP2pDevices)
		{
			names.add(curDevice.deviceAddress);
		}
		deviceList.setAdapter(adapter);
		deviceList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		*/
		
		parent.wifiP2pManager.requestGroupInfo(parent.channel, new WifiP2pManager.GroupInfoListener() 
		{
			@Override
			public void onGroupInfoAvailable(WifiP2pGroup g) 
			{
				group = g;
				localListOfP2pDevices = group.getClientList();
				if(group.isGroupOwner())
				{
					server = new FileServerAsyncTask(getApplicationContext(), group.getClientList().size());
					dataServer = new DataServerAsyncTask(activity, getApplicationContext(), group.getClientList().size());
					Log.d("Screensplit", group.getClientList().size() + " client(s)");
					server.execute();
					dataServer.execute();
				}
			}
		});
	}
	public void onChooseFilePress(View v)
	{
		
		/*
		parent.listOfDevicesToConnect.clear();
		SparseBooleanArray checkedListViewArray = deviceList.getCheckedItemPositions();
		for (int i=0; i<checkedListViewArray.size(); i++)
		{
			if (checkedListViewArray.valueAt(i))
			{
				parent.listOfDevicesToConnect.add(localListOfP2pDevices.get(i));
			}
		}
		Intent intent2 = new Intent(this, PlayActivity.class);
		startActivity(intent2);
		*/
		
	}
}
