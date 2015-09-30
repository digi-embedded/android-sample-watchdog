package com.example.android.watchdogsample;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.watchdog.WatchdogManager;
import android.watchdog.WatchdogStatusCallback;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class WatchdogSample extends Activity implements WatchdogStatusCallback {
	
	// Constants.
	private final static String TAG = "WatchdogSample";
	private final static String REQUEST_TEXT = "Watchdog service requested application status.";
	private final static String TAG_TIMEOUT = "%%TIMEOUT%%";
	private final static String ERROR_INVALID_TIMEOUT = "ERROR: Invalid timeout.";
	private final static String ERROR_INVALID_INTERVAL = "ERROR: Invalid interval.";
	private final static String ERROR_SUBSCRIBING = "ERROR: Could not subscribe to watchdog service > ";
	private final static String ERROR_INITIALIZE = "ERROR: Could not initialize hardware watchdog service > ";
	private final static String CHECK_LOGCAT_MESSAGE = "Check logcat for more information.";
	private final static String SYSTEM_REBOOT_MESSAGE = "Application will report failure in the next request, system will reboot in " +
			"about " + TAG_TIMEOUT + " seconds...";
	private final static String APPLICATION_SHUT_DOWN_MESSAGE = "Application will report failure in the next request and system will " +
			"stop the application";
	
	// Variables.
	private Button subscribeButton;
	private Button unsubscribeButton;
	private Button initHardwareWatchdogButton;
	
	private RadioButton hardwareWatchdogRadioButton;
	private RadioButton softwareWatchdogRadioButton;
	
	private CheckBox restartApplicationButton;
	
	private EditText intervalText;
	private EditText timeoutText;

	private TextView hardwareWatchdogStatusText;
	private TextView subscribeStatusText;
	private TextView reportFailureText;
	
	private ImageButton hardwareWatchdogServiceHelpButton;
	private ImageButton watchdogServiceHelpButton;
	private ImageButton hardwareWatchdogHelpButton;
	private ImageButton softwareWatchdogHelpButton;
	private ImageButton reportFailureButton;
	
	private WatchdogManager watchdogManager;
	
	private boolean reportValue = true;
	private boolean isSubscribed = false;
	
    /*
     * (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        // Retrieve Watchdog Manager service.
        watchdogManager = (WatchdogManager)getSystemService(Context.WATCHDOG_SERVICE);
        // Initialize UI.
     	initializeUIComponents();
    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onResume()
     */
    protected void onResume() {
    	super.onResume();
    	// Initialize watchdog service status.
    	initWatchdogServiceStatus();
    }
    
    /**
	 * Initializes all the UI components and sets the corresponding event listeners.
	 */
	private void initializeUIComponents() {
		// Initialize subscribe button.
		subscribeButton = (Button)findViewById(R.id.subscribe_button);
		subscribeButton.setOnClickListener(new OnClickListener() {
			/*
			 * (non-Javadoc)
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			public void onClick(View v) {
				handleSubscribeButtonPressed();
			}
		});
		// Initialize unsubscribe button.
		unsubscribeButton = (Button)findViewById(R.id.unsubscribe_button);
		unsubscribeButton.setOnClickListener(new OnClickListener() {
			/*
			 * (non-Javadoc)
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			public void onClick(View v) {
				handleUnsubscribeButtonPressed();
			}
		});
		// Initialize init hardware watchdog button.
		initHardwareWatchdogButton = (Button)findViewById(R.id.init_hw_wd_button);
		initHardwareWatchdogButton.setOnClickListener(new OnClickListener() {
			/*
			 * (non-Javadoc)
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			public void onClick(View v) {
				handleInitHardwareWatchdogButtonPressed();
			}
		});
		// Initialize hardware watchdog radio button.
		hardwareWatchdogRadioButton = (RadioButton)findViewById(R.id.hw_wd_radio_button);
		hardwareWatchdogRadioButton.setOnClickListener(new OnClickListener() {
			/*
			 * (non-Javadoc)
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			public void onClick(View v) {
				handleHardwareWatchdogRadioButtonPressed();
			}
		});
		// Initialize software watchdog radio button.
		softwareWatchdogRadioButton = (RadioButton)findViewById(R.id.sw_wd_radio_button);
		softwareWatchdogRadioButton.setOnClickListener(new OnClickListener() {
			/*
			 * (non-Javadoc)
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			public void onClick(View v) {
				handleSoftwareWatchdogRadioButtonPressed();
			}
		});
		// Hardware Watchdog Help button.
		hardwareWatchdogHelpButton = (ImageButton)findViewById(R.id.hw_wd_help_button);
		hardwareWatchdogHelpButton.setOnClickListener(new OnClickListener() {
			/*
			 * (non-Javadoc)
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			public void onClick(View v) {
				showPopupDialog(getStringResource(R.string.hardware_watchdog), getStringResource(R.string.hardware_watchdog_description));
			}
		});
		// Software Watchdog Help button.
		softwareWatchdogHelpButton = (ImageButton)findViewById(R.id.sw_wd_help_button);
		softwareWatchdogHelpButton.setOnClickListener(new OnClickListener() {
			/*
			 * (non-Javadoc)
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			public void onClick(View v) {
				showPopupDialog(getStringResource(R.string.software_watchdog), getStringResource(R.string.software_watchdog_description));
			}
		});
		// Hardware watchdog service Help button.
		hardwareWatchdogServiceHelpButton = (ImageButton)findViewById(R.id.hw_wd_service_help_button);
		hardwareWatchdogServiceHelpButton.setOnClickListener(new OnClickListener() {
			/*
			 * (non-Javadoc)
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			public void onClick(View v) {
				showPopupDialog(getStringResource(R.string.hardware_watchdog_service_title), getStringResource(R.string.hardware_watchdog_service_description));
			}
		});
		// Watchdog service Help button.
		watchdogServiceHelpButton = (ImageButton)findViewById(R.id.watchdog_service_help_button);
		watchdogServiceHelpButton.setOnClickListener(new OnClickListener() {
			/*
			 * (non-Javadoc)
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			public void onClick(View v) {
				showPopupDialog(getStringResource(R.string.watchdog_service_title), getStringResource(R.string.watchdog_service_description));
			}
		});
		// Report failure button.
		reportFailureButton = (ImageButton)findViewById(R.id.report_failure_button);
		reportFailureButton.setOnClickListener(new OnClickListener() {
			/*
			 * (non-Javadoc)
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			public void onClick(View v) {
				handleReportFailureButtonPressed();
			}
		});
		// Initialize restart application check box.
		restartApplicationButton = (CheckBox)findViewById(R.id.restart_application_button);
		// Initialize time interval text.
		intervalText = (EditText)findViewById(R.id.interval_text);
		// Initialize timeout text.
		timeoutText = (EditText)findViewById(R.id.timeout_text);
		// Initialize subscription status text.
		subscribeStatusText = (TextView)findViewById(R.id.subscribe_status_text);
		// Initialize hardware watchdog status text.
		hardwareWatchdogStatusText = (TextView)findViewById(R.id.hw_wd_status_text);
		// Report Failure text.
		reportFailureText = (TextView)findViewById(R.id.report_failure_text);
	}
    
	/**
	 * Initializes the status of the watchdog service after application starts.
	 */
	private void initWatchdogServiceStatus() {
		if (watchdogManager.isHardwareWatchdogRunning()) {
			enableHardwareWatchdogControls(false);
			setHardwareWatchdogStatus(true);
			timeoutText.setText("" + watchdogManager.getHardwareWatchdogTimeout());
		} else {
			enableHardwareWatchdogControls(true);
			setHardwareWatchdogStatus(false);
		}
		enableSubscribeControls(!isSubscribed);
		setSubscribedStatus(isSubscribed);
		enableReportFailureControls(isSubscribed);
	}
	
	/**
	 * Handles what happens when the subscribe button is pressed.
	 */
	private void handleSubscribeButtonPressed() {
		try {
			long interval = Long.valueOf(intervalText.getText().toString());
			if (restartApplicationButton.isChecked())
				watchdogManager.subscribeApplication(this, getSelectedWatchdogType(), interval, this, generatePendingIntent());
			else
				watchdogManager.subscribeApplication(this, getSelectedWatchdogType(), interval, this);	
			isSubscribed = true;
			enableSubscribeControls(false);
			setSubscribedStatus(true);
			enableReportFailureControls(true);
			reportValue = true;
		} catch (NumberFormatException e) {
			showToast(ERROR_INVALID_INTERVAL + " > " + intervalText.getText().toString());
		} catch (Exception e) {
			showToast(ERROR_SUBSCRIBING + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Handles what happens when the unsubscribe button is pressed.
	 */
	private void handleUnsubscribeButtonPressed() {
		watchdogManager.unsubscribeApplication(this);
		isSubscribed = false;
		enableSubscribeControls(true);
		setSubscribedStatus(false);
		enableReportFailureControls(false);
	}

	/**
	 * Handles what happens when the init hardware watchdog button is pressed.
	 */
	private void handleInitHardwareWatchdogButtonPressed() {
		try {
			int timeout = Integer.valueOf(timeoutText.getText().toString());
			boolean success = watchdogManager.initHardwareWatchdog(timeout);
	        if (!success)
	        	showToast(ERROR_INITIALIZE + CHECK_LOGCAT_MESSAGE);
	        else {
	        	int configuredTimeout = watchdogManager.getHardwareWatchdogTimeout();
	        	showToast("Configured Hardware Watchdog timeout is " + configuredTimeout + " seconds.");
	        	setHardwareWatchdogStatus(true);
	        	enableHardwareWatchdogControls(false);
	        	if (configuredTimeout != timeout)
	        		timeoutText.setText("" + configuredTimeout);
	        }
		} catch (NumberFormatException e) {
			showToast(ERROR_INVALID_TIMEOUT + " > " + timeoutText.getText().toString());
		} catch (Exception e) {
			showToast(ERROR_INITIALIZE + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * Handles what happens when the hardware watchdog radio button is pressed.
	 */
	private void handleHardwareWatchdogRadioButtonPressed() {
		if (hardwareWatchdogRadioButton.isChecked()) {
			softwareWatchdogRadioButton.setChecked(false);
			restartApplicationButton.setEnabled(false);
		} else {
			softwareWatchdogRadioButton.setChecked(true);
			restartApplicationButton.setEnabled(true);
		}
	}
	
	/**
	 * Handles what happens when the software watchdog radio button is pressed.
	 */
	private void handleSoftwareWatchdogRadioButtonPressed() {
		if (softwareWatchdogRadioButton.isChecked()) {
			hardwareWatchdogRadioButton.setChecked(false);
			restartApplicationButton.setEnabled(true);
		} else {
			hardwareWatchdogRadioButton.setChecked(true);
			restartApplicationButton.setEnabled(false);
		}
	}
	
	/**
	 * Handles what happens when the report failure button is pressed.
	 */
	private void handleReportFailureButtonPressed() {
		enableReportFailureControls(false);
		unsubscribeButton.setEnabled(false);
		reportValue = false;
		switch (getSelectedWatchdogType()) {
		case WatchdogManager.WATCHDOG_HARDWARE:
			showToast(SYSTEM_REBOOT_MESSAGE.replace(TAG_TIMEOUT, timeoutText.getText().toString()));
			break;
		case WatchdogManager.WATCHDOG_SOFTWARE:
		default:
			showToast(APPLICATION_SHUT_DOWN_MESSAGE);
			break;
		}
	}
	
	/**
	 * Changes the subscribed status text to display subscribed status.
	 * 
	 * @param subscribed True if application is subscribed to watchdog service, false otherwise.
	 */
	private void setSubscribedStatus(boolean subscribed) {
		if (subscribed) {
			switch (getSelectedWatchdogType()) {
			case WatchdogManager.WATCHDOG_HARDWARE:
				subscribeStatusText.setText(R.string.subscribed_hw);
				break;
			case WatchdogManager.WATCHDOG_SOFTWARE:
			default:
				subscribeStatusText.setText(R.string.subscribed_sw);
				break;
			}
			subscribeStatusText.setTextColor(getResources().getColor(R.color.light_green));
		} else {
			subscribeStatusText.setText(R.string.unsubscribed);
			subscribeStatusText.setTextColor(getResources().getColor(R.color.light_red));
		}
	}

	/**
	 * Changes the hardware watchdog status text to display hardware watchdog status.
	 * 
	 * @param running True if hardware watchdog is running, false otherwise.
	 */
	private void setHardwareWatchdogStatus(boolean running) {
		if (running) {
			hardwareWatchdogStatusText.setText(R.string.running);
			hardwareWatchdogStatusText.setTextColor(getResources().getColor(R.color.light_green));
		} else {
			hardwareWatchdogStatusText.setText(R.string.stopped);
			hardwareWatchdogStatusText.setTextColor(getResources().getColor(R.color.light_red));
		}
	}

	/**
	 * Changes the enablement state of the hardware watchdog controls.
	 * 
	 * @param enable True to enable hardware watchdog controls, false to disable.
	 */
	private void enableHardwareWatchdogControls(boolean enable) {
		initHardwareWatchdogButton.setEnabled(enable);
		timeoutText.setEnabled(enable);
	}
	
	/**
	 * Changes the enablement state of the subscribe controls.
	 * 
	 * @param enable True to enable subscribe controls, false to disable.
	 */
	private void enableSubscribeControls(boolean enable) {
		subscribeButton.setEnabled(enable);
		intervalText.setEnabled(enable);
		unsubscribeButton.setEnabled(!enable);
		hardwareWatchdogRadioButton.setEnabled(enable);
		softwareWatchdogRadioButton.setEnabled(enable);
		if (enable) {
			if (getSelectedWatchdogType() == WatchdogManager.WATCHDOG_SOFTWARE)
				restartApplicationButton.setEnabled(true);
			else
				restartApplicationButton.setEnabled(false);
		} else
			restartApplicationButton.setEnabled(false);
	}
	
	/**
	 * Changes the enablement state of the report failure controls.
	 * 
	 * @param enable True to enable report failure controls, false to disable.
	 */
	private void enableReportFailureControls(boolean enable) {
		reportFailureButton.setEnabled(enable);
		if (enable) {
			reportFailureText.setTextColor(getResources().getColor(R.color.white));
			reportFailureButton.setImageDrawable(getResources().getDrawable(R.drawable.failure_image));
			
		} else {
			reportFailureText.setTextColor(getResources().getColor(R.color.light_grey));
			reportFailureButton.setImageDrawable(getResources().getDrawable(R.drawable.failure_image_disabled));
		}
	}
	
	/**
	 * Displays a toast with the given message.
	 * 
	 * @param message Message to display in the toast.
	 */
	private void showToast(String message) {
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}
	
	/**
	 * Displays a popup dialog with the given title and message.
	 * 
	 * @param title Pupup dialog title.
	 * @param message Popup dialog message.
	 */
	private void showPopupDialog(String title, String message) {
		final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle(title);
		alertDialog.setMessage(message);
		alertDialog.setCancelable(true);
		alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				alertDialog.dismiss();
			}
		});
		// Set the Icon for the Dialog
		alertDialog.setIcon(R.drawable.help_image);
		alertDialog.show();
	}
	
	/**
	 * Retrieves the selected watchdog type.
	 * 
	 * @return The selected watchdog type.
	 */
	private int getSelectedWatchdogType() {
		if (hardwareWatchdogRadioButton.isChecked())
			return WatchdogManager.WATCHDOG_HARDWARE;
		else
			return WatchdogManager.WATCHDOG_SOFTWARE;
	}
	
	/**
	 * Generates the Pending Intent that will be used to restart the application on failure.
	 * Used only for software watchdog service.
	 * 
	 * @return The generated Pending Intent.
	 */
	private PendingIntent generatePendingIntent() {
		// Generate an intent to start this activity again as a new task.
		Intent intent = new Intent(this, WatchdogSample.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// Build pending intent.
		PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
		return pi;
	}
	
	/**
	 * Retrieves the given resource ID string.
	 * 
	 * @param resourceId ID of the resource to retrieve as string.
	 * @return The resource string.
	 */
	private String getStringResource(int resourceId) {
		return getResources().getString(resourceId);
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.watchdog.WatchdogStatusCallback#isApplicationAlive()
	 */
	public boolean isApplicationAlive() {
		Log.i(TAG, REQUEST_TEXT);
		return reportValue;
	}
}