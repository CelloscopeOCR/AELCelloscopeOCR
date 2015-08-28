package com.ael.celloscope.aelcelloscopeocr.ocr;

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
	private final CameraManager cameraManager;

	private enum State {
		PREVIEW, PREVIEW_PAUSED, CONTINUOUS, CONTINUOUS_PAUSED, SUCCESS, DONE
	}

	CaptureActivityHandler(CaptureActivity activity, CameraManager cameraManager) {
		this.activity = activity;
		this.cameraManager = cameraManager;

		cameraManager.startPreview();

		decodeThread = new DecodeThread(activity);
		decodeThread.start();

		state = State.SUCCESS;
		// Show the shutter and torch buttons
		activity.setButtonVisibility(true);
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
			activity.setShutterButtonClickable(true);
			// activity.handleOcrDecode((OcrResult) message.obj);
			Toast toastSuccess = Toast.makeText(activity.getBaseContext(),
					message.obj.toString(), Toast.LENGTH_LONG);
			toastSuccess.setGravity(Gravity.TOP, 0, 0);
			toastSuccess.show();
			break;
		case R.id.ocr_decode_failed:
			state = State.PREVIEW;
			activity.setShutterButtonClickable(true);
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
		if (cameraManager != null) {
			cameraManager.stopPreview();
		}
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
		activity.setButtonVisibility(true);

		if (state == State.SUCCESS) {
			state = State.PREVIEW;

			// Draw the viewfinder.
			activity.drawViewfinder();
		}
	}

	/**
	 * Request OCR on the current preview frame.
	 */
	private void ocrDecode() {
		state = State.PREVIEW_PAUSED;
		cameraManager.requestOcrDecode(decodeThread.getHandler(),
				R.id.ocr_decode);
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
		// Disable further clicks on this button until OCR request is finished
		activity.setShutterButtonClickable(false);
		ocrDecode();
	}

}
