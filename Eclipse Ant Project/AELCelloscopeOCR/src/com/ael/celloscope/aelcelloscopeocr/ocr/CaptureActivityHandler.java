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
import com.ael.celloscope.aelcelloscopeocr.camera.CameraManager;

/**
 * This class handles all the messaging which comprises the state machine for
 * capture.
 */
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

	@Override
	public void handleMessage(Message message) {

		switch (message.what) {
		case R.id.restart_preview:
			restartOcrPreview();
			break;
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

		// Message quit = Message.obtain(decodeThread.getHandler(), R.id.quit);
		try {
			// quit.sendToTarget(); // This always gives
			// "sending message to a Handler on a dead thread"

			// Wait at most half a second; should be enough time, and onPause()
			// will timeout quickly
			decodeThread.join(500L);
		} catch (InterruptedException e) {
			Log.w(TAG, "Caught InterruptedException in quitSyncronously()", e);
			// continue
		} catch (RuntimeException e) {
			Log.w(TAG, "Caught RuntimeException in quitSyncronously()", e);
			// continue
		} catch (Exception e) {
			Log.w(TAG, "Caught unknown Exception in quitSynchronously()", e);
		}

		// Be absolutely sure we don't send any queued up messages
		removeMessages(R.id.ocr_continuous_decode);
		removeMessages(R.id.ocr_decode);

	}

	/**
	 * Start the preview, but don't try to OCR anything until the user presses
	 * the shutter button.
	 */
	private void restartOcrPreview() {
		// Display the shutter and torch buttons

		if (state == State.SUCCESS) {
			state = State.PREVIEW;

			// Draw the viewfinder.

		}
	}

	/**
	 * Request OCR on the current preview frame.
	 */
	private void ocrDecode() {
		state = State.PREVIEW_PAUSED;
		Message message = decodeThread.getHandler().obtainMessage(
				R.id.ocr_decode, 0, 0, 0);
		message.sendToTarget();

	}

	/**
	 * Request OCR when the hardware shutter button is clicked.
	 */
	void hardwareShutterButtonClick() {
		// Ensure that we're not in continuous recognition mode
		if (state == State.PREVIEW) {
			ocrDecode();
		}
	}

	/**
	 * Request OCR when the on-screen shutter button is clicked.
	 */
	void shutterButtonClick() {

		ocrDecode();
	}

}
