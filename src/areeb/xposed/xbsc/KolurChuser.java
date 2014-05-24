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

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.SaturationBar;
import com.larswerkman.holocolorpicker.ValueBar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

@SuppressLint("WorldReadableFiles")
public class KolurChuser extends Activity {

	Bundle mBundle;

	EditText edt;
	EditText edt1;

	EditText edt100;
	EditText edt60;
	EditText edt30;
	EditText edt15;
	

	ImageView colorButton;
	ImageView previewButton;
	ImageView buttonUndo;
	

	

	Button disableButton;
	Button logButton;
	Button applyButton;
	Button buttonApply;

	LinearLayout oldTopLine;
	LinearLayout newTopLine;
	LinearLayout oldGraph;
	LinearLayout newGraph;

	LinearLayout normalLayout;
	RelativeLayout batteryLayout;

	LinearLayout normalTab;
	LinearLayout batteryTab;

	LinearLayout color100;
	LinearLayout color60;
	LinearLayout color30;
	LinearLayout color15;

	

	SeekBar transBar;

	String previColored;
	String nextColored;

	Intent recreateIntent = new Intent("recreate");

	SharedPreferences pref;
	SharedPreferences.Editor edit;

	String ENABLE_BUTTON;
	String DISABLE_BUTTON;
	String LOG_OFF_BUTTON;
	String LOG_ON_BUTTON;
	String TOAST_SUCCESS;
	String TOAST_INVALID;
	String TOAST_ICS;
	String DIAG_TITLE;
	String DIAG_OK;
	String DIAG_CANCEL;

	private static final String PREF_NAME = "color_preference";
	private static final String PREF_COLOR = "color";
	private static final String PREF_TRANS = "transparency";
	private static final String PREF_BATTERY = "battery";
	private static final String PREF_NORMAL = "normal";
	private static final String PREF_FIRST_TIME = "first_time";
	private static final String PREF_ENABLED = "enabled";
	private static final String PREF_LOGGING = "logs";
	private static final String DEF_COLOR = "22cc22";
	private static final String DEF_TRANS = "99";
	private static final String DEF_SELECT_COL = "#aa33b5e5";
	private static final String PREF_BATTERY_GOOD = "battery_good";
	private static final String PREF_BATTERY_OK = "battery_ok";
	private static final String PREF_BATTERY_WARN = "battery_warn";
	private static final String PREF_BATTERY_CRITICAL = "battery_critical";
	private static final String BATTERY_GOOD = "33cc66";
	private static final String BATTERY_OK = "ffc32a";
	private static final String BATTERY_WARN = "ff6a09";
	private static final String BATTERY_CRITICAL = "ff4b4e";
	

	/** Called when the activity is first created. */
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		InputFilter input = new InputFilter() {
			@Override
			public CharSequence filter(CharSequence source, int start, int end,
					Spanned dest, int dstart, int dend) {
				if (source instanceof SpannableStringBuilder) {
					SpannableStringBuilder sourceAsSpannableBuilder = (SpannableStringBuilder) source;
					for (int i = end - 1; i >= start; i--) {
						char currentChar = source.charAt(i);

						String allowed = Character.toString(currentChar);
						if (!allowed.matches("^[a-fA-F0-9]+$")) {
							sourceAsSpannableBuilder.delete(i, i + 1);
						} else if (!Character.isLetterOrDigit(currentChar)
								&& !Character.isSpaceChar(currentChar)) {
							sourceAsSpannableBuilder.delete(i, i + 1);
						}
					}
					return source;
				} else {
					StringBuilder filteredStringBuilder = new StringBuilder();
					for (int i = start; i < end; i++) {
						char currentChar = source.charAt(i);

						String allowed = Character.toString(currentChar);
						if (!allowed.matches("[a-fA-F0-9]+$")) {
							filteredStringBuilder.append(currentChar);
						}

						else if (Character.isLetterOrDigit(currentChar)
								|| Character.isSpaceChar(currentChar)) {

							filteredStringBuilder.append(currentChar);

						}
					}
					return filteredStringBuilder.toString();
				}
			}

		};

		View.OnKeyListener vok = new View.OnKeyListener() {
			public boolean onKey(View view, int keyCode, KeyEvent event) {
				// If the event is a key-down event on the "enter" button
				if ((event.getAction() == KeyEvent.ACTION_DOWN)
						&& (keyCode == KeyEvent.KEYCODE_ENTER)) {
					// Perform action on key press
					preview(view);
					return true;
				}
				return false;
			}
		};

		pref = getSharedPreferences(PREF_NAME, Context.MODE_WORLD_READABLE);
		edit = pref.edit();

