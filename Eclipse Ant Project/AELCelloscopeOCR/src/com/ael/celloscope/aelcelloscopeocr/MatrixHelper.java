package com.ael.celloscope.aelcelloscopeocr;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;

public class MatrixHelper {

	public static Bitmap setBrightness(Bitmap sourceBitmap, int brightness) {

		Bitmap alteredBitmap = Bitmap.createBitmap(sourceBitmap.getWidth(),
				sourceBitmap.getHeight(), sourceBitmap.getConfig());
		Canvas canvas = new Canvas(alteredBitmap);
		Paint paint = new Paint();
		ColorMatrix colorMatrix = new ColorMatrix(new float[] { 1, 0, 0, 0,
				brightness, 0, 1, 0, 0, brightness, 0, 0, 1, 0, brightness, 0,
				0, 0, 1, 0 });
		paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));

		canvas.drawBitmap(sourceBitmap, new Matrix(), paint);

		return alteredBitmap;
	}

	public static Bitmap setContrast(Bitmap sourceBitmap, float contrast) {

		Bitmap alteredBitmap = Bitmap.createBitmap(sourceBitmap.getWidth(),
				sourceBitmap.getHeight(), sourceBitmap.getConfig());
		Canvas canvas = new Canvas(alteredBitmap);
		Paint paint = new Paint();
		ColorMatrix colorMatrix = new ColorMatrix(
				new float[] { contrast, 0, 0, 0, 0, 0, contrast, 0, 0, 0, 0, 0,
						contrast, 0, 0, 0, 0, 0, 1, 0 });
		paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));

		canvas.drawBitmap(sourceBitmap, new Matrix(), paint);

		return alteredBitmap;
	}

}
