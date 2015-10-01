package com.ael.celloscope.aelcelloscopeocr.ocr;

import android.app.Activity;
import android.app.AlertDialog;

import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;

import com.ael.celloscope.aelcelloscopeocr.ocr.R;

public final class OCRActivity extends Activity {

	// private static final String TAG = OCRActivity.class.getSimpleName();
	private OCRHelper mOcrHelper;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		Window window = getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.capture);
		mOcrHelper = new OCRHelper(OCRActivity.this);
		this.findViewById(R.id.shutter_button).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						mOcrHelper.ocrActivityHandler.shutterButtonClick(Environment
								.getExternalStorageDirectory() + "/ocr.jpg");
					}
				});
	}

	@Override
	protected void onResume() {
		super.onResume();
		mOcrHelper.initializeOCREngine();
		mOcrHelper.ocrActivityHandler = new OCRHandler(mOcrHelper, this);
	}

	@Override
	protected void onPause() {
		if (mOcrHelper.ocrActivityHandler != null) {
			mOcrHelper.ocrActivityHandler.quitSynchronously();
		}
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		if (mOcrHelper.baseApi != null) {
			mOcrHelper.baseApi.end();
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
			mOcrHelper.ocrActivityHandler.hardwareShutterButtonClick(Environment
					.getExternalStorageDirectory() + "/ocr.jpg");
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	void showErrorMessage(String title, String message) {
		new AlertDialog.Builder(this).setTitle(title).setMessage(message)
				.setOnCancelListener(new FinishListener(this))
				.setPositiveButton("Done", new FinishListener(this)).show();
	}

}