		ENABLE_BUTTON = getString(R.string.btn_enable);
		DISABLE_BUTTON = getString(R.string.btn_disable);
		LOG_OFF_BUTTON = getString(R.string.btn_log_off);
		LOG_ON_BUTTON = getString(R.string.btn_log_on);
		TOAST_SUCCESS = getString(R.string.toast_success);
		TOAST_INVALID = getString(R.string.toast_invalid);
		TOAST_ICS = getString(R.string.toast_battery_in);
		DIAG_TITLE = getString(R.string.diag_title);
		DIAG_OK = getString(android.R.string.ok);
		DIAG_CANCEL = getString(android.R.string.cancel);

		String prevColor = pref.getString(PREF_COLOR, DEF_COLOR);
		String prevTrans = pref.getString(PREF_TRANS, DEF_TRANS);
		Boolean enable = pref.getBoolean(PREF_ENABLED, true);
		Boolean log = pref.getBoolean(PREF_LOGGING, false);
		Boolean normal = pref.getBoolean("normal", true);
		Boolean battery = pref.getBoolean(PREF_BATTERY, false);

		String batteryGood = pref.getString(PREF_BATTERY_GOOD, BATTERY_GOOD);
		String batteryOk = pref.getString(PREF_BATTERY_OK, BATTERY_OK);
		String batteryWarn = pref.getString(PREF_BATTERY_WARN, BATTERY_WARN);
		String batteryCritical = pref.getString(PREF_BATTERY_CRITICAL,
				BATTERY_CRITICAL);

		String prevColored = "#" + prevColor;
		String prevTransed = "#" + prevTrans + prevColor;

		previColored = prevColor;

		newTopLine = (LinearLayout) findViewById(R.id.newTopLine);
		newGraph = (LinearLayout) findViewById(R.id.newGraph);

		normalLayout = (LinearLayout) findViewById(R.id.normalLayout);
		batteryLayout = (RelativeLayout) findViewById(R.id.batteryLayout);

		normalTab = (LinearLayout) findViewById(R.id.normalTab);
		batteryTab = (LinearLayout) findViewById(R.id.batteryTab);

		disableButton = (Button) findViewById(R.id.disableButton);
		applyButton = (Button) findViewById(R.id.applyButton);
		buttonApply = (Button) findViewById(R.id.buttonApply);
		
		

		colorButton = (ImageView) findViewById(R.id.colorButton);
		previewButton = (ImageView) findViewById(R.id.previewButton);
		buttonUndo = (ImageView) findViewById(R.id.buttonUndo);
		

		color100 = (LinearLayout) findViewById(R.id.layout100);
		color60 = (LinearLayout) findViewById(R.id.layout60);
		color30 = (LinearLayout) findViewById(R.id.layout30);
		color15 = (LinearLayout) findViewById(R.id.layout15);

		if (normal == true) {

			batteryLayout.setVisibility(View.GONE);
			normalLayout.setVisibility(View.VISIBLE);

			normalTab.setBackgroundColor(Color.parseColor(DEF_SELECT_COL));
			batteryTab.setBackgroundResource(R.drawable.tab_btn);

		}

		if (battery == true) {

			batteryLayout.setVisibility(View.VISIBLE);
			normalLayout.setVisibility(View.GONE);

			normalTab.setBackgroundResource(R.drawable.tab_btn);
			batteryTab.setBackgroundColor(Color.parseColor(DEF_SELECT_COL));

		}
		
		
		

		color100.setBackgroundColor(Color.parseColor("#" + batteryGood));
		color60.setBackgroundColor(Color.parseColor("#" + batteryOk));
		color30.setBackgroundColor(Color.parseColor("#" + batteryWarn));
		color15.setBackgroundColor(Color.parseColor("#" + batteryCritical));

		edt = (EditText) findViewById(R.id.ET1);
		edt.setFilters(new InputFilter[] { input, new InputFilter.LengthFilter(6) });
		edt.setSingleLine();
		edt.setOnKeyListener(vok);

		edt1 = (EditText) findViewById(R.id.ET2);
		edt1.setFilters(new InputFilter[] { input, new InputFilter.LengthFilter(2) });
		edt1.setSingleLine();

		edt100 = (EditText) findViewById(R.id.editText100);
		edt100.setFilters(new InputFilter[] { input, new InputFilter.LengthFilter(6) });
		edt100.setSingleLine();

		edt60 = (EditText) findViewById(R.id.editText60);
		edt60.setFilters(new InputFilter[] { input, new InputFilter.LengthFilter(6) });
		edt1.setSingleLine();

