package edu.virginia.splitscreen;

import android.app.Activity;
import android.os.Bundle;

public class HostingActivity extends Activity
{
	MainActivity parent;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		parent = MainActivity.mainActivity;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hosting);
	}
}
