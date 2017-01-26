package com.javasilev.morphing.ui;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.javasilev.morphing.R;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

	@BindViews({
			R.id.activity_main_top_right,
			R.id.activity_main_bottom_left,
			R.id.activity_main_bottom_right
	})
	List<SelectImageWidget> mImages;

	@BindView(R.id.activity_main_button_next)
	Button mNextButton;

	private int mSelectedImageId;

	private static final ButterKnife.Action<SelectImageWidget> UNCHECK = (image, index) -> image.uncheck();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this, this);
		setTitle(getString(R.string.select));

		mNextButton.setEnabled(false);
	}

	@OnClick({
			R.id.activity_main_top_right,
			R.id.activity_main_bottom_left,
			R.id.activity_main_bottom_right
	})
	public void toggleCheck(SelectImageWidget image) {
		ButterKnife.apply(mImages, UNCHECK);
		image.check();
		mSelectedImageId = image.getImageId();
		if (!mNextButton.isEnabled()) {
			mNextButton.setEnabled(true);
		}
	}

	@OnClick(R.id.activity_main_button_next)
	public void nextStep() {
		Intent intent = new Intent(MainActivity.this, TuneActivity.class);
		intent.putExtra(TuneActivity.EXTRA_IMAGE_ID, mSelectedImageId);
		startActivity(intent);
	}
}
