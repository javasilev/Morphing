package com.javasilev.morphing.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.javasilev.morphing.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TuneActivity extends AppCompatActivity {

	public static final String EXTRA_IMAGE_ID = "extra_image_id";
	public static final int STEPS_COUNT = 15;
	public static final int DEFAULT_STEPS = 6;
	public static final float DEFAULT_DURATION = 2.0f;

	@BindView(R.id.activity_tune_image_view_above)
	ImageView mTopImageView;

	@BindView(R.id.activity_tune_seekbar_opacity)
	SeekBar mOpacitySeekBar;

	@BindView(R.id.activity_tune_spinner_steps)
	Spinner mStepsSpinner;

	@BindView(R.id.activity_tune_text_view_duration_sec)
	TextView mDurationTextView;

	@BindView(R.id.activity_tune_seekbar_duration)
	SeekBar mDurationSeekBar;

	private int mTopImageId;

	private float mOpacity;
	private int mSteps;
	private float mDuration;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tune);
		setTitle(getString(R.string.tune));
		ButterKnife.bind(this, this);

		mTopImageId = getIntent().getIntExtra(EXTRA_IMAGE_ID, R.drawable.q02);
		mTopImageView.setImageDrawable(ContextCompat.getDrawable(TuneActivity.this, mTopImageId));

		setOpacity();

		setSteps();

		setDuration();
	}

	private void setOpacity() {
		mOpacitySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				mOpacity = progress / 100f;
				mTopImageView.setAlpha(mOpacity);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}
		});
	}

	private void setSteps() {
		String[] values = new String[STEPS_COUNT];
		for (int i = 0; i < values.length; i++) {
			values[i] = "" + (i + 1);
		}

		mSteps = DEFAULT_STEPS;

		mStepsSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, values));
		mStepsSpinner.setSelection(mSteps - 1);
		mStepsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				mSteps = position + 1;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
 	}

	private void setDuration() {
		mDurationSeekBar.setProgress((int) (DEFAULT_DURATION * 10 - 5));

		mDuration = (mDurationSeekBar.getProgress() + 5) / 10f;
		setDurationText();

		mDurationSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				mDuration = (progress + 5) / 10f;
				setDurationText();
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}
		});
	}

	private void setDurationText() {
		mDurationTextView.setText(String.valueOf(mDuration));
	}

	@OnClick(R.id.activity_tune_button_next)
	public void nextStep() {
		Intent intent = new Intent(TuneActivity.this, ExportActivity.class);
		intent.putExtra(ExportActivity.EXTRA_IMAGE_ID, mTopImageId);
		intent.putExtra(ExportActivity.EXTRA_OPACITY, mOpacity);
		intent.putExtra(ExportActivity.EXTRA_STEPS, mSteps);
		intent.putExtra(ExportActivity.EXTRA_DURATION, mDuration);
		startActivity(intent);
	}
}
