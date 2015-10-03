package co.celloscope.services;

import android.app.Activity;
import android.app.AlertDialog;

import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;

import co.celloscope.services.R;

public final class OCRActivity extends Activity {

	// private static final String TAG = OCRActivity.class.getSimpleName();
	private OCRManager mOCRManager;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		Window window = getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.capture);
		this.findViewById(R.id.shutter_button).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						mOCRManager.doOCR(mOCRManager.testFilePath);
					}
				});

		mOCRManager = new OCRManager(OCRActivity.this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mOCRManager.initialize(this, null);
	}

	@Override
	protected void onPause() {
		mOCRManager.release();
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		mOCRManager.destroy();
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			setResult(RESULT_CANCELED);
			finish();
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_CAMERA) {
			mOCRManager.doOCR(mOCRManager.testFilePath);
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