package com.ael.celloscope.aelcelloscopeocr.ocr;

import java.io.File;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.ael.celloscope.aelcelloscopeocr.R;
import com.googlecode.leptonica.android.ReadFile;
import com.googlecode.tesseract.android.TessBaseAPI;

/**
 * Class to send OCR requests to the OCR engine in a separate thread, send a
 * success/failure message, and dismiss the indeterminate progress dialog box.
 * Used for non-continuous mode OCR only.
 */
final class OcrRecognizeAsyncTask extends AsyncTask<Void, Void, Boolean> {

	private CaptureActivity activity;
	private TessBaseAPI baseApi;
	private byte[] data;
	private int width;
	private int height;	
	String textResult;

	OcrRecognizeAsyncTask(CaptureActivity activity, TessBaseAPI baseApi,
			byte[] data, int width, int height) {
		this.activity = activity;
		this.baseApi = baseApi;
		this.data = data;
		this.width = width;
		this.height = height;

	}

	@Override
	protected Boolean doInBackground(Void... arg0) {

		 Bitmap bitmap =
		 activity.getCameraManager().buildLuminanceSource(data, width,
		 height).renderCroppedGreyscaleBitmap();

		// File path = Environment
		// .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
		// File file = new File(path, "/test.jpg");
		// Bitmap imutableBitmap = BitmapFactory
		// .decodeFile(file.getAbsolutePath());
		// Bitmap bitmap = imutableBitmap.copy(Bitmap.Config.ARGB_8888, true);

		try {
			baseApi.setImage(ReadFile.readBitmap(bitmap));
			textResult = baseApi.getUTF8Text();

			if (textResult != null && !textResult.equals("")) {
				return true;
			}

		} catch (RuntimeException e) {
			Log.e("OcrRecognizeAsyncTask",
					"Caught RuntimeException in request to Tesseract. Setting state to CONTINUOUS_STOPPED.");
			e.printStackTrace();
			try {
				baseApi.clear();
				activity.stopHandler();
			} catch (NullPointerException e1) {
				// Continue
			}
		}
		return false;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);

		Handler handler = activity.getHandler();
		if (handler != null) {

			if (result) {
				Message message = Message.obtain(handler,
						R.id.ocr_decode_succeeded, textResult);
				message.sendToTarget();
			} else {
				Message message = Message.obtain(handler,
						R.id.ocr_decode_failed, textResult);
				message.sendToTarget();
			}
			activity.getProgressDialog().dismiss();
		}
		if (baseApi != null) {
			baseApi.clear();
		}
	}
}
