package com.ael.celloscope.aelcelloscopeocr.ocr;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.ael.celloscope.aelcelloscopeocr.ocr.R;
import com.googlecode.leptonica.android.ReadFile;
import com.googlecode.tesseract.android.TessBaseAPI;

final class OcrRecognizeAsyncTask extends AsyncTask<Void, Void, Boolean> {

	private OCRHelper helper;
	private TessBaseAPI baseApi;
	private final Context context;
	private Object data;
	// private int width;
	// private int height;
	String textResult;
	private ProgressDialog indeterminateDialog;

	OcrRecognizeAsyncTask(OCRHelper helper, Context context, Object data,
			int width, int height) {
		this.helper = helper;
		this.baseApi = helper.getBaseApi();
		this.context = context;
		this.data = data;
		// this.width = width;
		// this.height = height;

	}

	@Override
	protected void onPreExecute() {
		this.displayProgressDialog();
	}

	@Override
	protected Boolean doInBackground(Void... arg0) {

		try {
			baseApi.setImage(ReadFile.readBitmap(BitmapEffect
					.decodeSmallBitmap((String) data, 1200, 1200 / 7)));
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
				helper.stopHandler();
			} catch (NullPointerException e1) {
				// Continue
			}
		}
		return false;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);

		Handler handler = helper.getcaptureActivityHandler();
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
			if (context instanceof Activity) {
				indeterminateDialog.dismiss();
			}
		}
		if (baseApi != null) {
			baseApi.clear();
		}
	}

	void displayProgressDialog() {
		if (context instanceof Activity) {
			indeterminateDialog = new ProgressDialog(context);
			indeterminateDialog.setTitle("Please wait");
			indeterminateDialog.setMessage("Performing OCR " + "...");
			indeterminateDialog.setCancelable(false);
			indeterminateDialog.show();
		}
	}
}
