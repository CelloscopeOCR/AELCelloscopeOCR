package com.ael.celloscope.aelcelloscopeocr.ocr;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.ClipboardManager;
import android.text.SpannableStringBuilder;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import android.widget.Toast;

import com.ael.celloscope.aelcelloscopeocr.R;
import com.ael.celloscope.aelcelloscopeocr.camera.CameraManager;
import com.ael.celloscope.aelcelloscopeocr.camera.ShutterButton;
import com.googlecode.tesseract.android.TessBaseAPI;

/**
 * This activity opens the camera and does the actual scanning on a background
 * thread. It draws a viewfinder to help the user place the text correctly,
 * shows feedback as the image processing is happening, and then overlays the
 * results when a scan is successful.
 */
public final class CaptureActivity extends Activity implements
		SurfaceHolder.Callback, ShutterButton.OnShutterButtonListener {

	private static final String TAG = CaptureActivity.class.getSimpleName();

	private CameraManager cameraManager;
	private CaptureActivityHandler captureActivityHandler;
	private ViewfinderView viewfinderView;
	private SurfaceView surfaceView;
	private SurfaceHolder surfaceHolder;
	private boolean hasSurface;
	private ShutterButton shutterButton;

	private TessBaseAPI baseApi;
	private ProgressDialog dialog;
	private ProgressDialog indeterminateDialog;
	private boolean isEngineReady;

	Handler getcaptureActivityHandler() {
		return captureActivityHandler;
	}

	CameraManager getCameraManager() {
		return cameraManager;
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
		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);

		captureActivityHandler = null;
		hasSurface = false;

		shutterButton = (ShutterButton) findViewById(R.id.shutter_button);
		shutterButton.setOnShutterButtonListener(this);

		cameraManager = new CameraManager(getApplication());
		viewfinderView.setCameraManager(cameraManager);

		// Set listener to change the size of the viewfinder rectangle.
		viewfinderView.setOnTouchListener(new View.OnTouchListener() {
			int lastX = -1;
			int lastY = -1;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					lastX = -1;
					lastY = -1;
					return true;
				case MotionEvent.ACTION_MOVE:
					int currentX = (int) event.getX();
					int currentY = (int) event.getY();

					try {
						Rect rect = cameraManager.getFramingRect();

						final int BUFFER = 50;
						final int BIG_BUFFER = 60;
						if (lastX >= 0) {
							// Adjust the size of the viewfinder rectangle.
							// Check if the touch event occurs in the corner
							// areas first, because the regions overlap.
							if (((currentX >= rect.left - BIG_BUFFER && currentX <= rect.left
									+ BIG_BUFFER) || (lastX >= rect.left
									- BIG_BUFFER && lastX <= rect.left
									+ BIG_BUFFER))
									&& ((currentY <= rect.top + BIG_BUFFER && currentY >= rect.top
											- BIG_BUFFER) || (lastY <= rect.top
											+ BIG_BUFFER && lastY >= rect.top
											- BIG_BUFFER))) {
								// Top left corner: adjust both top and left
								// sides
								cameraManager.adjustFramingRect(
										2 * (lastX - currentX),
										2 * (lastY - currentY));
								viewfinderView.removeResultText();
							} else if (((currentX >= rect.right - BIG_BUFFER && currentX <= rect.right
									+ BIG_BUFFER) || (lastX >= rect.right
									- BIG_BUFFER && lastX <= rect.right
									+ BIG_BUFFER))
									&& ((currentY <= rect.top + BIG_BUFFER && currentY >= rect.top
											- BIG_BUFFER) || (lastY <= rect.top
											+ BIG_BUFFER && lastY >= rect.top
											- BIG_BUFFER))) {
								// Top right corner: adjust both top and right
								// sides
								cameraManager.adjustFramingRect(
										2 * (currentX - lastX),
										2 * (lastY - currentY));
								viewfinderView.removeResultText();
							} else if (((currentX >= rect.left - BIG_BUFFER && currentX <= rect.left
									+ BIG_BUFFER) || (lastX >= rect.left
									- BIG_BUFFER && lastX <= rect.left
									+ BIG_BUFFER))
									&& ((currentY <= rect.bottom + BIG_BUFFER && currentY >= rect.bottom
											- BIG_BUFFER) || (lastY <= rect.bottom
											+ BIG_BUFFER && lastY >= rect.bottom
											- BIG_BUFFER))) {
								// Bottom left corner: adjust both bottom and
								// left sides
								cameraManager.adjustFramingRect(
										2 * (lastX - currentX),
										2 * (currentY - lastY));
								viewfinderView.removeResultText();
							} else if (((currentX >= rect.right - BIG_BUFFER && currentX <= rect.right
									+ BIG_BUFFER) || (lastX >= rect.right
									- BIG_BUFFER && lastX <= rect.right
									+ BIG_BUFFER))
									&& ((currentY <= rect.bottom + BIG_BUFFER && currentY >= rect.bottom
											- BIG_BUFFER) || (lastY <= rect.bottom
											+ BIG_BUFFER && lastY >= rect.bottom
											- BIG_BUFFER))) {
								// Bottom right corner: adjust both bottom and
								// right sides
								cameraManager.adjustFramingRect(
										2 * (currentX - lastX),
										2 * (currentY - lastY));
								viewfinderView.removeResultText();
							} else if (((currentX >= rect.left - BUFFER && currentX <= rect.left
									+ BUFFER) || (lastX >= rect.left - BUFFER && lastX <= rect.left
									+ BUFFER))
									&& ((currentY <= rect.bottom && currentY >= rect.top) || (lastY <= rect.bottom && lastY >= rect.top))) {
								// Adjusting left side: event falls within
								// BUFFER pixels of left side, and between top
								// and bottom side limits
								cameraManager.adjustFramingRect(
										2 * (lastX - currentX), 0);
								viewfinderView.removeResultText();
							} else if (((currentX >= rect.right - BUFFER && currentX <= rect.right
									+ BUFFER) || (lastX >= rect.right - BUFFER && lastX <= rect.right
									+ BUFFER))
									&& ((currentY <= rect.bottom && currentY >= rect.top) || (lastY <= rect.bottom && lastY >= rect.top))) {
								// Adjusting right side: event falls within
								// BUFFER pixels of right side, and between top
								// and bottom side limits
								cameraManager.adjustFramingRect(
										2 * (currentX - lastX), 0);
								viewfinderView.removeResultText();
							} else if (((currentY <= rect.top + BUFFER && currentY >= rect.top
									- BUFFER) || (lastY <= rect.top + BUFFER && lastY >= rect.top
									- BUFFER))
									&& ((currentX <= rect.right && currentX >= rect.left) || (lastX <= rect.right && lastX >= rect.left))) {
								// Adjusting top side: event falls within BUFFER
								// pixels of top side, and between left and
								// right side limits
								cameraManager.adjustFramingRect(0,
										2 * (lastY - currentY));
								viewfinderView.removeResultText();
							} else if (((currentY <= rect.bottom + BUFFER && currentY >= rect.bottom
									- BUFFER) || (lastY <= rect.bottom + BUFFER && lastY >= rect.bottom
									- BUFFER))
									&& ((currentX <= rect.right && currentX >= rect.left) || (lastX <= rect.right && lastX >= rect.left))) {
								// Adjusting bottom side: event falls within
								// BUFFER pixels of bottom side, and between
								// left and right side limits
								cameraManager.adjustFramingRect(0,
										2 * (currentY - lastY));
								viewfinderView.removeResultText();
							}
						}
					} catch (NullPointerException e) {
						Log.e(TAG, "Framing rect not available", e);
					}
					v.invalidate();
					lastX = currentX;
					lastY = currentY;
					return true;
				case MotionEvent.ACTION_UP:
					lastX = -1;
					lastY = -1;
					return true;
				}
				return false;
			}
		});

		isEngineReady = false;
	}

	@Override
	protected void onResume() {
		super.onResume();
		resetStatusView();

		// Set up the camera preview surface.
		surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		surfaceHolder = surfaceView.getHolder();
		if (!hasSurface) {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}

		// Comment out the following block to test non-OCR functions without an
		// SD card

		// Do OCR engine initialization, if necessary
		boolean doNewInit = (baseApi == null);
		if (doNewInit) {

			File storageDirectory = getStorageDirectory();
			if (storageDirectory != null) {
				initOcrEngine(storageDirectory);
			}
		} else {
			// We already have the engine initialized, so just start the camera.
			resumeOCR();
		}
	}

	/**
	 * Method to start or restart recognition after the OCR engine has been
	 * initialized, or after the app regains focus. Sets state related settings
	 * and OCR engine parameters, and requests camera initialization.
	 */
	void resumeOCR() {
		Log.d(TAG, "resumeOCR()");
		isEngineReady = true;

		if (baseApi != null) {
			baseApi.setPageSegMode(TessBaseAPI.PageSegMode.PSM_AUTO_OSD);
			baseApi.setVariable(TessBaseAPI.VAR_CHAR_BLACKLIST, "");
			baseApi.setVariable(
					TessBaseAPI.VAR_CHAR_WHITELIST,
					"!?@#$%&*()<>_-+=/.,:;'\"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789");
		}

		if (hasSurface) {
			initCamera(surfaceHolder);
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.d(TAG, "surfaceCreated()");

		if (holder == null) {
			Log.e(TAG, "surfaceCreated gave us a null surface");
		}

		// Only initialize the camera if the OCR engine is ready to go.
		if (!hasSurface && isEngineReady) {
			Log.d(TAG, "surfaceCreated(): calling initCamera()...");
			initCamera(holder);
		}
		hasSurface = true;
	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		Log.d(TAG, "initCamera()");
		if (surfaceHolder == null) {
			throw new IllegalStateException("No SurfaceHolder provided");
		}
		try {

			cameraManager.openDriver(surfaceHolder);
			captureActivityHandler = new CaptureActivityHandler(this,
					cameraManager);

		} catch (IOException ioe) {
			showErrorMessage("Error",
					"Could not initialize camera. Please try restarting device.");
		} catch (RuntimeException e) {

			showErrorMessage("Error",
					"Could not initialize camera. Please try restarting device.");
		}
	}

	@Override
	protected void onPause() {
		if (captureActivityHandler != null) {
			captureActivityHandler.quitSynchronously();
		}

		// Stop using the camera, to avoid conflicting with other camera-based
		// apps
		cameraManager.closeDriver();

		if (!hasSurface) {
			SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
			SurfaceHolder surfaceHolder = surfaceView.getHolder();
			surfaceHolder.removeCallback(this);
		}
		super.onPause();
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
		} else if (keyCode == KeyEvent.KEYCODE_FOCUS) {
			// Only perform autofocus if user is not holding down the button.
			if (event.getRepeatCount() == 0) {
				cameraManager.requestAutoFocus(500L);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
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

	/**
	 * Requests initialization of the OCR engine with the given parameters.
	 * 
	 * @param storageRoot
	 *            Path to location of the tessdata directory to use
	 */
	private void initOcrEngine(File storageRoot) {
		isEngineReady = false;

		if (dialog != null) {
			dialog.dismiss();
		}
		dialog = new ProgressDialog(this);

		indeterminateDialog = new ProgressDialog(this);
		indeterminateDialog.setTitle("Please wait");
		indeterminateDialog.setMessage("Initializing OCR engine ...");
		indeterminateDialog.setCancelable(false);
		indeterminateDialog.show();

		if (captureActivityHandler != null) {
			captureActivityHandler.quitSynchronously();
		}

		// Start AsyncTask to install language data and init OCR
		baseApi = new TessBaseAPI();
		new OcrInitAsyncTask(this, baseApi, dialog, indeterminateDialog)
				.execute(storageRoot.toString());
	}


	private void resetStatusView() {

		viewfinderView.setVisibility(View.VISIBLE);
		shutterButton.setVisibility(View.VISIBLE);
		viewfinderView.removeResultText();
	}

	void setButtonVisibility(boolean visible) {
		if (shutterButton != null && visible == true) {
			shutterButton.setVisibility(View.VISIBLE);
		} else if (shutterButton != null) {
			shutterButton.setVisibility(View.GONE);
		}
	}

	void setShutterButtonClickable(boolean clickable) {
		shutterButton.setClickable(clickable);
	}

	void drawViewfinder() {
		viewfinderView.drawViewfinder();
	}

	@Override
	public void onShutterButtonClick(ShutterButton b) {
		if (captureActivityHandler != null) {
			captureActivityHandler.shutterButtonClick();
		}
	}

	@Override
	public void onShutterButtonFocus(ShutterButton b, boolean pressed) {
		cameraManager.requestAutoFocus(350L);
	}

	void displayProgressDialog() {

		indeterminateDialog = new ProgressDialog(this);
		indeterminateDialog.setTitle("Please wait");
		indeterminateDialog.setMessage("Performing OCR " + "...");
		indeterminateDialog.setCancelable(false);
		indeterminateDialog.show();
	}

	ProgressDialog getProgressDialog() {
		return indeterminateDialog;
	}

	void showErrorMessage(String title, String message) {
		new AlertDialog.Builder(this).setTitle(title).setMessage(message)
				.setOnCancelListener(new FinishListener(this))
				.setPositiveButton("Done", new FinishListener(this)).show();
	}
}
