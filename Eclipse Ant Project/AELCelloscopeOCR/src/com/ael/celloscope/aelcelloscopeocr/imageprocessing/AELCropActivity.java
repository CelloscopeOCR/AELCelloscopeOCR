package com.ael.celloscope.aelcelloscopeocr.imageprocessing;

import java.io.File;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.EGLConfigChooser;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.ael.celloscope.aelcelloscopeocr.BitmapEffectActivity;
import com.ael.celloscope.aelcelloscopeocr.R;
import com.ael.celloscope.aelcelloscopeocr.mediaeffects.MediaEffectsFragment;
import com.soundcloud.android.crop.Crop;

public class AELCropActivity extends Activity {
	private static final String TAG = AELCropActivity.class.getSimpleName();
	private static final int CAMERA_REQUEST = 0xffff55;
	Uri sourceUri;
	boolean cropDone = false;
	String imageDestination;;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		imageDestination = "file://"
				+ Environment.getExternalStorageDirectory() + "/ael_image.jpg";
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!cropDone) {
			sourceUri = Uri.parse("file://"
					+ Environment.getExternalStorageDirectory()
					+ "/ael_image.jpg");
			Intent photoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			photoIntent.putExtra(MediaStore.EXTRA_OUTPUT, sourceUri);
			startActivityForResult(photoIntent, CAMERA_REQUEST);
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
			cropDone = true;
			Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
			Crop.of(sourceUri, destination).withAspect(0, 1).start(this);
		} else if (requestCode == Crop.REQUEST_CROP) {
			Log.v("handle", "CROP__");
			handleCrop(resultCode, data);

		}
	}

	private void handleCrop(int resultCode, Intent result) {
		if (resultCode == RESULT_OK) {
			Log.v(TAG, "CROPPING DONE");
			/*setContentView(R.layout.activity_aelcrop);

			FragmentTransaction fragTransaction = getFragmentManager()
					.beginTransaction();
			MediaEffectsFragment fragment = new MediaEffectsFragment();
			Bundle bundle = new Bundle();
			bundle.putString("img_path", imageDestination);
			fragment.setArguments(bundle);
			fragTransaction.replace(R.id.media_effect_container, fragment);
			fragTransaction.commit();*/
			startActivity(new Intent(this, BitmapEffectActivity.class));

		} else if (resultCode == Crop.RESULT_ERROR) {
			Toast.makeText(this, Crop.getError(result).getMessage(),
					Toast.LENGTH_SHORT).show();
		}
	}
}