		edt30 = (EditText) findViewById(R.id.editText30);
		edt30.setFilters(new InputFilter[] { input, new InputFilter.LengthFilter(6) });
		edt30.setSingleLine();

		edt15 = (EditText) findViewById(R.id.editText15);
		edt15.setFilters(new InputFilter[] { input, new InputFilter.LengthFilter(6) });
		edt15.setSingleLine();
		
		edt100.setText(batteryGood);
		edt60.setText(batteryOk);
		edt30.setText(batteryWarn);
		edt15.setText(batteryCritical);

		color100.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				batteryColor(1);

			}
		});

		color60.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				batteryColor(2);

			}
		});

		color30.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				batteryColor(3);

			}
		});

		color15.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				batteryColor(4);

			}
		});

		oldTopLine = (LinearLayout) findViewById(R.id.oldTopLine);
		oldTopLine.setBackgroundColor(Color.parseColor(prevColored));

		oldGraph = (LinearLayout) findViewById(R.id.oldGraph);
		oldGraph.setBackgroundColor(Color.parseColor(prevTransed));

		transBar = (SeekBar) findViewById(R.id.seekBar1);

		int transpro = Integer.parseInt(prevTrans, 16);

		transBar.setProgress(transpro);

		transBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				// TODO Auto-generated method stub

				edt1.setText(hexProgress(arg1));

			}
		});

		if (enable == false) {

			disableButton.setText(ENABLE_BUTTON);
			edt.setEnabled(false);
			edt1.setEnabled(false);
			previewButton.setEnabled(false);
			transBar.setEnabled(false);
			applyButton.setEnabled(false);
			colorButton.setEnabled(false);
			buttonApply.setEnabled(false);
			buttonUndo.setEnabled(false);

		}

		if (log == true) {

			logButton.setText(LOG_ON_BUTTON);

		}

	}

	public String hexProgress(int pro) {

		StringBuilder sb = new StringBuilder();
		sb.append(Integer.toHexString(pro));
		if (sb.length() < 2) {
			sb.insert(0, '0'); // pad with leading zero if needed
		}
		String hex = sb.toString();

		return hex;
	}

	public void preview(View view) {

		String gotColor = edt.getText().toString();
		String gotTransparency = edt1.getText().toString();

		if (gotTransparency.equals("")) {

			gotTransparency = DEF_TRANS;

		}

		if (isHex(gotColor, gotTransparency) == true) {

			nextColored = gotColor;

			newTopLine.setBackgroundColor(Color.parseColor("#" + gotColor));

			String transp = "#" + gotTransparency + gotColor;

			newGraph.setBackgroundColor(Color.parseColor(transp));

		} else {

			Toast.makeText(this, TOAST_INVALID, Toast.LENGTH_LONG).show();

		}

	}

	public void btn(View view) {

		String gotColor = edt.getText().toString();

		String gotTransparency = edt1.getText().toString();

		if (gotTransparency.equals("")) {

			gotTransparency = DEF_TRANS;

		}

		if (isHex(gotColor, gotTransparency) == true) {

			applyColor(gotColor, gotTransparency);

		} else {

			Toast.makeText(this, TOAST_INVALID, Toast.LENGTH_SHORT).show();

		}

	}

	public void oldClick(View view) {

		edt.setText(previColored);

	}

	public void newClick(View view) {

		try {

			if (!nextColored.equals("")) {

				edt.setText(nextColored);

			}

		} catch (NullPointerException n) {

			n.printStackTrace();

		}

	}

	

	public void disable(View view) {

		Boolean enable = pref.getBoolean(PREF_ENABLED, true);

		if (enable == true) {

			disableCustom();

		} else {

			enableCustom();

		}

	}

	public void disableCustom() {

		edit.putBoolean(PREF_ENABLED, false);
		edit.apply();
		sendBroadcast(recreateIntent);
		disableButton.setText(ENABLE_BUTTON);

		sendBroadcast(recreateIntent);

		edt.setEnabled(false);
		edt1.setEnabled(false);
		colorButton.setEnabled(false);
		transBar.setEnabled(false);
		previewButton.setEnabled(false);
		applyButton.setEnabled(false);
		buttonApply.setEnabled(false);
		buttonUndo.setEnabled(false);

	}

	public void enableCustom() {

		disableButton.setText(ENABLE_BUTTON);
		edit.putBoolean(PREF_ENABLED, true);
		edit.apply();
		sendBroadcast(recreateIntent);
		disableButton.setText(DISABLE_BUTTON);

		sendBroadcast(recreateIntent);

		edt.setEnabled(true);
		edt1.setEnabled(true);
		colorButton.setEnabled(true);
		transBar.setEnabled(true);
		previewButton.setEnabled(true);
		applyButton.setEnabled(true);
		buttonApply.setEnabled(true);
		buttonUndo.setEnabled(true);

	}

	public void enableNormal(View view) {

		edit.putBoolean(PREF_NORMAL, true);
		edit.putBoolean(PREF_BATTERY, false);
		edit.apply();
		sendBroadcast(recreateIntent);

		sendBroadcast(recreateIntent);

		/*
		 * edt.setEnabled(true); edt1.setEnabled(true);
		 * colorButton.setEnabled(true); previewButton.setEnabled(true);
		 * applyButton.setEnabled(true);
		 */

		normalTab.setBackgroundColor(Color.parseColor(DEF_SELECT_COL));
		batteryTab.setBackgroundResource(R.drawable.tab_btn);

		normalLayout.setVisibility(View.VISIBLE);
		batteryLayout.setVisibility(View.GONE);

	}

	public void enableBattery(View view) {
		
		Boolean first = pref.getBoolean(PREF_FIRST_TIME, true);
		
		if (first == true){
			
			Toast.makeText(this, TOAST_ICS, Toast.LENGTH_SHORT).show();
			edit.putBoolean("first_time", false);
			
		}
		

		edit.putBoolean(PREF_BATTERY, true);
		edit.putBoolean(PREF_NORMAL, false);
		edit.apply();
		sendBroadcast(recreateIntent);

		normalLayout.setVisibility(View.GONE);
		batteryLayout.setVisibility(View.VISIBLE);
		sendBroadcast(recreateIntent);

		normalTab.setBackgroundResource(R.drawable.tab_btn);
		batteryTab.setBackgroundColor(Color.parseColor(DEF_SELECT_COL));

	}

	public void log(View view) {

		Boolean log = pref.getBoolean(PREF_LOGGING, false);

		if (log == true) {

			edit.putBoolean(PREF_LOGGING, false);
			logButton.setText(LOG_OFF_BUTTON);

		} else {

			edit.putBoolean(PREF_LOGGING, true);
			logButton.setText(LOG_ON_BUTTON);

		}

		edit.apply();

	}

	public void applyColor(String gotColor, String gotTransparency) {

		edit.putString(PREF_COLOR, gotColor);
		edit.putString(PREF_TRANS, gotTransparency);
		edit.apply();

		sendBroadcast(recreateIntent);

		previColored = gotColor;
		nextColored = gotColor;

		newTopLine.setBackgroundColor(Color.parseColor("#" + gotColor));
		oldTopLine.setBackgroundColor(Color.parseColor("#" + gotColor));

		String transp = "#" + gotTransparency + gotColor;

		newGraph.setBackgroundColor(Color.parseColor(transp));
		oldGraph.setBackgroundColor(Color.parseColor(transp));

		sendBroadcast(recreateIntent);

		Toast.makeText(this, TOAST_SUCCESS, Toast.LENGTH_SHORT).show();

	}

	public void colorpicker(View view) {

		LayoutInflater li = this.getLayoutInflater();
		View colorView = li.inflate(R.layout.dialog_layout, null);

		final ColorPicker picker = (ColorPicker) colorView
				.findViewById(R.id.color_picker);
		SaturationBar saturationBar = (SaturationBar) colorView
				.findViewById(R.id.saturationbar);
		ValueBar valueBar = (ValueBar) colorView.findViewById(R.id.valuebar);

		picker.addSaturationBar(saturationBar);
		picker.addValueBar(valueBar);

		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(DIAG_TITLE);
		alert.setIcon(R.drawable.color);
		alert.setView(colorView);
		alert.setPositiveButton(DIAG_OK, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub

				edt.setText(String.format("%06X", (0xFFFFFF & picker.getColor())));

				//Toast.makeText(getApplicationContext(), "Color Passed", Toast.LENGTH_SHORT).show();

			}
		});
		alert.setNegativeButton(DIAG_CANCEL,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub

						arg0.dismiss();

					}
				});
		alert.show();

	}

	public void batteryColor(int i) {

		if (i > 4)
			return;

		final int index = i;

		LayoutInflater li = this.getLayoutInflater();
		View colorView = li.inflate(R.layout.dialog_layout, null);

		final ColorPicker picker = (ColorPicker) colorView.findViewById(R.id.color_picker);
		
		SaturationBar saturationBar = (SaturationBar) colorView.findViewById(R.id.saturationbar);
		ValueBar valueBar = (ValueBar) colorView.findViewById(R.id.valuebar);

		picker.addSaturationBar(saturationBar);
		picker.addValueBar(valueBar);

		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(DIAG_TITLE);
		alert.setIcon(R.drawable.color);
		alert.setView(colorView);
		alert.setPositiveButton(DIAG_OK, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				
				if (index == 1){
				

				edt100.setText(String.format("%06X", (0xFFFFFF & picker.getColor())));
				
				
				color100.setBackgroundColor(picker.getColor());
				
				}
				
				if (index == 2){
					

					edt60.setText(String.format("%06X", (0xFFFFFF & picker.getColor())));
					
					
					color60.setBackgroundColor(picker.getColor());
					
					}
				
				if (index == 3){
					

					edt30.setText(String.format("%06X", (0xFFFFFF & picker.getColor())));
					
					
					color30.setBackgroundColor(picker.getColor());
					
					}
				
				if (index == 4){
					

					edt15.setText(String.format("%06X", (0xFFFFFF & picker.getColor())));
					
					
					color15.setBackgroundColor(picker.getColor());
					
					}

			}
		});
		alert.setNegativeButton(DIAG_CANCEL,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub

						arg0.dismiss();

					}
				});
		alert.show();

	}
	
	public void batteryApply(View view){
		
		String goodColor = edt100.getText().toString();
		String okColor = edt60.getText().toString();
		String warnColor = edt30.getText().toString();
		String criticalColor = edt15.getText().toString();
		
		sendBroadcast(recreateIntent);
		
		if (isHex(goodColor, DEF_TRANS) && isHex(okColor, DEF_TRANS) && isHex(warnColor, DEF_TRANS) && isHex(criticalColor, DEF_TRANS)){
		
		
		edit.putString(PREF_BATTERY_GOOD, goodColor);
		edit.putString(PREF_BATTERY_OK, okColor);
		edit.putString(PREF_BATTERY_WARN, warnColor);
		edit.putString(PREF_BATTERY_CRITICAL, criticalColor);
		edit.apply();

		Toast.makeText(this, TOAST_SUCCESS, Toast.LENGTH_SHORT).show();
		
		sendBroadcast(recreateIntent);
		
		}
		
	}
	
	public void undo(View view){
		
		edt100.setText(BATTERY_GOOD);
		edt60.setText(BATTERY_OK);
		edt30.setText(BATTERY_WARN);
		edt15.setText(BATTERY_CRITICAL);
		
		
		color100.setBackgroundColor(Color.parseColor("#" + BATTERY_GOOD));
		color60.setBackgroundColor(Color.parseColor("#" + BATTERY_OK));
		color30.setBackgroundColor(Color.parseColor("#" + BATTERY_WARN));
		color15.setBackgroundColor(Color.parseColor("#" + BATTERY_CRITICAL));
		
	}
	

	public boolean isHex(String hex, String transparency) {

		if (hex.matches("^[a-fA-F0-9]+$")) {

			if (transparency.matches("^[a-fA-F0-9]+$")) {

				if (hex.length() == 6) {

					if (transparency.length() == 2) {

						return true;

					}

				}

			}

		}

		return false;

	}

	/*
	 * ========Thanks======== (In order of my brain remembering them)
	 * 
	 * rovo89 for this awesome Xposed which allows us to mess with Android.
	 * 
	 * rovo89 again for his development tutorial, and every other help or
	 * resource taken from his works or projects which allowed this module to be
	 * completed.
	 * 
	 * GermainZ for bearing my annoying questions and helping me in
	 * getting rid of my noobish errors.
	 * 
	 * stackoverflow for each and every question that matched mine and each and 
	 * every answer that helped me.
	 * 
	 * Giupy for helping me miscellaneously and testing the module thoroughly.
	 * 
	 * AIDE for providing some time pass on phones too.
	 * 
	 * Gravitybox, Tinted Status Bar, and Xposed App Settings to provide enough 
	 * discouragement to my not so bright brain to make me realize that where I stand.
	 * (JK, Thanks to all Open Source developers)
	 * 
	 * My English note book for reminding me that it would be registerReceiver and not registerReciever
	 * (Yeah, RIP English!).
	 * 
	 * Android Team for adding non-functional APIs since API version 3 and telling to add functionality later.
	 * 
	 * srt99(He crossed his 100th 100 far back though, Sachin Tendulkar),
	 * amogh420(Seldom steals), Giupy99(Nigga Moon), GermainZ(Winking Bomb) for testing the module.
	 *
	 * Jake Wharthon for ActionBarSherlock,  Lars Werkman for HoloColorPicker
	 * 
	 * 
	 * t2107 for Guide on developing modules on AIDE
	 * 
	 * YOU for using it if you do
	 */

}
