package areeb.xposed.xbsc;


import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Toast;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

public class KolurChuser extends SherlockFragmentActivity {
	
	SharedPreferences pref;
	SharedPreferences.Editor edit;
	
	Intent recreateIntent = new Intent("recreate");
	
	private static final String PREF_NAME = "color_preference";
	private static final String PREF_BATTERY = "battery";
	private static final String PREF_NORMAL = "normal";
	private static final String PREF_ENABLED = "enabled";
	private static final String PREF_LOGGING = "logs";
	
	String MODULE_ENABLE;
	String MODULE_DISABLE;
	String LOGS_ENABLE;
	String LOGS_DISABLE;
	String TOAST_ACT;
	String CONTACT;
	String FACEBOOK;
	String XDA;
	String GPLUS;
	String TWITTER;
	
	Boolean enable;
	Boolean log;
	
	


	@SuppressLint({ "WorldReadableFiles", "CommitPrefEdits" })
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.layout_main);
		
		MODULE_ENABLE = getString(R.string.menu_enable_module);
		MODULE_DISABLE = getString(R.string.menu_disable_module);
		LOGS_ENABLE = getString(R.string.menu_enable_logs);
		LOGS_DISABLE = getString(R.string.menu_disable_logs);
		TOAST_ACT = getString(R.string.toast_noactivity);
		CONTACT = getString(R.string.contact);
		FACEBOOK = getString(R.string.facebook);
		XDA = getString(R.string.xda);
		GPLUS = getString(R.string.gplus);
		TWITTER = getString(R.string.twitter);
		
		pref = getSharedPreferences(PREF_NAME, Context.MODE_WORLD_READABLE);
		edit = pref.edit();
		
		Boolean normal = pref.getBoolean(PREF_NORMAL, true);
		Boolean battery = pref.getBoolean(PREF_BATTERY, false);
		
		enable = pref.getBoolean(PREF_ENABLED, true);
		log = pref.getBoolean(PREF_LOGGING, false);
		
		getSupportActionBar().setIcon(getResources().getDrawable(R.drawable.ac_ic));
		
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        ActionBar.Tab tab1 = getSupportActionBar().newTab();
        tab1.setText(getString(R.string.normal));
        tab1.setTabListener(new TabListener());
        getSupportActionBar().addTab(tab1);
        
        ActionBar.Tab tab2 = getSupportActionBar().newTab();
        tab2.setText(getString(R.string.battery));
        tab2.setTabListener(new TabListener());
        getSupportActionBar().addTab(tab2);
		
		if (normal == true) {

			getSupportActionBar().selectTab(tab1);

		}

		if (battery == true) {

			getSupportActionBar().selectTab(tab2);
			
		}
		
		
		
		
		
		super.invalidateOptionsMenu();
        
		
	}
	
	private class TabListener implements ActionBar.TabListener{

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub
			
			switch(tab.getPosition()) {
			case 0:
				NormalFragment frag = new NormalFragment();
				ft.replace(android.R.id.content, frag);
				getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#33b5e5")));
				enableNormal();
				break;
			case 1:
				BatteryFragment frag1 = new BatteryFragment();
				ft.replace(android.R.id.content, frag1);
				getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ff5858")));
				enableBattery();
				break;
			
			}
			
			
		}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub
			
		}
		
		
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		super.onCreateOptionsMenu(menu);
        getSupportMenuInflater().inflate(R.menu.menu, menu);
        return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu){
		super.onPrepareOptionsMenu(menu);
		
		MenuItem moduleMenu = menu.findItem(R.id.toggleModule);
		MenuItem logsMenu = menu.findItem(R.id.toggleLogs);

		enable = pref.getBoolean(PREF_ENABLED, true);
		log = pref.getBoolean(PREF_LOGGING, false);
		
		if (enable == true){
			
			moduleMenu.setIcon(R.drawable.ic_deselect_all);
			moduleMenu.setTitle(MODULE_DISABLE);
			
		} else {
			
			moduleMenu.setIcon(R.drawable.ic_select_all);
			moduleMenu.setTitle(MODULE_ENABLE);
			
		}
		
		if (log == false){
			
			logsMenu.setTitle(LOGS_ENABLE);
			
		} else {
			
			logsMenu.setTitle(LOGS_DISABLE);
			
		}
		
		
		return true;
	}
	
	 @Override
	 public boolean onOptionsItemSelected(MenuItem item) {
	        switch (item.getItemId()) {
	            case R.id.aboutButton:
	            	View view = getLayoutInflater().inflate(R.layout.layout_about, null);
	            	
	            	AlertDialog.Builder dialog = new AlertDialog.Builder(this);
	            	dialog.setView(view);
	            	dialog.show();
	                
	                return true;
	                
	            case R.id.graph:
	            	Intent intent = new Intent(Intent.ACTION_MAIN);
	        		intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.fuelgauge.PowerUsageSummary"));
	    			
	    			try {
	    				startActivity(intent);
	    			} catch (Exception e) {
	    				
	    				Toast.makeText(this, "Redirection to Battery Graph failed", Toast.LENGTH_SHORT).show();
	    				
	    				AlertDialog.Builder alert= new AlertDialog.Builder(this);
	    				alert.setMessage(e.getMessage());
	    				alert.setTitle("Error");
	    				alert.show();
	    			}
	    			return true;
	    			
	            case R.id.toggleModule:
	            	toggle(item);
	            	return true;
	            	
	            case R.id.toggleLogs:
	            	log(item);
	            	return true;
	            	
	            default:
	                return super.onOptionsItemSelected(item);
	        }
	    }
	
	public void enableNormal() {

		edit.putBoolean(PREF_NORMAL, true);
		edit.putBoolean(PREF_BATTERY, false);
		edit.apply();
		
		sendBroadcast(recreateIntent);

		sendBroadcast(recreateIntent);


	}
	
	public void enableBattery() {
		
		

		edit.putBoolean(PREF_BATTERY, true);
		edit.putBoolean(PREF_NORMAL, false);
		edit.apply();
		sendBroadcast(recreateIntent);

		
		sendBroadcast(recreateIntent);

		

	}
	
	public void log(MenuItem item) {

		Boolean log = pref.getBoolean(PREF_LOGGING, false);

		if (log == true) {

			edit.putBoolean(PREF_LOGGING, false);
			item.setTitle(LOGS_ENABLE);

		} else {

			edit.putBoolean(PREF_LOGGING, true);
			item.setTitle(LOGS_DISABLE);
			
		}

		edit.apply();

	}
	
	public void toggle(MenuItem item) {

		Boolean enable = pref.getBoolean(PREF_ENABLED, true);

		if (enable == true) {
			
			edit.putBoolean(PREF_ENABLED, false);
			edit.apply();

			item.setIcon(getResources().getDrawable(R.drawable.ic_select_all));
			item.setTitle(MODULE_ENABLE);
			
			Intent intent = new Intent("xbsc_control");
			sendBroadcast(intent);
			

		} else {
			
			edit.putBoolean(PREF_ENABLED, true);
			edit.apply();
			
			item.setIcon(getResources().getDrawable(R.drawable.ic_deselect_all));
			item.setTitle(MODULE_DISABLE);
			
			Intent intent = new Intent("xbsc_control");
			sendBroadcast(intent);

		}

	}
	
	
	
	/*
	 * 
	 * Dialog redirects
	 * 
	 */

	
	public void iamareebjamal(View view){
		
		String[] choices = {FACEBOOK, XDA, GPLUS, TWITTER};


		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(CONTACT);
		builder.setItems(choices , new DialogInterface.OnClickListener(){


				public void onClick(DialogInterface dialog , int which)
				{
					
					String string = null;

					switch (which)
					{

						case (0) :
							string = "http://www.facebook.com/iamareebjamal";
							break;
						case (1) :
							string = "http://forum.xda-developers.com/member.php?u=4782403";
							break;
						case (2) :
							string = "http://plus.google.com/+areebjamaiam";
							break;
						case (3) :
							string = "http://www.twitter.com/iamareebjamal";
							break;


					}
					
					if (!string.equals(null))
					{
						go(string);
					}
				}
			});
		builder.show();
		
	}
		
	
	public void xda(View view){
		
		go("http://forum.xda-developers.com/xposed/modules/xposed-x-battery-stats-colors-change-t2761575");
		
	}
	
	public void xposed(View view){
		
		go("http://repo.xposed.info/module/areeb.xposed.xbsc");
		
	}

	
	public void github(View view){
		
		go("https://github.com/iamareebjamal/xbsc_xposed");
		
	}
	

	public void go(String url){
		
		Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(url));
		
		try {
			
			startActivity(intent);
			
			} catch (ActivityNotFoundException e){
				
				Toast.makeText(this, TOAST_ACT, Toast.LENGTH_SHORT).show();
				e.printStackTrace();
				
			}
		
	}

	

}
