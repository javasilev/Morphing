package com.javasilev.morphing.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.javasilev.morphing.R;

/**
 * Created by Aleksei Vasilev.
 */

public class SelectImageWidget extends FrameLayout {

	public static final float SELECTED_ALPHA = 0.65f;
	private ImageView mPhotoImageView;
	private ImageView mCheckImageView;

	private int mImageRes;

	public SelectImageWidget(Context context, AttributeSet attrs) {
		super(context, attrs);

		TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.SelectImageWidgetOptions, 0, 0);
		mImageRes = attributes.getResourceId(R.styleable.SelectImageWidgetOptions_src, R.drawable.q01);
		boolean selected = attributes.getBoolean(R.styleable.SelectImageWidgetOptions_selected, false);
		attributes.recycle();

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_image_select, this, true);

		mPhotoImageView = (ImageView) getChildAt(0);
		mPhotoImageView.setImageResource(mImageRes);
		mPhotoImageView.setAlpha(selected ? SELECTED_ALPHA : 1.0f);

		mCheckImageView = (ImageView) getChildAt(1);
		mCheckImageView.setVisibility(selected ? VISIBLE : INVISIBLE);
	}

	public void check() {
		mPhotoImageView.setAlpha(SELECTED_ALPHA);
		mCheckImageView.setVisibility(VISIBLE);
	}

	public void uncheck() {
		mPhotoImageView.setAlpha(1.0f);
		mCheckImageView.setVisibility(INVISIBLE);
	}

	public int getImageId() {
		return mImageRes;
	}
}
