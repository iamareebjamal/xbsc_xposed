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
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.SaturationBar;
import com.larswerkman.holocolorpicker.ValueBar;

public class NormalFragment extends SherlockFragment {
	
	Intent recreateIntent = new Intent("recreate");
	
	SharedPreferences pref;
	SharedPreferences.Editor edit;
	
	EditText edt;
	EditText edt1;
	
	ImageView previewButton;
	ImageView colorButton;
	Button applyButton;
	
	LinearLayout oldTopLine;
	LinearLayout newTopLine;
	LinearLayout oldGraph;
	LinearLayout newGraph;
	
	String TOAST_SUCCESS;
	String TOAST_INVALID;
	String DIAG_TITLE;
	String DIAG_OK;
	String DIAG_CANCEL;
	
	SeekBar transBar;

	String previColored;
	String nextColored;
	String nextTransed;

	private static final String PREF_NAME = "color_preference";
	private static final String PREF_ENABLED = "enabled";
	private static final String PREF_COLOR = "color";
	private static final String PREF_TRANS = "transparency";
	private static final String DEF_COLOR = "22cc88";
	private static final String DEF_TRANS = "40";
	
	View rootView;
	

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle saved)
	{
		
		rootView = inflater.inflate(R.layout.fragment_normal, group, false);
		
		return rootView;
	}
	
	
	@SuppressWarnings("deprecation")
	@SuppressLint({ "CommitPrefEdits", "WorldReadableFiles" })
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

		View.OnKeyListener vok = new View.OnKeyListener() {
			public boolean onKey(View view, int keyCode, KeyEvent event) {
				// If the event is a key-down event on the "enter" button
				if ((event.getAction() == KeyEvent.ACTION_DOWN)
						&& (keyCode == KeyEvent.KEYCODE_ENTER)) {
					// Perform action on key press
					preview();
					return true;
				}
				return false;
			}
		};
		
		TOAST_SUCCESS = getString(R.string.toast_success);
		TOAST_INVALID = getString(R.string.toast_invalid);
		DIAG_TITLE = getString(R.string.diag_title);
		DIAG_OK = getString(android.R.string.ok);
		DIAG_CANCEL = getString(android.R.string.cancel);
		
		pref = getActivity().getSharedPreferences(PREF_NAME, Context.MODE_WORLD_READABLE);
		edit = pref.edit();
		
		
		
		String prevColor = pref.getString(PREF_COLOR, DEF_COLOR);
		String prevTrans = pref.getString(PREF_TRANS, DEF_TRANS);
		
		String prevColored = "#" + prevColor;
		String prevTransed = "#" + prevTrans + prevColor;

		previColored = prevColor;
		
		previewButton = (ImageView) rootView.findViewById(R.id.previewButton);
		previewButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				preview();
			}
		});
		
		colorButton = (ImageView) rootView.findViewById(R.id.colorButton);
		colorButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				colorpicker();
			}
		});
		
		applyButton = (Button) rootView.findViewById(R.id.applyButton);
		applyButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				applyButton();
			}
		});
		

		newTopLine = (LinearLayout) rootView.findViewById(R.id.newTopLine);
		newGraph = (LinearLayout) rootView.findViewById(R.id.newGraph);
		
		oldTopLine = (LinearLayout) rootView.findViewById(R.id.oldTopLine);
		oldTopLine.setBackgroundColor(Color.parseColor(prevColored));

		oldGraph = (LinearLayout) rootView.findViewById(R.id.oldGraph);
		oldGraph.setBackgroundColor(Color.parseColor(prevTransed));
		
		oldTopLine.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				oldClick();
				
			}
		});
		
		oldGraph.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				oldClick();
				
			}
		});
		
		newTopLine.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				newClick();
				
			}
		});
		
		newGraph.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				newClick();
				
			}
		});

		transBar = (SeekBar) rootView.findViewById(R.id.seekBar1);

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
		
		
		edt = (EditText) rootView.findViewById(R.id.ET1);
		edt.setText(prevColor);
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			edt.setFilters(new InputFilter[] { input, new InputFilter.LengthFilter(6) });
		} else {
			edt.setFilters(new InputFilter[] {new InputFilter.LengthFilter(6)});
		}
		edt.setSingleLine();
		edt.setOnKeyListener(vok);
		
		
		edt1 = (EditText) rootView.findViewById(R.id.ET2);
		edt1.setFilters(new InputFilter[] { input, new InputFilter.LengthFilter(2) });
		edt1.setText(prevTrans);
		edt1.setSingleLine();
		
		Boolean enable = pref.getBoolean(PREF_ENABLED, true);
		
		if (enable == true){
			
			enableAll();
			
		} else {
			
			
			disableAll();
			
		}
		
		
		
		
		
	}
	
	public void preview() {

		String gotColor = edt.getText().toString();
		String gotTransparency = edt1.getText().toString();

		if (gotTransparency.equals("")) {

			gotTransparency = DEF_TRANS;

		}

		if (isHex(gotColor, gotTransparency) == true) {

			nextColored = gotColor;
			nextTransed = gotTransparency;

			newTopLine.setBackgroundColor(Color.parseColor("#" + gotColor));

			String transp = "#" + gotTransparency + gotColor;

			newGraph.setBackgroundColor(Color.parseColor(transp));
			
			int transpro = Integer.parseInt(edt1.getText().toString(), 16);
			transBar.setProgress(transpro);

		} else {

			Toast.makeText(getActivity(), TOAST_INVALID, Toast.LENGTH_LONG).show();

		}

	}
	
	public void oldClick() {
		
		String previTrans = pref.getString(PREF_TRANS, DEF_TRANS);

		edt.setText(previColored);
		edt1.setText(previTrans);
		int transpro = Integer.parseInt(String.valueOf(previTrans), 16);
		transBar.setProgress(transpro);

	}

	public void newClick() {

		try {

			if (!nextColored.equals("") && !nextTransed.equals("")) {

				edt.setText(nextColored);
				edt1.setText(nextTransed);

			}

		} catch (NullPointerException n) {

			n.printStackTrace();

		}

	}
	
	public void colorpicker() {
		
		String pickerColor = edt.getText().toString();
		
		if (isHex(pickerColor, DEF_TRANS) != true){
			
			Toast.makeText(getActivity(), TOAST_INVALID, Toast.LENGTH_SHORT).show();
			return;
			
		}
			

		LayoutInflater li = getActivity().getLayoutInflater();
		View colorView = li.inflate(R.layout.dialog_layout, null);

		final ColorPicker picker = (ColorPicker) colorView
				.findViewById(R.id.color_picker);
		SaturationBar saturationBar = (SaturationBar) colorView
				.findViewById(R.id.saturationbar);
		ValueBar valueBar = (ValueBar) colorView.findViewById(R.id.valuebar);

		picker.addSaturationBar(saturationBar);
		picker.addValueBar(valueBar);
		
		
		
		picker.setOldCenterColor(Color.parseColor("#" + pickerColor));
		picker.setColor(Color.parseColor("#" + pickerColor));

		AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
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
		
		edt.setEnabled(false);
		edt1.setEnabled(false);
		colorButton.setEnabled(false);
		transBar.setEnabled(false);
		previewButton.setEnabled(false);
		applyButton.setEnabled(false);
		
		
	}
	
	public void enableAll(){
		
		edt.setEnabled(true);
		edt1.setEnabled(true);
		colorButton.setEnabled(true);
		transBar.setEnabled(true);
		previewButton.setEnabled(true);
		applyButton.setEnabled(true);
		
		
	}
	
	
	public void applyButton() {

		String gotColor = edt.getText().toString();

		String gotTransparency = edt1.getText().toString();

		if (gotTransparency.equals("")) {

			gotTransparency = DEF_TRANS;

		}

		if (isHex(gotColor, gotTransparency) == true) {

			applyColor(gotColor, gotTransparency);

		} else {

			Toast.makeText(getActivity(), TOAST_INVALID, Toast.LENGTH_SHORT).show();

		}

	}
	
	
	
	public void applyColor(String gotColor, String gotTransparency) {

		edit.putString(PREF_COLOR, gotColor);
		edit.putString(PREF_TRANS, gotTransparency);
		edit.apply();

		getActivity().sendBroadcast(recreateIntent);

		previColored = gotColor;
		nextColored = gotColor;

		newTopLine.setBackgroundColor(Color.parseColor("#" + gotColor));
		oldTopLine.setBackgroundColor(Color.parseColor("#" + gotColor));

		String transp = "#" + gotTransparency + gotColor;

		newGraph.setBackgroundColor(Color.parseColor(transp));
		oldGraph.setBackgroundColor(Color.parseColor(transp));
		
		int transpro = Integer.parseInt(edt1.getText().toString(), 16);
		transBar.setProgress(transpro);


		getActivity().sendBroadcast(recreateIntent);

		Toast.makeText(getActivity(), TOAST_SUCCESS, Toast.LENGTH_SHORT).show();

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
