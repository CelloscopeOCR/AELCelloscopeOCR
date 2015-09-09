package com.ael.celloscope.aelcelloscopeocr;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;

public class BitmapEffectHandler extends Handler {

	private final BitmapEffectActivity activity;

	// private static final String TAG =
	// BitmapEffectHandler.class.getSimpleName();

	BitmapEffectHandler(BitmapEffectActivity activity) {
		this.activity = activity;

	}

	@Override
	public void handleMessage(Message message) {

		Bitmap alteredBitmap = null;

		switch (message.what) {
		case R.id.set_brightness:
			int brightness = (Integer) message.obj;
			alteredBitmap = MatrixHelper.setBrightness(activity.targetBitmap,
					brightness);
			break;
		case R.id.set_contrast:
			float contrast = (Float) message.obj;
			alteredBitmap = MatrixHelper.setContrast(activity.targetBitmap,
					contrast);
			break;
		case R.id.rotate:
			float angle = (Float) message.obj;
			alteredBitmap = MatrixHelper.rotate(activity.targetBitmap, angle);
			break;
		}

		if (alteredBitmap != null) {
			activity.bitmapEffectActivityHandler.obtainMessage(R.id.set_image,
					0, 0, alteredBitmap).sendToTarget();
		}

	}

}
