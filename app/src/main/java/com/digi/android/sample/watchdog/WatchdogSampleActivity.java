/*
 * Copyright (c) 2014-2025, Digi International Inc. <support@digi.com>
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.digi.android.sample.watchdog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.digi.android.watchdog.ApplicationWatchdogManager;
import com.digi.android.watchdog.SystemWatchdogManager;

/**
 * Watchdog sample application.
 *
 * <p>This example demonstrates the usage of the Watchdog API. The application
 * allows users to register to the watchdog service choosing between the
 * system watchdog or the application watchdog.
 * Users can force application failures anytime using a button.</p>
 *
 * <p>For a complete description on the example, refer to the 'README.md' file
 * included in the example directory.</p>
 */
public class WatchdogSampleActivity extends Activity {

	// Constants.
	private final static String TAG_TIMEOUT = "@@TIMEOUT@@";
	private final static String ERROR_INVALID_TIMEOUT = "ERROR: Invalid timeout.";
	private final static String ERROR_REGISTERING = "ERROR: Could not register to watchdog service > ";
	private final static String SYSTEM_REBOOT_MESSAGE = "Application will stop refreshing the "
			+ "watchdog now. System will reboot in less than " + TAG_TIMEOUT + " milliseconds...";
	private final static String APPLICATION_SHUT_DOWN_MESSAGE = "Application will stop refreshing "
			+ "the watchdog now. System will stop the application in less than "
			+ TAG_TIMEOUT + " milliseconds...";

	private final static int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 66;

	// Variables.
	private Button registerButton;
	private Button unregisterButton;

	private RadioButton systemWatchdogRadioButton;
	private RadioButton applicationWatchdogRadioButton;

	private CheckBox restartApplicationButton;

	private EditText timeoutText;

	private TextView registerStatusText;
	private TextView reportFailureText;

	private ImageButton reportFailureButton;

	private SystemWatchdogManager systemWatchdogManager;

	private ApplicationWatchdogManager applicationWatchdogManager;

