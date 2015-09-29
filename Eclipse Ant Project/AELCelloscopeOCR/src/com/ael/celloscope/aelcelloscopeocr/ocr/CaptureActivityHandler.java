package com.ael.celloscope.aelcelloscopeocr.ocr;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.ael.celloscope.aelcelloscopeocr.R;

final class CaptureActivityHandler extends Handler {

	private static final String TAG = CaptureActivityHandler.class
			.getSimpleName();

	private final CaptureActivity activity;
	private final DecodeThread decodeThread;
	private static State state;

	private enum State {
		PREVIEW, PREVIEW_PAUSED, CONTINUOUS, CONTINUOUS_PAUSED, SUCCESS, DONE
	}

	CaptureActivityHandler(CaptureActivity activity) {
		this.activity = activity;

		decodeThread = new DecodeThread(activity);
		decodeThread.start();

		state = State.SUCCESS;
		restartOcrPreview();
	}

	private void restartOcrPreview() {

		if (state == State.SUCCESS) {
			state = State.PREVIEW;
		}
	}

	@Override
	public void handleMessage(Message message) {

		switch (message.what) {

		case R.id.ocr_decode_succeeded:
			state = State.SUCCESS;

			new AlertDialog.Builder(activity)
					.setMessage(message.obj.toString())
					.setPositiveButton("OCR", new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							activity.startEmbeddedCropActivity();

						}
					}).setCancelable(false).create().show();

			break;
		case R.id.ocr_decode_failed:
			state = State.PREVIEW;

			Toast toast = Toast.makeText(activity.getBaseContext(),
					"OCR failed. Please try again.", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.TOP, 0, 0);
			toast.show();
			break;
		}
	}

	void stop() {

		Log.d(TAG, "Setting state to CONTINUOUS_PAUSED.");
		state = State.CONTINUOUS_PAUSED;
		removeMessages(R.id.ocr_continuous_decode);
		removeMessages(R.id.ocr_decode);
		removeMessages(R.id.ocr_continuous_decode_failed);
		removeMessages(R.id.ocr_continuous_decode_succeeded);
	}

	void quitSynchronously() {
		state = State.DONE;
		try {
			decodeThread.join(500L);
		} catch (InterruptedException e) {
			Log.w(TAG, "Caught InterruptedException in quitSyncronously()", e);

		} catch (RuntimeException e) {
			Log.w(TAG, "Caught RuntimeException in quitSyncronously()", e);
		} catch (Exception e) {
			Log.w(TAG, "Caught unknown Exception in quitSynchronously()", e);
		}
		removeMessages(R.id.ocr_continuous_decode);
		removeMessages(R.id.ocr_decode);

	}

	void hardwareShutterButtonClick() {

		if (state == State.PREVIEW) {
			ocrDecode();
		}
	}

	void shutterButtonClick() {

		ocrDecode();
	}
	
	private void ocrDecode() {
		state = State.PREVIEW_PAUSED;
		Message message = decodeThread.getHandler().obtainMessage(
				R.id.ocr_decode, 0, 0, 0);
		message.sendToTarget();

	}

}
