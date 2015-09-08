package com.ael.celloscope.aelcelloscopeocr;

import com.ael.celloscope.aelcelloscopeocr.mediaeffects.BitmapEffect;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class BitmapEffectHandler extends Handler {

	private final BitmapEffectActivity activity;
	private static final String TAG = BitmapEffectHandler.class.getSimpleName();

	BitmapEffectHandler(BitmapEffectActivity activity) {
		this.activity = activity;

	}

	@Override
	public void handleMessage(Message message) {

		switch (message.what) {
		case R.id.change_brightness:
			Bitmap brightBitmap = BitmapEffect.doBrightness(
					activity.targetBitmap, (Integer) message.obj);

			activity.bitmapEffectActivityHandler.obtainMessage(R.id.set_image,
					0, 0, brightBitmap).sendToTarget();
			break;

		case R.id.change_contrast:
			Bitmap contrastBitmap = BitmapEffect.doContrast(
					activity.targetBitmap, (Double) message.obj);

			activity.bitmapEffectActivityHandler.obtainMessage(R.id.set_image,
					0, 0, contrastBitmap).sendToTarget();
			break;
		}
	}

}
