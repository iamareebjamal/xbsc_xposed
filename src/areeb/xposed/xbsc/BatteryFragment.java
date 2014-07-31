package areeb.xposed.xbsc;



import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.SaturationBar;
import com.larswerkman.holocolorpicker.ValueBar;

public class BatteryFragment extends SherlockFragment {
	
	
	Intent recreateIntent = new Intent("recreate");
	
	EditText edt100;
	EditText edt60;
	EditText edt30;
	EditText edt15;
	

	ImageView buttonUndo;
	Button buttonApply;
	
	LinearLayout color100;
	LinearLayout color60;
	LinearLayout color30;
	LinearLayout color15;
	
	String batteryGood;
	String batteryOk;
	String batteryWarn;
	String batteryCritical;
	
	private static final String PREF_NAME = "color_preference";
	private static final String PREF_ENABLED = "enabled";
	private static final String PREF_BATTERY_GOOD = "battery_good";
	private static final String PREF_BATTERY_OK = "battery_ok";
	private static final String PREF_BATTERY_WARN = "battery_warn";
	private static final String PREF_BATTERY_CRITICAL = "battery_critical";
	private static final String BATTERY_GOOD = "33cc66";
	private static final String BATTERY_OK = "ffc32a";
	private static final String BATTERY_WARN = "ff6a09";
	private static final String BATTERY_CRITICAL = "ff4b4e";
	private static final String DEF_TRANS = "40";
	
	String DIAG_TITLE;
	String DIAG_OK;
	String DIAG_CANCEL;
	String TOAST_SUCCESS;

	SharedPreferences pref;
	SharedPreferences.Editor edit;
	
