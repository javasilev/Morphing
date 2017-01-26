package com.javasilev.morphing.ui;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.javasilev.morphing.R;
import com.javasilev.morphing.utils.ExportGifController;
import com.tbruyelle.rxpermissions.RxPermissions;

import net.rdrei.android.dirchooser.DirectoryChooserActivity;
import net.rdrei.android.dirchooser.DirectoryChooserConfig;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observer;

@SuppressWarnings({"FieldCanBeLocal", "SpellCheckingInspection"})
public class ExportActivity extends AppCompatActivity implements Observer<Boolean> {

	public static final String EXTRA_IMAGE_ID = "extra_image_id";
	public static final String EXTRA_OPACITY = "extra_opacity";
	public static final String EXTRA_STEPS = "extra_steps";
	public static final String EXTRA_DURATION = "extra_duration";
	public static final String APP_FOLDER = "morphing_gifs";
	private static final int REQUEST_DIRECTORY = 1;

	@BindView(R.id.activity_export_image_view_animation)
	ImageView mAnimationImageView;

	@BindView(R.id.activity_export_button_export)
	Button mExportButton;

	private int mTopImageId;

	private float mMaxOpacity;
	private int mSteps;
	private float mDuration;

	private int duration;
	private float maxAlpha;
	private int alphaStep;

	private AnimationDrawable mAnimationDrawable;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_export);
		setTitle(getString(R.string.export));
		ButterKnife.bind(this, this);

		initValues();

		AnimationDrawable animation = getAnimation();
		mAnimationImageView.setImageDrawable(animation);
		animation.setVisible(true, true);
		animation.start();

		mAnimationDrawable = getOptimizedAnimation(animation, duration);
	}

	private AnimationDrawable getAnimation() {
		AnimationDrawable animationDrawable = new AnimationDrawable();
		animationDrawable.setOneShot(false);

		for (int i = 0; i <= mSteps; i++) {
			Drawable background = ContextCompat.getDrawable(this, R.drawable.q01);
			Drawable top = ContextCompat.getDrawable(this, mTopImageId);

			if (i != mSteps) {
				top.setAlpha(i * alphaStep);
			} else {
				top.setAlpha((int) maxAlpha);
			}

			LayerDrawable layerDrawable = new LayerDrawable(new Drawable[] {background, top});

			Bitmap bitmap = Bitmap.createBitmap(top.getIntrinsicWidth(), top.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
			layerDrawable.setBounds(0, 0, top.getIntrinsicWidth(), top.getIntrinsicHeight());
			layerDrawable.draw(new Canvas(bitmap));

			BitmapDrawable frame = new BitmapDrawable(getResources(), bitmap);
			frame.setBounds(0, 0, top.getIntrinsicWidth(), top.getIntrinsicHeight());

			animationDrawable.addFrame(frame, duration);
		}

		return animationDrawable;
	}

	@SuppressWarnings("ResultOfMethodCallIgnored")
	@OnClick(R.id.activity_export_button_export)
	public void exportGif() {
		RxPermissions.getInstance(this)
				.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
				.subscribe(granted -> {
					if (granted) {
						final Intent chooserIntent = new Intent(this, DirectoryChooserActivity.class);

						final DirectoryChooserConfig config = DirectoryChooserConfig.builder()
								.newDirectoryName(APP_FOLDER)
								.allowReadOnlyDirectory(true)
								.allowNewDirectoryNameModification(true)
								.build();

						chooserIntent.putExtra(DirectoryChooserActivity.EXTRA_CONFIG, config);

						startActivityForResult(chooserIntent, REQUEST_DIRECTORY);
					} else {
						Toast.makeText(this, R.string.please_grant_permission, Toast.LENGTH_SHORT).show();
					}
				});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == REQUEST_DIRECTORY) {
			if (resultCode == DirectoryChooserActivity.RESULT_CODE_DIR_SELECTED) {

				String path = data.getStringExtra(DirectoryChooserActivity.RESULT_SELECTED_DIR);

				mExportButton.setEnabled(false);

				Toast.makeText(ExportActivity.this, R.string.saving, Toast.LENGTH_SHORT).show();

				ExportGifController controller = new ExportGifController(this);
				controller.saveGif(mAnimationDrawable, path);
			}
		}
	}

	private AnimationDrawable getOptimizedAnimation(AnimationDrawable animationDrawable, int duration) {
		float delta = 255 * 0.5f / 100; //minimum change == 0.5%
		int multiplier = (int) Math.ceil(delta / alphaStep);

		if (multiplier < 1) {
			multiplier = 1;
		}

		AnimationDrawable newAnimation = new AnimationDrawable();

		for (int i = 0; i < animationDrawable.getNumberOfFrames(); i += multiplier) {
			BitmapDrawable currentFrame = (BitmapDrawable) animationDrawable.getFrame(i);
			newAnimation.addFrame(currentFrame, duration * multiplier);
		}

		if (animationDrawable.getNumberOfFrames() % multiplier != 0) {
			newAnimation.addFrame(animationDrawable.getFrame(animationDrawable.getNumberOfFrames() - 1), duration * multiplier);
		}

		return newAnimation;
	}

	@Override
	public void onCompleted() {
		Toast.makeText(ExportActivity.this, R.string.gif_saved, Toast.LENGTH_SHORT).show();
		mExportButton.setEnabled(true);
	}

	@Override
	public void onError(Throwable e) {
		Toast.makeText(ExportActivity.this, R.string.export_failed, Toast.LENGTH_SHORT).show();
		mExportButton.setEnabled(true);
	}

	@Override
	public void onNext(Boolean b) {

	}

	private void initValues() {
		mTopImageId = getIntent().getIntExtra(EXTRA_IMAGE_ID, R.drawable.q02);
		mMaxOpacity = getIntent().getFloatExtra(EXTRA_OPACITY, 1.0f);
		mSteps = getIntent().getIntExtra(EXTRA_STEPS, TuneActivity.DEFAULT_STEPS);
		mDuration = getIntent().getFloatExtra(EXTRA_DURATION, TuneActivity.DEFAULT_DURATION);

		duration = (int) (mDuration * 1000 / mSteps);
		maxAlpha = 255 * mMaxOpacity;
		alphaStep = (int) Math.ceil(maxAlpha / mSteps);
	}
}
