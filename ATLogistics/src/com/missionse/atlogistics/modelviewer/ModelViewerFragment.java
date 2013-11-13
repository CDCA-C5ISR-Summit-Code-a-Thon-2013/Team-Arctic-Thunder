package com.missionse.atlogistics.modelviewer;

import java.util.ArrayList;

import rajawali.RajawaliFragment;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.missionse.atlogistics.R;
import com.missionse.atlogistics.gesturedetector.PanGestureDetector;
import com.missionse.atlogistics.gesturedetector.RotationGestureDetector;

public class ModelViewerFragment extends RajawaliFragment implements OnTouchListener, ObjectLoadedListener {
	private ModelViewerRenderer renderer;
	private ProgressBar progressBar;

	private ModelViewerGestureListener gestureListener;
	private GestureDetector gestureDetector;
	private ScaleGestureDetector scaleGestureDetector;
	private RotationGestureDetector rotationGestureDetector;
	private PanGestureDetector panGestureDetector;
	private ArrayList<ObjectLoadedListener> objectLoadedListeners;

	public ModelViewerFragment() {
		objectLoadedListeners = new ArrayList<ObjectLoadedListener>();
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setGLBackgroundTransparent(true);

		renderer = new ModelViewerRenderer(getActivity(), this);
		renderer.setSurfaceView(mSurfaceView);
		setRenderer(renderer);

		gestureListener = new ModelViewerGestureListener();
		gestureListener.setController(getController(), getAnimator());

		gestureDetector = new GestureDetector(getActivity(), gestureListener);
		scaleGestureDetector = new ScaleGestureDetector(getActivity(), gestureListener);
		rotationGestureDetector = new RotationGestureDetector(gestureListener);
		panGestureDetector = new PanGestureDetector(gestureListener);

		mSurfaceView.setOnTouchListener(this);
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		mLayout = (FrameLayout) inflater.inflate(R.layout.fragment_model, container, false);

		if (mSurfaceView.getParent() != null) {
			((ViewGroup) mSurfaceView.getParent()).removeView(mSurfaceView);
		}

		mSurfaceView.setId(R.id.content_frame);
		mLayout.addView(mSurfaceView);

		mLayout.findViewById(R.id.progress_bar_container).bringToFront();
		progressBar = (ProgressBar) mLayout.findViewById(R.id.progress_bar);

		mLayout.findViewById(R.id.model_resource_container).bringToFront();

		return mLayout;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		renderer.onSurfaceDestroyed();
	}

	protected void showLoader() {
		if (!loaderShown()) {
			progressBar.post(new Runnable() {
				@Override
				public void run() {
					progressBar.setVisibility(View.VISIBLE);
				}
			});
		}
	}

	protected void hideLoader() {
		if (loaderShown()) {
			progressBar.post(new Runnable() {
				@Override
				public void run() {
					progressBar.setVisibility(View.GONE);
				}
			});
		}
	}

	protected boolean loaderShown() {
		return progressBar.isShown();
	}

	@Override
	public boolean onTouch(final View v, final MotionEvent event) {
		boolean touchConsumed = false;
		if (gestureListener != null) {
			scaleGestureDetector.onTouchEvent(event);
			rotationGestureDetector.onTouchEvent(event);
			panGestureDetector.onTouchEvent(event);
			gestureDetector.onTouchEvent(event);
			touchConsumed = true;
		}

		return touchConsumed;
	}

	@Override
	public void onObjectLoaded() {
		for (ObjectLoadedListener listener : objectLoadedListeners) {
			listener.onObjectLoaded();
		}
	}

	public void registerObjectLoadedListener(final ObjectLoadedListener listener) {
		objectLoadedListeners.add(listener);
	}

	public ModelController getController() {
		return renderer.getController();
	}

	public ModelAnimationController getAnimator() {
		return renderer.getAnimator();
	}
}
