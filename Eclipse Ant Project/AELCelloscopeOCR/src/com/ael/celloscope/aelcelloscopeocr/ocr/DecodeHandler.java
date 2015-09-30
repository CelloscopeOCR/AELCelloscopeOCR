package com.ael.celloscope.aelcelloscopeocr.ocr;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.ael.celloscope.aelcelloscopeocr.ocr.R;

final class DecodeHandler extends Handler {

	private final OCRHelper activity;
	private final OCRActivity context;
	private boolean running = true;

	DecodeHandler(OCRHelper activity, OCRActivity context) {
		this.activity = activity;
		this.context = context;
	}

	@Override
	public void handleMessage(Message message) {
		if (!running) {
			return;
		}
		switch (message.what) {
		case R.id.ocr_decode:
			ocrDecode(null, 0, 0);
			break;
		case R.id.quit:
			running = false;
			Looper.myLooper().quit();
			break;
		}
	}

	private void ocrDecode(byte[] data, int width, int height) {
		new OcrRecognizeAsyncTask(activity, context, data, width, height)
				.execute();
	}
}
