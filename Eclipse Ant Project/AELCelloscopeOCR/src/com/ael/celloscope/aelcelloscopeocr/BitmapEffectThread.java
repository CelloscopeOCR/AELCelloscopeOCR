package com.ael.celloscope.aelcelloscopeocr;

import java.util.concurrent.CountDownLatch;

import android.os.Handler;
import android.os.Looper;

public class BitmapEffectThread extends Thread {
	private final BitmapEffectActivity activity;
	private Handler handler;

	private final CountDownLatch handlerInitLatch;

	BitmapEffectThread(BitmapEffectActivity activity) {
		this.activity = activity;
		handlerInitLatch = new CountDownLatch(1);
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
		 handler = new BitmapEffectHandler(activity);
		handlerInitLatch.countDown();
		Looper.loop();
	}
}
