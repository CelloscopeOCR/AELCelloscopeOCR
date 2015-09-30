package com.ael.celloscope.aelcelloscopeocr.ocr;

import java.util.concurrent.CountDownLatch;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

final class DecodeThread extends Thread {

	private final OCRHelper activity;
	private final OCRActivity context;
	private Handler handler;
	private final CountDownLatch handlerInitLatch;
 
	DecodeThread(OCRHelper activity, OCRActivity context) {
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
 