package com.ael.celloscope.aelcelloscopeocr.ocr;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.ael.celloscope.aelcelloscopeocr.R;


/**
 * Class to send bitmap data for OCR.
 */
final class DecodeHandler extends Handler {

	private final CaptureActivity activity;
	private boolean running = true;

	DecodeHandler(CaptureActivity activity) {
		this.activity = activity;
	}

	@Override
	public void handleMessage(Message message) {
		if (!running) {
			return;
		}
		switch (message.what) {
		case R.id.ocr_decode:
			// ocrDecode((byte[]) message.obj, message.arg1, message.arg2);
			ocrDecode(null, 0, 0);
			break;
		case R.id.quit:
			running = false;
			Looper.myLooper().quit();
			break;
		}
	}

	private void ocrDecode(byte[] data, int width, int height) {

		// activity.displayProgressDialog();
		new OcrRecognizeAsyncTask(activity, data, width, height).execute();
	}
}
