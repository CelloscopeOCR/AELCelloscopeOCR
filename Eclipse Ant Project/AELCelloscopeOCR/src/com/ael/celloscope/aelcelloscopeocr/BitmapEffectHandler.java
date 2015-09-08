package com.ael.celloscope.aelcelloscopeocr;

import android.os.Handler;
import android.os.Message;

public class BitmapEffectHandler extends Handler {

	private final BitmapEffectActivity activity;
	private final BitmapEffectThread decodeThread;

	BitmapEffectHandler(BitmapEffectActivity activity) {
		this.activity = activity;

		decodeThread = new BitmapEffectThread(activity);
		decodeThread.start();
	}

	@Override
	public void handleMessage(Message message) {

		switch (message.what) {
		case R.id.restart_preview:

			break;
		case R.id.ocr_decode_succeeded:

			break;
		case R.id.ocr_decode_failed:

			break;
		}
	}

}
