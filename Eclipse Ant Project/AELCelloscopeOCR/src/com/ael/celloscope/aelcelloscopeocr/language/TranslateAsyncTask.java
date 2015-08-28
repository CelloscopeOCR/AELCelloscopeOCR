/*
 * Copyright 2011 Robert Theis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ael.celloscope.aelcelloscopeocr.language;

import android.graphics.Typeface;
import android.os.AsyncTask;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.ael.celloscope.aelcelloscopeocr.R;
import com.ael.celloscope.aelcelloscopeocr.ocr.CaptureActivity;

/**
 * Class to perform translations in the background.
 */
public final class TranslateAsyncTask extends
		AsyncTask<String, String, Boolean> {

	private static final String TAG = TranslateAsyncTask.class.getSimpleName();

	private CaptureActivity activity;

	private String sourceLanguageCode;
	private String targetLanguageCode;
	private String sourceText;
	private String translatedText = "";

	public TranslateAsyncTask(CaptureActivity activity,
			String sourceLanguageCode, String targetLanguageCode,
			String sourceText) {
		this.activity = activity;
		this.sourceLanguageCode = sourceLanguageCode;
		this.targetLanguageCode = targetLanguageCode;
		this.sourceText = sourceText;

	}

	@Override
	protected Boolean doInBackground(String... arg0) {
		translatedText = Translator.translate(activity, sourceLanguageCode,
				targetLanguageCode, sourceText);

		// Check for failed translations.
		if (translatedText.equals(Translator.BAD_TRANSLATION_MSG)) {
			return false;
		}

		return true;
	}

}
