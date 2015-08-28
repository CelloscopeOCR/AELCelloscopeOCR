package com.ael.celloscope.aelcelloscopeocr.ocr;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.ael.celloscope.aelcelloscopeocr.R;
import com.googlecode.tesseract.android.TessBaseAPI;

/**
 * Class to send bitmap data for OCR.
 */
final class DecodeHandler extends Handler {

	private final CaptureActivity activity;
	private boolean running = true;
	private final TessBaseAPI baseApi;

	DecodeHandler(CaptureActivity activity) {
		this.activity = activity;
		baseApi = activity.getBaseApi();

	}

	@Override
	public void handleMessage(Message message) {
		if (!running) {
			return;
		}
		switch (message.what) {
		case R.id.ocr_decode:
			ocrDecode((byte[]) message.obj, message.arg1, message.arg2);
			break;
		case R.id.quit:
			running = false;
			Looper.myLooper().quit();
			break;
		}
	}

	private void ocrDecode(byte[] data, int width, int height) {

		// activity.displayProgressDialog();
		new OcrRecognizeAsyncTask(activity, baseApi, data, width, height)
				.execute();
	}
}
