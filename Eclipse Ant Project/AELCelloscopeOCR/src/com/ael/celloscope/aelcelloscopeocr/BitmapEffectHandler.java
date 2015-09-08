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
		case R.id.increaseBrightness:
			Bitmap brightBitmap = BitmapEffect.doBrightness(
					activity.targetBitmap, (Integer) message.obj);

			Message message2 = activity.bitmapEffectActivityHandler
					.obtainMessage(R.id.increaseBrightness, 0, 0, brightBitmap);
			message2.sendToTarget();
			Log.d(TAG, message.obj.toString());
			break;

		case R.id.increaseContrast:
			Bitmap contrastBitmap = BitmapEffect.doContrast(
					activity.targetBitmap, (Double) message.obj);

			Message message3 = activity.bitmapEffectActivityHandler
					.obtainMessage(R.id.increaseContrast, 0, 0, contrastBitmap);
			message3.sendToTarget();
			Log.d(TAG, message.obj.toString());
			break;
		}
	}

}
