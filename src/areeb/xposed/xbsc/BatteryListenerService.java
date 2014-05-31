package areeb.xposed.xbsc;



import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.IBinder;
import android.util.Log;

@SuppressLint("WorldReadableFiles")
public class BatteryListenerService extends Service {
	
	

	private static final String PREF_NAME = "color_preference";
	private static final String PREF_COLOR = "battery_color";
	private static final String PREF_BATTERY_GOOD = "battery_good";
	private static final String PREF_BATTERY_OK = "battery_ok";
	private static final String PREF_BATTERY_WARN = "battery_warn";
	private static final String PREF_BATTERY_CRITICAL = "battery_critical";
	private static final String PREF_BATTERY_LEVEL = "battery_level";
	private static final String BATTERY_GOOD = "33cc66";
	private static final String BATTERY_OK = "ffc32a";
	private static final String BATTERY_WARN = "ff6a09";
	private static final String BATTERY_CRITICAL = "ff4b4e";
	

	@Override
	public IBinder onBind(Intent intent) {
		// TODO: Return the communication channel to the service.
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	public void onDestroy(){
		
		//Toast.makeText(this, "Sevice Terminated", Toast.LENGTH_SHORT).show();
		Log.d("XBSC", "Service onDestroy");
		
	}
	
	@Override
	public void onStart(Intent intent, int startId){
		
		final Context cont = this;
		
		Log.d("XBSC", "Service onStart");
		
		new Thread(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				while(true)
				{
					try {
						Thread.sleep(50000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					String currValue = String.format("%.0f", getBatteryLevel(cont));

					int i = Integer.valueOf(currValue);


					@SuppressWarnings("deprecation")
					SharedPreferences prefI = getSharedPreferences(PREF_NAME, Context.MODE_WORLD_READABLE);
					
					startWork(prefI, i);
				}
				
			}
			
			
			
		}).start();
		
		
		

	}
	
	public void startWork(SharedPreferences prefI, int i){
		
		String batteryGood = prefI.getString(PREF_BATTERY_GOOD, BATTERY_GOOD);
		String batteryOk = prefI.getString(PREF_BATTERY_OK, BATTERY_OK);
		String batteryWarn = prefI.getString(PREF_BATTERY_WARN, BATTERY_WARN);
		String batteryCritical = prefI.getString(PREF_BATTERY_CRITICAL, BATTERY_CRITICAL);

		SharedPreferences.Editor edit = prefI.edit();
		
		edit.putInt(PREF_BATTERY_LEVEL, i);
		
		edit.putString(PREF_COLOR, batteryGood);

		if (30 < i && i <= 60) {

			edit.putString(PREF_COLOR, batteryOk);

		} else if (15 < i && i <= 30) {

			edit.putString(PREF_COLOR, batteryWarn);

		} else if (0 < i && i <= 15) {

			edit.putString(PREF_COLOR, batteryCritical);

		}
		
		

		edit.commit();
		
	}
	
	public float getBatteryLevel(Context cont) {
		Intent batteryIntent = cont.registerReceiver(null, new IntentFilter(
				Intent.ACTION_BATTERY_CHANGED));

		int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

		if (level == -1 || scale == -1) {
			return 50.0f;
		}

		return ((float) level / (float) scale) * 100.0f;
	}
}
