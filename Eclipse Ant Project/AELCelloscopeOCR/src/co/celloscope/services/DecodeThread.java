package co.celloscope.services;

import java.util.concurrent.CountDownLatch;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

final class DecodeThread extends Thread {

	private final OCRManager activity;
	private final Context context;
	private Handler handler;
	private final CountDownLatch handlerInitLatch;
 
	DecodeThread(OCRManager activity, Context context) {
		this.activity = activity;
		handlerInitLatch = new CountDownLatch(1);
		this.context = context;
	}

	Handler getHandler() {
		try {
			handlerInitLatch.await();
		} catch (InterruptedException ie) {

		}
		return handler;
	}

	@Override
	public void run() {
		Looper.prepare();
		handler = new DecodeHandler(activity, context);
		handlerInitLatch.countDown();
		Looper.loop();
	}
}
 