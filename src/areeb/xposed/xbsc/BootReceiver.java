package areeb.xposed.xbsc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO: This method is called when the BroadcastReceiver is receiving
		// an Intent broadcast.
		
			Log.d("XBSC", "Intent Received");
			
			Intent serviceIntent = new Intent(context, BatteryListenerService.class);
			context.startService(serviceIntent);
			
			Log.d("XBSC", "Service Started");
		
		
	}
}
