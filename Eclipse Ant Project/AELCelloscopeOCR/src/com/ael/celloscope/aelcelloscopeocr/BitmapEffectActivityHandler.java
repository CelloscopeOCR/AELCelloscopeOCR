package com.ael.celloscope.aelcelloscopeocr;

import android.graphics.Bitmap;
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
		case R.id.increaseBrightness:
			activity.imageView.setImageBitmap((Bitmap) message.obj);
			break;

		case R.id.increaseContrast:
			activity.imageView.setImageBitmap((Bitmap) message.obj);
			break;
		}
	}

	public void increaseBrightness() {

		 Message message = bitmapEffectThread.getHandler().obtainMessage(
		 R.id.increaseBrightness, 0, 0, 0);
		 message.sendToTarget();
	}

	public void increaseContrast() {

		Message message = bitmapEffectThread.getHandler().obtainMessage(
				R.id.increaseContrast, 0, 0, 0);
		message.sendToTarget();
	}

}
