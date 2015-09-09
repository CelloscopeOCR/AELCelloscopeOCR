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
		case R.id.set_image:
			activity.imageView.setImageBitmap((Bitmap) message.obj);
			break;
		}
	}

	public void setBrightness(int brightness) {

		bitmapEffectThread.getHandler()
				.obtainMessage(R.id.set_brightness, brightness).sendToTarget();
	}

	public void setContrast(float contrast) {

		bitmapEffectThread.getHandler()
				.obtainMessage(R.id.set_contrast, contrast).sendToTarget();
	}

	public void rotate(float rotation) {

		bitmapEffectThread.getHandler().obtainMessage(R.id.rotate, rotation)
				.sendToTarget();
	}
}
