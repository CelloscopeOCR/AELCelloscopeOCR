package com.ael.celloscope.aelcelloscopeocr;

import android.os.Handler;
import android.os.Message;

public class BitmapEffectActivityHandler extends Handler {

	private final BitmapEffectActivity activity;
	private final BitmapEffectThread bitmapEffectThread;

	BitmapEffectActivityHandler(BitmapEffectActivity activity) {
		this.activity = activity;

		bitmapEffectThread = new BitmapEffectThread(activity);
		bitmapEffectThread.start();
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
