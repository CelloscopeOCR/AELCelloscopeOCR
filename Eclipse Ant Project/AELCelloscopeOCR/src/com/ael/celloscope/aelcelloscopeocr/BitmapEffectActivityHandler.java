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

	public void changeBrightness(int brightness) {

		bitmapEffectThread.getHandler()
				.obtainMessage(R.id.change_brightness, 0, 0, brightness)
				.sendToTarget();
	}

	public void changeContrast(double contrast) {

		bitmapEffectThread.getHandler()
				.obtainMessage(R.id.change_contrast, 0, 0, contrast)
				.sendToTarget();
	}
	
	public void rotate(float rotation) {

		bitmapEffectThread.getHandler()
				.obtainMessage(R.id.rotate, 0, 0, rotation)
				.sendToTarget();
	}
}
