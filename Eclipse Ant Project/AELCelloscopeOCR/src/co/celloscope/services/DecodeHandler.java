package co.celloscope.services;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import co.celloscope.services.R;

final class DecodeHandler extends Handler {

	private final OCRHelper activity;
	private final Context context;
	private boolean running = true;

	DecodeHandler(OCRHelper activity, Context context) {
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
			ocrDecode(message.obj, 0, 0);
			break;
		case R.id.quit:
			running = false;
			Looper.myLooper().quit();
			break;
		}
	}

	private void ocrDecode(Object data, int width, int height) {
		new OcrRecognizeAsyncTask(activity, context, data, width, height)
				.execute();
	}
}