	private boolean running = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		// Create watchdog managers.
		systemWatchdogManager = new SystemWatchdogManager(this);
		applicationWatchdogManager = new ApplicationWatchdogManager(this);
		// Initialize UI.
	 	initializeUIComponents();
		// Enable controls.
		enableRegisterControls(true);
		enableReportFailureControls(false);
		// Check overlay permissions to re-launch application.
		checkPermission();
	}

	@Override
	protected void onStop() {
		super.onStop();
		running = false;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
			if (Build.VERSION.SDK_INT >= 29 && !Settings.canDrawOverlays(this)) {
				// If the permissions is not granted by the user, display an error and end the activity.
				showToast("Error: Application requires \"Display over other apps\" permission to work.");
				finish();
			}
		}
	}

	/**
	 * Verifies that the application has been granted the "Display over other apps" permission. If
	 * not, the user is directed to the permissions window.
	 */
	public void checkPermission() {
		if (Build.VERSION.SDK_INT >= 29 && !Settings.canDrawOverlays(this)) {
			Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
			startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
		}
	}

	/**
	 * Initializes all the UI components and sets the corresponding event listeners.
	 */
	private void initializeUIComponents() {
		// Initialize subscribe button.
		registerButton = findViewById(R.id.register_button);
		registerButton.setOnClickListener(v -> handleRegisterButtonPressed());
		// Initialize unregister button.
		unregisterButton = findViewById(R.id.unregister_button);
		unregisterButton.setOnClickListener(v -> handleUnregisterButtonPressed());
		// Initialize system watchdog radio button.
		systemWatchdogRadioButton = findViewById(R.id.system_wd_radio_button);
		systemWatchdogRadioButton.setOnClickListener(v -> handleSystemWatchdogRadioButtonPressed());
		// Initialize application watchdog radio button.
		applicationWatchdogRadioButton = findViewById(R.id.application_wd_radio_button);
		applicationWatchdogRadioButton.setOnClickListener(v -> handleApplicationWatchdogRadioButtonPressed());
		// System Watchdog Help button.
		ImageButton hardwareWatchdogHelpButton = findViewById(R.id.system_wd_help_button);
		hardwareWatchdogHelpButton.setOnClickListener(v -> showPopupDialog(getStringResource(R.string.system_watchdog_service),
				getStringResource(R.string.system_watchdog_description)));
		// Application Watchdog Help button.
		ImageButton softwareWatchdogHelpButton = findViewById(R.id.application_wd_help_button);
		softwareWatchdogHelpButton.setOnClickListener(v -> showPopupDialog(getStringResource(R.string.application_watchdog_service),
				getStringResource(R.string.application_watchdog_description)));
		// Report failure button.
		reportFailureButton = findViewById(R.id.report_failure_button);
		reportFailureButton.setOnClickListener(v -> handleReportFailureButtonPressed());
		// Initialize restart application check box.
		restartApplicationButton = findViewById(R.id.restart_application_button);
		// Initialize timeout text.
		timeoutText = findViewById(R.id.timeout_text);
		// Initialize registered status text.
		registerStatusText = findViewById(R.id.register_status_text);
		// Report Failure text.
		reportFailureText = findViewById(R.id.report_failure_text);
	}

	/**
	 * Handles what happens when the subscribe button is pressed.
	 */
	private void handleRegisterButtonPressed() {
		try {
			long timeout = Long.parseLong(timeoutText.getText().toString());
			long realTimeout;
			if (applicationWatchdogRadioButton.isChecked()) {
				if (restartApplicationButton.isChecked())
					applicationWatchdogManager.init(timeout, generatePendingIntent());
				else
					applicationWatchdogManager.init(timeout, null);
				realTimeout = timeout;
				showToast("Registered to application watchdog with a timeout of " + realTimeout + " milliseconds.");
			} else {
				realTimeout = systemWatchdogManager.init(timeout);
				showToast("Registered to system watchdog with a timeout of " + realTimeout + " milliseconds.");
			}
			startRefreshThread(systemWatchdogRadioButton.isChecked(), realTimeout);
			enableRegisterControls(false);
			setRegisteredStatus(true);
			enableReportFailureControls(true);
		} catch (NumberFormatException e) {
			showToast(ERROR_INVALID_TIMEOUT + " > " + timeoutText.getText().toString());
		} catch (Exception e) {
			showToast(ERROR_REGISTERING + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Handles what happens when the unregister button is pressed.
	 */
	private void handleUnregisterButtonPressed() {
		applicationWatchdogManager.stop();
		running = false;
		enableRegisterControls(true);
		setRegisteredStatus(false);
		enableReportFailureControls(false);
	}

	/**
	 * Handles what happens when the system watchdog service radio button is pressed.
	 */
	private void handleSystemWatchdogRadioButtonPressed() {
		boolean systemWatchdog = systemWatchdogRadioButton.isChecked();
		applicationWatchdogRadioButton.setChecked(!systemWatchdog);
		restartApplicationButton.setEnabled(!systemWatchdog);
	}

	/**
	 * Handles what happens when the applications watchdog service radio button is pressed.
	 */
	private void handleApplicationWatchdogRadioButtonPressed() {
		boolean applicationWatchdog = applicationWatchdogRadioButton.isChecked();
		systemWatchdogRadioButton.setChecked(!applicationWatchdog);
		restartApplicationButton.setEnabled(applicationWatchdog);
	}

	/**
	 * Handles what happens when the report failure button is pressed.
	 */
	private void handleReportFailureButtonPressed() {
		enableReportFailureControls(false);
		unregisterButton.setEnabled(false);
		if (applicationWatchdogRadioButton.isChecked())
			showToast(APPLICATION_SHUT_DOWN_MESSAGE.replace(TAG_TIMEOUT, timeoutText.getText().toString()));
		else
			showToast(SYSTEM_REBOOT_MESSAGE.replace(TAG_TIMEOUT, timeoutText.getText().toString()));
		running = false;
	}

	/**
	 * Changes the registered status text to display registered status.
	 * 
	 * @param subscribed True if application is registered to watchdog service, false otherwise.
	 */
	private void setRegisteredStatus(boolean subscribed) {
		if (subscribed) {
			if (applicationWatchdogRadioButton.isChecked())
				registerStatusText.setText(R.string.registered_application_watchdog);
			else
				registerStatusText.setText(R.string.registered_system_watchdog);
			registerStatusText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.light_green));
		} else {
			registerStatusText.setText(R.string.unregistered);
			registerStatusText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.light_red));
		}
	}

	/**
	 * Changes the enablement state of the register controls.
	 * 
	 * @param enable True to enable register controls, false to disable.
	 */
	private void enableRegisterControls(boolean enable) {
		registerButton.setEnabled(enable);
		timeoutText.setEnabled(enable);
		if (!enable)
			unregisterButton.setEnabled(applicationWatchdogRadioButton.isChecked());
		else
			unregisterButton.setEnabled(false);
		systemWatchdogRadioButton.setEnabled(enable);
		applicationWatchdogRadioButton.setEnabled(enable);
		restartApplicationButton.setEnabled(enable && applicationWatchdogRadioButton.isChecked());
	}

	/**
	 * Changes the enablement state of the report failure controls.
	 * 
	 * @param enable True to enable report failure controls, false to disable.
	 */
	private void enableReportFailureControls(boolean enable) {
		reportFailureButton.setEnabled(enable);
		if (enable) {
			reportFailureText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
			reportFailureButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.failure_image, null));
		} else {
			reportFailureText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.light_grey));
			reportFailureButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.failure_image_disabled, null));
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
	 * @param title Popup dialog title.
	 * @param message Popup dialog message.
	 */
	private void showPopupDialog(String title, String message) {
		final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle(title);
		alertDialog.setMessage(message);
		alertDialog.setCancelable(true);
		alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", (dialog, which) -> alertDialog.dismiss());
		// Set the Icon for the Dialog
		alertDialog.setIcon(R.drawable.help_image);
		alertDialog.show();
	}

	/**
	 * Generates the Pending Intent that will be used to restart the application on failure.
	 * Used only for software watchdog service.
	 * 
	 * @return The generated Pending Intent.
	 */
	private PendingIntent generatePendingIntent() {
		// Generate an intent to start this activity again as a new task.
		Intent intent = new Intent(this, WatchdogSampleActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// Build pending intent.
		return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
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

	/**
	 * Starts the refresh timeout thread with the configured watchdog service and timeout.
	 *
	 * @param isSystemWatchdog {@code true} if the watchdog service is system, {@code false} otherwise.
	 * @param timeout Timeout to refresh the watchdog service.
	 */
	private  void startRefreshThread(final boolean isSystemWatchdog, final long timeout) {
		running = true;
		Thread refreshThread = new Thread() {
			@Override
			public void run() {
				while (running) {
					if (isSystemWatchdog)
						systemWatchdogManager.refresh();
					else
						applicationWatchdogManager.refresh();
					try {
						Thread.sleep(timeout / 2);
					} catch (InterruptedException ignored) {}
				}
			}
		};
		refreshThread.start();
	}
}
