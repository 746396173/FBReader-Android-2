/***********************************************************************************
 *
 *  Copyright 2012 Yota Devices LLC, Russia
 *
 ************************************************************************************/

package com.yotadevices.fbreader;

import android.content.Intent;
import android.graphics.*;
import android.widget.FrameLayout;

import com.yotadevices.sdk.BSActivity;
import com.yotadevices.sdk.BSDrawer;
import com.yotadevices.sdk.Constants;
import com.yotadevices.sdk.BSMotionEvent;

import org.geometerplus.fbreader.fbreader.FBReaderApp;
import org.geometerplus.zlibrary.ui.android.library.ZLAndroidLibrary;
import org.geometerplus.zlibrary.ui.android.view.ZLAndroidWidget;

/**
 * @author ASazonov
 */
public class FBReaderYotaService extends BSActivity {
	private static final String TAG = FBReaderYotaService.class.getSimpleName();

	private ZLAndroidWidget myWidget;
	private Canvas mCanvas;
	private Bitmap mBitmap;
	private Bitmap mWhiteBitmap;

	@Override
	public void onBSResume() {
		super.onBSResume();
		initBookView();
		getBSDrawer().drawBitmap(0, 0, mBitmap, BSDrawer.Waveform.WAVEFORM_GC_FULL);
	}

	@Override
	public void onBSCreate() {
		super.onBSCreate();
		initBookView();
	}

	private void initBookView() {
		mWhiteBitmap = Bitmap.createBitmap(BSDrawer.SCREEN_WIDTH,
				BSDrawer.SCREEN_HEIGHT, Bitmap.Config.ARGB_8888);
		final Canvas c = new Canvas(mWhiteBitmap);
		final Paint paint = new Paint();
		paint.setColor(0xFFFFFF);
		c.drawRect(0, 0, BSDrawer.SCREEN_WIDTH, BSDrawer.SCREEN_HEIGHT, paint);
		mBitmap = Bitmap.createBitmap(BSDrawer.SCREEN_WIDTH,
				BSDrawer.SCREEN_HEIGHT, Bitmap.Config.ARGB_8888);
		mCanvas = new Canvas(mBitmap);
		myWidget = new ZLAndroidWidget(getApplicationContext(), true);
		if (FBReaderApp.Instance() == null) {
			FBReaderApp.createInstance(myWidget);
		}

		myWidget.setLayoutParams(new FrameLayout.LayoutParams(
				BSDrawer.SCREEN_WIDTH, BSDrawer.SCREEN_HEIGHT));
		myWidget.measure(BSDrawer.SCREEN_WIDTH, BSDrawer.SCREEN_HEIGHT);
		myWidget.layout(0, 0, BSDrawer.SCREEN_WIDTH, BSDrawer.SCREEN_HEIGHT);
		drawText();
	}

	private void drawText() {
		final ZLAndroidLibrary zlibrary = (ZLAndroidLibrary)ZLAndroidLibrary.Instance();
		if (zlibrary.YotaDrawOnBackScreenOption.getValue()) {
			myWidget.draw(mCanvas);
		}
	}	

	@Override
	protected void onVolumeButtonsEvent(Constants.VolumeButtonsEvent event) {
		super.onVolumeButtonsEvent(event);
		switch (event) {
			case VOLUME_MINUS_UP:
				handleGesture(Constants.Gestures.GESTURES_BS_RL);
				break;
			case VOLUME_PLUS_UP:
				handleGesture(Constants.Gestures.GESTURES_BS_LR);
				break;
			default:
				break;
		}
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		initBookView();
		getBSDrawer().drawBitmap(0, 0, mBitmap, BSDrawer.Waveform.WAVEFORM_GC_FULL);
	}

	@Override
	public void onBSTouchEvent(BSMotionEvent event) {
		handleGesture(event.getBSAction());
	}

	private void handleGesture(Constants.Gestures action) {
		if (action == Constants.Gestures.GESTURES_BS_RL) {
			myWidget.turnPageStatic(true);
			drawText();
			getBSDrawer().drawBitmap(0, 0, mBitmap, BSDrawer.Waveform.WAVEFORM_GC_FULL);
		} else if (action == Constants.Gestures.GESTURES_BS_LR) {
			myWidget.turnPageStatic(false);
			drawText();
			getBSDrawer().drawBitmap(0, 0, mBitmap, BSDrawer.Waveform.WAVEFORM_GC_FULL);
		}
	}
}
