/*
 * Copyright (C) 2014 Areeb Jamal (iamareebjamal)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package areeb.xposed.xbsc;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedBridge.hookAllConstructors;
import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.BatteryManager;
import android.os.Bundle;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

@SuppressLint("DefaultLocale")
public class EggspoachedMod implements IXposedHookLoadPackage {

	Bundle mBundle;

	Activity mActivity;

	Paint mBatteryBackgroundPaint;
	Paint mBatteryGoodPaint;
	Paint mBatteryWarnPaint;
	Paint mBatteryCriticalPaint;
	Paint mChargingPaint;
	Paint mScreenOnPaint;
	Paint mWakeLockPaint;
	Paint mWifiRunningPaint;
	Paint mGpsOnPaint;
	
	int mLeftColor;
	int mMiddleColor;

	Object mPhoneSignalChart;

	String initColor;
	String transInitColor;

	private static final String BITCH_SPELLING_MISTAKE = "registerReceiver";

	private static final String INTENT_NAME = "recreate";
	private static final String L_INTENT = "Intent Received";
	private static final String L_ACTIVITY = "Activity Recreated";
	private static final String L_LOADED = "Loaded Color: ";
	private static final String L_COL_CHANGED = "Colors Changed";
	private static final String L_COMPLETE = "Completed";
	private static final String L_HOOKED = "Hooked ";
	private static final String L_SEC_HOOK = " on Second Hook";
	private static final String PREF_PACKAGE = "areeb.xposed.xbsc";
	private static final String PREF_NAME = "color_preference";
	private static final String PREF_COLOR = "color";
	private static final String PREF_TRANS = "transparency";
	private static final String PREF_ENABLED = "enabled";
	private static final String PREF_BATTERY = "battery";
	private static final String PREF_NORMAL = "normal";
	private static final String PREF_LOGGING = "logs";
	private static final String DEF_COLOR = "22cc88";
	private static final String DEF_TRANS = "39";
	private static final String DEF_ERR_BAR = "#bbf7494b";
	private static final String HOOKED_PACKAGE = "com.android.settings";
	private static final String HOOK_CLASS1 = "com.android.settings.fuelgauge.BatteryHistoryChart";
	private static final String HOOK_CLASS_GRAPH = "com.android.settings.fuelgauge.BatteryHistoryDetail";
	private static final String HOOK_METHOD1 = "onSizeChanged";
	private static final String HOOK_METHOD2 = "onCreate";
	
	
	private static final String PREF_BATTERY_GOOD = "battery_good";
	private static final String PREF_BATTERY_OK = "battery_ok";
	private static final String PREF_BATTERY_WARN = "battery_warn";
	private static final String PREF_BATTERY_CRITICAL = "battery_critical";
	private static final String BATTERY_GOOD = "33cc66";
	private static final String BATTERY_OK = "ffc32a";
	private static final String BATTERY_WARN = "ff6a09";
	private static final String BATTERY_CRITICAL = "ff4b4e";

	BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(INTENT_NAME)) {

				logIt(L_INTENT);

				callMethod(mActivity, HOOK_METHOD2, mBundle);

				logIt(L_ACTIVITY);
			}

		}

	};

	@Override
	public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {

		if (!lpparam.packageName.equals(HOOKED_PACKAGE))
			return;

		final Class<?> classFinder = findClass(HOOK_CLASS1, lpparam.classLoader);
		hookAllConstructors(classFinder, new XC_MethodHook() {
			@Override
			protected void afterHookedMethod(MethodHookParam param)
					throws Throwable {

				// BatteryChartBackground
				mBatteryBackgroundPaint = (Paint) getObjectField(
						param.thisObject, "mBatteryBackgroundPaint");

				// BatteryChargingColor
				mBatteryGoodPaint = (Paint) getObjectField(param.thisObject,
						"mBatteryGoodPaint");

				// BatteryWarnPaint
				mBatteryWarnPaint = (Paint) getObjectField(param.thisObject,
						"mBatteryWarnPaint");

				// BatteryCriticalPaint
				mBatteryCriticalPaint = (Paint) getObjectField(
						param.thisObject, "mBatteryCriticalPaint");

				// ChargingPaint
				mChargingPaint = (Paint) getObjectField(param.thisObject,
						"mChargingPaint");

				// Phone Signal Chart
				mPhoneSignalChart = getObjectField(param.thisObject,
						"mPhoneSignalChart");
				
				Context mContext = (Context) callMethod(
						param.thisObject, "getContext");;

				XSharedPreferences prefI = new XSharedPreferences(PREF_PACKAGE,
						PREF_NAME);

				prefI.reload();

				Boolean enable = prefI.getBoolean(PREF_ENABLED, true);
				Boolean normal = prefI.getBoolean(PREF_NORMAL, true);
				Boolean battery = prefI.getBoolean(PREF_BATTERY, false);

				if (enable == false) {
					return;
				}

				if (normal == true) {
					String color = prefI.getString(PREF_COLOR, DEF_COLOR);
					String transparency = prefI.getString(PREF_TRANS, DEF_TRANS);

					colorGraph(color, transparency);

				}

				if (battery == true) {

					String color = getBatteryColor(mContext, prefI);

					String transparency = prefI.getString(PREF_TRANS, DEF_TRANS);

					colorGraph(color, transparency);


				}

				logIt(L_COMPLETE);

			}
		});

		findAndHookMethod(classFinder, HOOK_METHOD1, int.class, int.class,
				int.class, int.class, new XC_MethodHook() {
					@Override
					protected void afterHookedMethod(MethodHookParam param)
							throws Throwable {

						logIt(L_HOOKED + HOOK_METHOD1);

						// ScreenOn Paint
						mScreenOnPaint = (Paint) getObjectField(param.thisObject, "mScreenOnPaint");

						// Wakelock Paint
						mWakeLockPaint = (Paint) getObjectField(param.thisObject, "mWakeLockPaint");

						// Wifi Running Paint
						mWifiRunningPaint = (Paint) getObjectField(param.thisObject, "mWifiRunningPaint");

						// GPS ON Paint
						mGpsOnPaint = (Paint) getObjectField(param.thisObject, "mGpsOnPaint");
						
						Context mContext = (Context) callMethod(param.thisObject, "getContext");
						

						XSharedPreferences prefI = new XSharedPreferences(PREF_PACKAGE, PREF_NAME);

						prefI.reload();

						Boolean enable = prefI.getBoolean(PREF_ENABLED, true);
						Boolean normal = prefI.getBoolean(PREF_NORMAL, true);
						Boolean battery = prefI.getBoolean(PREF_BATTERY, false);

						if (enable == false)
							return;

						if (normal == true) {

							String color = prefI.getString(PREF_COLOR, DEF_COLOR);

							colorBar(color);

						}

						if (battery == true) {
							
							String color = getBatteryColor(mContext, prefI);

							colorBar(color);

						}

						logIt(L_COMPLETE + L_SEC_HOOK);

					}
				});
		
		
		


		final Class<?> batteryActivityClass = findClass(HOOK_CLASS_GRAPH,
				lpparam.classLoader);
		findAndHookMethod(batteryActivityClass, HOOK_METHOD2, Bundle.class,
				new XC_MethodHook() {
					@Override
					protected void afterHookedMethod(MethodHookParam param)
							throws Throwable {
						mActivity = (Activity) param.thisObject;

						Context mContext = (Context) callMethod(
								param.thisObject, "getApplicationContext");

						ClassLoader mCl = (ClassLoader) callMethod(
								param.thisObject, "getClassLoader");

						mBundle = new Bundle(mCl);

						IntentFilter mFilter = new IntentFilter(INTENT_NAME);

						callMethod(mContext, BITCH_SPELLING_MISTAKE, receiver,
								mFilter);

						// mActivity.getWindow().getDecorView().setBackgroundColor(Color.BLACK);

					}
				});
		


	}

	

	public void logIt(String string) {

		XSharedPreferences prefI = new XSharedPreferences(PREF_PACKAGE,
				PREF_NAME);
		prefI.reload();
		boolean log = prefI.getBoolean(PREF_LOGGING, false);

		if (log == true) {

			XposedBridge.log(string);

		}

	}

	public void colorGraph(String color, String transparency) {

		String initColorI = "#" + color;
		String transInitColorI = "#" + transparency + color;

		int[] colors = new int[] { Color.parseColor("#00" + color),
				Color.parseColor(DEF_ERR_BAR), Color.parseColor("#dd" + color),
				Color.parseColor("#aa" + color),
				Color.parseColor("#99" + color),
				Color.parseColor("#77" + color), Color.parseColor("#" + color) };

		logIt(L_LOADED + color);

		mBatteryBackgroundPaint.setColor(Color.parseColor(transInitColorI));
		mBatteryGoodPaint.setColor(Color.parseColor(initColorI));
		mBatteryWarnPaint.setColor(Color.parseColor(initColorI));
		mBatteryCriticalPaint.setColor(Color.parseColor(initColorI));
		mChargingPaint.setColor(Color.parseColor(initColorI));

		callMethod(mPhoneSignalChart, "setColors", colors);

		logIt(L_COL_CHANGED);

	}

	public void colorBar(String color) {

		String initColorI = "#" + color;

		logIt(L_LOADED + color);

		mScreenOnPaint.setColor(Color.parseColor(initColorI));
		mWakeLockPaint.setColor(Color.parseColor(initColorI));
		mWifiRunningPaint.setColor(Color.parseColor(initColorI));
		mGpsOnPaint.setColor(Color.parseColor(initColorI));

		logIt(L_COL_CHANGED + L_SEC_HOOK);

	}
	
	@SuppressLint("DefaultLocale")
	public String getBatteryColor(Context context, XSharedPreferences prefI){
		
		String currValue = String.format("%.0f", getBatteryLevel(context));

		int i = Integer.valueOf(currValue);
		
		String batteryGood = prefI.getString(PREF_BATTERY_GOOD, BATTERY_GOOD);
		String batteryOk = prefI.getString(PREF_BATTERY_OK, BATTERY_OK);
		String batteryWarn = prefI.getString(PREF_BATTERY_WARN, BATTERY_WARN);
		String batteryCritical = prefI.getString(PREF_BATTERY_CRITICAL, BATTERY_CRITICAL);

		String color = batteryGood;
		
		

		if (30 < i && i <= 60) {

			color = batteryOk;

		} else if (15 < i && i <= 30) {

			color = batteryWarn;

		} else if (0 < i && i <= 15) {

			color = batteryCritical;

		}
		
		
		return color;
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



