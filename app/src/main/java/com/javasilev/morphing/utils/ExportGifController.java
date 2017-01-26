package com.javasilev.morphing.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;

import com.bumptech.glide.gifencoder.AnimatedGifEncoder;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Aleksei Vasilev.
 */

@SuppressWarnings("SpellCheckingInspection")
public class ExportGifController {
	private Observer<Boolean> mObserver;

	public ExportGifController(Observer<Boolean> observer) {
		mObserver = observer;
	}

	public void saveGif(AnimationDrawable animationDrawable, String rootDir) {
		Observable.fromCallable(() -> export(animationDrawable, rootDir))
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(mObserver);
	}

	@NonNull
	private Boolean export(AnimationDrawable animationDrawable, String rootDir) {
		String fileName = rootDir + "/" + getDateString() + ".gif";

		AnimatedGifEncoder encoder = new AnimatedGifEncoder();
		encoder.setDelay(animationDrawable.getDuration(0));
		encoder.setRepeat(1);
		encoder.start(fileName);

		if (animationDrawable.isRunning()) {
			animationDrawable.stop();
		}

		for (int i = 0; i < animationDrawable.getNumberOfFrames(); i++) {
			encoder.addFrame(((BitmapDrawable)animationDrawable.getFrame(i)).getBitmap());
		}

		return encoder.finish();
	}

	private String getDateString() {
		@SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("yyyyMMdd_hhmmss");
		return df.format(new Date());
	}
}
