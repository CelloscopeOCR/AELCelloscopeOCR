package co.celloscope.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Messenger;
import android.util.Log;
import co.celloscope.ocr.OCRManager;

public class OCRService extends Service {

	private static final String TAG = OCRService.class.getSimpleName();
	private OCRManager mOcrManager;
	private Messenger svcMessenger;

	@Override
	public void onCreate() {

		mOcrManager = new OCRManager(this);
		Handler handler = new ServiceHandler(mOcrManager);
		mOcrManager.setContextHandler(handler);
		svcMessenger = new Messenger(handler);
	}

	@Override
	public IBinder onBind(Intent intent) {
		mOcrManager.initialize();
		Log.i(TAG, "Service bounded");
		return svcMessenger.getBinder();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		mOcrManager.release();
		Log.i(TAG, "Unbind");
		return super.onUnbind(intent);
	}

	@Override
	public void onDestroy() {
		mOcrManager.destroy();
		Log.i(TAG, "Service Destroyed");
	}

}