	View rootView;
	
	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle saved)
	{
		rootView = inflater.inflate(R.layout.fragment_battery, group, false);
		
		return rootView;
	}
	
	@SuppressWarnings("deprecation")
	@SuppressLint("WorldReadableFiles")
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		
		IntentFilter mFilter = new IntentFilter("xbsc_control");
		
		getActivity().registerReceiver(receiver, mFilter);
		
		Intent tintedColorIntent = new Intent("com.mohammadag.colouredstatusbar.ChangeStatusBarColor");
		tintedColorIntent.putExtra("status_bar_icons_color", Color.WHITE);
		tintedColorIntent.putExtra("navigation_bar_icon_color", Color.WHITE);
		getActivity().sendOrderedBroadcast(tintedColorIntent, null);
		
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
		
		DIAG_TITLE = getString(R.string.diag_title);
		DIAG_OK = getString(android.R.string.ok);
		DIAG_CANCEL = getString(android.R.string.cancel);
		TOAST_SUCCESS = getString(R.string.toast_success);
		
		
		pref = getActivity().getSharedPreferences(PREF_NAME, Context.MODE_WORLD_READABLE);
		edit = pref.edit();
		
		
		
		batteryGood = pref.getString(PREF_BATTERY_GOOD, BATTERY_GOOD);
		batteryOk = pref.getString(PREF_BATTERY_OK, BATTERY_OK);
		batteryWarn = pref.getString(PREF_BATTERY_WARN, BATTERY_WARN);
		batteryCritical = pref.getString(PREF_BATTERY_CRITICAL, BATTERY_CRITICAL);
		
		
		buttonUndo = (ImageView) rootView.findViewById(R.id.buttonUndo);
		buttonUndo.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				undo();
			}
		});
		
		buttonApply = (Button) rootView.findViewById(R.id.buttonApply);
		buttonApply.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				batteryApply();
			}
		});
		
		Boolean enable = pref.getBoolean(PREF_ENABLED, true);
		
		if (enable == true){
			
			enableAll();
			
		} else {
			
			
			disableAll();
			
		}

		color100 = (LinearLayout) rootView.findViewById(R.id.layout100);
		color60 = (LinearLayout) rootView.findViewById(R.id.layout60);
		color30 = (LinearLayout) rootView.findViewById(R.id.layout30);
		color15 = (LinearLayout) rootView.findViewById(R.id.layout15);
		
		color100.setBackgroundColor(Color.parseColor("#" + batteryGood));
		color60.setBackgroundColor(Color.parseColor("#" + batteryOk));
		color30.setBackgroundColor(Color.parseColor("#" + batteryWarn));
		color15.setBackgroundColor(Color.parseColor("#" + batteryCritical));
		
		edt100 = new EditText(getActivity());
		edt100.setFilters(new InputFilter[] { input, new InputFilter.LengthFilter(6) });
		edt100.setSingleLine();
		

		edt60 = new EditText(getActivity());
		edt60.setFilters(new InputFilter[] { input, new InputFilter.LengthFilter(6) });
		edt60.setSingleLine();

		edt30 = new EditText(getActivity());
		edt30.setFilters(new InputFilter[] { input, new InputFilter.LengthFilter(6) });
		edt30.setSingleLine();

		edt15 = new EditText(getActivity());
		edt15.setFilters(new InputFilter[] { input, new InputFilter.LengthFilter(6) });
		edt15.setSingleLine();
		
		edt100.setText(batteryGood);
		edt60.setText(batteryOk);
		edt30.setText(batteryWarn);
		edt15.setText(batteryCritical);
		
		edt100.setEnabled(false);
		edt60.setEnabled(false);
		edt30.setEnabled(false);
		edt15.setEnabled(false);

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
		
	}
	
	
	BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Boolean enable = pref.getBoolean(PREF_ENABLED, true);
			
			if (enable == true){
				
				enableAll();
				
			} else {
				
				
				disableAll();
				
			}

		}

	};
	
	
	public void disableAll(){
		
		buttonApply.setEnabled(false);
		buttonUndo.setEnabled(false);
		
	}
	
	public void enableAll(){
		
		buttonApply.setEnabled(true);
		buttonUndo.setEnabled(true);
		
	}
	
	
	public void undo(){
		
		edt100.setText(BATTERY_GOOD);
		edt60.setText(BATTERY_OK);
		edt30.setText(BATTERY_WARN);
		edt15.setText(BATTERY_CRITICAL);
		
		
		color100.setBackgroundColor(Color.parseColor("#" + BATTERY_GOOD));
		color60.setBackgroundColor(Color.parseColor("#" + BATTERY_OK));
		color30.setBackgroundColor(Color.parseColor("#" + BATTERY_WARN));
		color15.setBackgroundColor(Color.parseColor("#" + BATTERY_CRITICAL));
		
	}
	
	@SuppressLint("DefaultLocale")
	public void batteryApply(){
		
		String goodColor = edt100.getText().toString();
		String okColor = edt60.getText().toString();
		String warnColor = edt30.getText().toString();
		String criticalColor = edt15.getText().toString();
		
		getActivity().sendBroadcast(recreateIntent);
		
		if (isHex(goodColor, DEF_TRANS) && isHex(okColor, DEF_TRANS) && isHex(warnColor, DEF_TRANS) && isHex(criticalColor, DEF_TRANS)){
		
	
		
		edit.putString(PREF_BATTERY_GOOD, goodColor);
		edit.putString(PREF_BATTERY_OK, okColor);
		edit.putString(PREF_BATTERY_WARN, warnColor);
		edit.putString(PREF_BATTERY_CRITICAL, criticalColor);
		
		
		edit.apply();
		

		Toast.makeText(getActivity(), TOAST_SUCCESS, Toast.LENGTH_SHORT).show();
		
		getActivity().sendBroadcast(recreateIntent);
		
		}
		
	}
	
	
	public void batteryColor(int i) {

		if (i > 4)
			return;

		final int index = i;

		LayoutInflater li = getActivity().getLayoutInflater();
		View colorView = li.inflate(R.layout.dialog_layout, null);

		final ColorPicker picker = (ColorPicker) colorView.findViewById(R.id.color_picker);
		
		SaturationBar saturationBar = (SaturationBar) colorView.findViewById(R.id.saturationbar);
		ValueBar valueBar = (ValueBar) colorView.findViewById(R.id.valuebar);

		picker.addSaturationBar(saturationBar);
		picker.addValueBar(valueBar);
		
		EditText edtTemp = null;
		LinearLayout colorTemp = null;
		
		
			if (index == 1){
			
				edtTemp = edt100;
				colorTemp = color100;
			
			}
			
			if (index == 2){

				edtTemp = edt60;
				colorTemp = color60;
				
			}
			
			if (index == 3){
				edtTemp = edt30;
				colorTemp = color30;
				
			}
			
			if (index == 4){

				edtTemp = edt15;
				colorTemp = color15;
				
			}
			
			
		final EditText edtFiltered = edtTemp;
		final LinearLayout colorFiltered = colorTemp;

		picker.setOldCenterColor(Color.parseColor("#" + edtFiltered.getText().toString()));
		picker.setColor(Color.parseColor("#" + edtFiltered.getText().toString()));
		

		AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
		alert.setTitle(DIAG_TITLE);
		alert.setIcon(R.drawable.color);
		alert.setView(colorView);
		alert.setPositiveButton(DIAG_OK, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				

				edtFiltered.setText(String.format("%06X", (0xFFFFFF & picker.getColor())));
				colorFiltered.setBackgroundColor(picker.getColor());
				
				

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
	

}
