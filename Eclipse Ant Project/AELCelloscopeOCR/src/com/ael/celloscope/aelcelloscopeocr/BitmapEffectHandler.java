package com.ael.celloscope.aelcelloscopeocr;

import com.ael.celloscope.aelcelloscopeocr.mediaeffects.BitmapEffect;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
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

		Matrix matrix = new Matrix();
		ColorMatrix colorMatrix = new ColorMatrix();
		Paint paint = new Paint();
		Bitmap alteredBitmap = Bitmap.createBitmap(
				activity.targetBitmap.getWidth(),
				activity.targetBitmap.getHeight(),
				activity.targetBitmap.getConfig());
		Canvas canvas = new Canvas();

		switch (message.what) {
		case R.id.set_brightness:
			int brightness = (Integer) message.obj;
			alteredBitmap = MatrixHelper.setBrightness(activity.targetBitmap,
					brightness);
			activity.bitmapEffectActivityHandler.obtainMessage(R.id.set_image,
					0, 0, alteredBitmap).sendToTarget();
			break;

		case R.id.set_contrast:

			float contrast = (Float) message.obj / 10;
			colorMatrix.set(new float[] { contrast, 0, 0, 0, 0, 0, contrast, 0,
					0, 0, 0, 0, contrast, 0, 0, 0, 0, 0, 1, 0 });
			paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
			canvas.setBitmap(alteredBitmap);
			canvas.drawBitmap(activity.targetBitmap, matrix, paint);
			activity.bitmapEffectActivityHandler.obtainMessage(R.id.set_image,
					0, 0, alteredBitmap).sendToTarget();

			break;

		case R.id.rotate:

			matrix.setRotate((Float) message.obj,
					activity.targetBitmap.getWidth() / 2,
					activity.targetBitmap.getHeight() / 2);
			canvas.setBitmap(alteredBitmap);
			canvas.drawBitmap(activity.targetBitmap, matrix, paint);
			activity.bitmapEffectActivityHandler.obtainMessage(R.id.set_image,
					0, 0, alteredBitmap).sendToTarget();

			break;
		}

	}

}
