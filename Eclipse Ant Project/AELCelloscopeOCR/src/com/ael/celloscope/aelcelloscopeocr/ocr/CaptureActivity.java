package com.ael.celloscope.aelcelloscopeocr.ocr;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.ael.celloscope.aelcelloscopeocr.R;
import com.ael.celloscope.aelcelloscopeocr.camera.ShutterButton;
import com.googlecode.tesseract.android.TessBaseAPI;
import com.soundcloud.android.crop.Crop;
import com.soundcloud.android.crop.Crop.Position;

/**
 * This activity opens the camera and does the actual scanning on a background
 * thread. It draws a viewfinder to help the user place the text correctly,
 * shows feedback as the image processing is happening, and then overlays the
 * results when a scan is successful.
 */
public final class CaptureActivity extends Activity {

	private static final String TAG = CaptureActivity.class.getSimpleName();

	private CaptureActivityHandler captureActivityHandler;

	private TessBaseAPI baseApi;

	Handler getcaptureActivityHandler() {
		return captureActivityHandler;
	}

	TessBaseAPI getBaseApi() {
		return baseApi;
	}

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		Window window = getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.capture);
		captureActivityHandler = null;
		Button button = (Button) findViewById(R.id.shutter_button);
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startEmbeddedCropActivity();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		this.initializeOCREngine();
		captureActivityHandler = new CaptureActivityHandler(this);
	}

	/**
	 * Method to start or restart recognition after the OCR engine has been
	 * initialized, or after the app regains focus. Sets state related settings
	 * and OCR engine parameters, and requests camera initialization.
	 */
	void resumeOCR() {
		Log.d(TAG, "resumeOCR()");

		if (baseApi != null) {
			baseApi.setPageSegMode(TessBaseAPI.PageSegMode.PSM_AUTO_OSD);
			baseApi.setVariable(TessBaseAPI.VAR_CHAR_BLACKLIST, "");
			baseApi.setVariable(
					TessBaseAPI.VAR_CHAR_WHITELIST,
					"[]!?@#$%&*()<>_-+=/.,:;'\"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789");
		}
	}

	@Override
	protected void onPause() {
		if (captureActivityHandler != null) {
			captureActivityHandler.quitSynchronously();
		}
		super.onPause();
	}

	Uri sourceUri;
	int CAMERA_REQUEST = 0xff00;

	public void startEmbeddedCropActivity() {
		sourceUri = Uri.parse("file://"
				+ Environment.getExternalStorageDirectory()
				+ "/sourceForOcr.jpg");
		Uri destination = Uri.parse("file://"
				+ Environment.getExternalStorageDirectory() + "/ocr.jpg");
		Crop.of(sourceUri, destination).withAspect(0, 1).at(Position.BOTTOM)
				.start(this);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Crop.REQUEST_CROP && resultCode == RESULT_OK) {
			if (captureActivityHandler != null) {
				captureActivityHandler.shutterButtonClick();
			}
		}
	}

	void stopHandler() {
		if (captureActivityHandler != null) {
			captureActivityHandler.stop();
		}
	}

	@Override
	protected void onDestroy() {
		if (baseApi != null) {
			baseApi.end();
		}
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			setResult(RESULT_CANCELED);
			finish();
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_CAMERA) {
			captureActivityHandler.hardwareShutterButtonClick();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private File getStorageDirectory() {

		String state = null;
		try {
			state = Environment.getExternalStorageState();
		} catch (RuntimeException e) {
			Log.e(TAG, "Is the SD card visible?", e);
			showErrorMessage("Error",
					"Required external storage (such as an SD card) is unavailable.");
		}

		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {

			try {
				return getExternalFilesDir(Environment.MEDIA_MOUNTED);
			} catch (NullPointerException e) {
				// We get an error here if the SD card is visible, but full
				Log.e(TAG, "External storage is unavailable");
				showErrorMessage("Error",
						"Required external storage (such as an SD card) is full or unavailable.");
			}

		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {

			Log.e(TAG, "External storage is read-only");
			showErrorMessage(
					"Error",
					"Required external storage (such as an SD card) is unavailable for data storage.");
		} else {
			Log.e(TAG, "External storage is unavailable");
			showErrorMessage("Error",
					"Required external storage (such as an SD card) is unavailable or corrupted.");
		}
		return null;
	}

	private void initializeOCREngine() {
		// Do OCR engine initialization, if necessary
		boolean doNewInit = (baseApi == null);
		if (doNewInit) {

			File storageDirectory = getStorageDirectory();
			if (storageDirectory != null) {

				if (captureActivityHandler != null) {
					captureActivityHandler.quitSynchronously();
				}

				// Start AsyncTask to install language data and init OCR
				baseApi = new TessBaseAPI();
				new OcrInitAsyncTask(this, baseApi).execute(storageDirectory
						.toString());
			}
		} else {
			// We already have the engine initialized, so just start the camera.
			resumeOCR();
		}
	}

	void showErrorMessage(String title, String message) {
		new AlertDialog.Builder(this).setTitle(title).setMessage(message)
				.setOnCancelListener(new FinishListener(this))
				.setPositiveButton("Done", new FinishListener(this)).show();
	}
}
