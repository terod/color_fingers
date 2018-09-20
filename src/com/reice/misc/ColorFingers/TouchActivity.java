package com.reice.misc.ColorFingers;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
//import android.widget.RadioGroup;

/**
 * 
 * @author George Ebbinason
 * @version v1
 */
public class TouchActivity extends Activity {

	// min finger-pixel movement to indicate change change in position
	private static final int MIN_DXDY = 2;

	// Assume no more than 20 simultaneous touches
	//final private static int MAX_TOUCHES = 2;
	//wheel size
	private static int wheelSize = 800; 
	
	// Single marker
	private MarkerView marker;

	// bitmap for wheeler
	private Bitmap bitmap;

	// single wheeler
	private WheelView wheeler;

	// padding to centre the wheeler
	public static int padding = wheelSize / 2;

	// radius of the circle
	//final private int RADIUS = 250;

	// color calculator
	private ColorCalc colorC;
	private int rgb[];

	// Managing points
	private MPoint p1;
	private MPoint p2;
	//private MPoint p3;

	// screen size

	private DisplayMetrics mDisplayMetrics;
	private int mDisplayWidth;
	private int mDisplayHeight;

	// value variable in HSV
	double mValue = 1;

	// differnce saved
	double xDiff = 0;
	double yDiff = 0;

	// To find if second finger was used
	boolean newFinger = false;

	// Pool of MarkerViews
	//final private static LinkedList<View> mInactiveMarkers = new LinkedList<View>();

	// Set of MarkerViews currently visible on the display
	// Map is used to create a key-value pair
	// @SuppressLint("UseSparseArrays")
	//final private static Map<Integer, View> mActiveMarkers = new HashMap<Integer, View>();

	// choice of colors
	// private RadioGroup mGroupColor1;
	// private RadioGroup mGroupColor2;

	protected static final String TAG = "TouchActivity";
	protected static final String testTAG = "testTagforIndicate";

	private FrameLayout mFrame;
	//private RelativeLayout mMacbethFrame;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		// setting the reference to radioGroups

		wheeler = new WheelView(getApplicationContext(), 0, 0);
		marker = new MarkerView(getApplicationContext(), 0, 0);

		bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.a1);
		bitmap = Bitmap.createScaledBitmap(bitmap, wheelSize, wheelSize, false);

		mFrame = (FrameLayout) findViewById(R.id.frame);
		//mMacbethFrame = (RelativeLayout) findViewById(R.id.layout2);

		// to find the height and width of the screen
		mDisplayMetrics = new DisplayMetrics();
		TouchActivity.this.getWindowManager().getDefaultDisplay()
				.getMetrics(mDisplayMetrics);
		// get the display width and height
		mDisplayWidth = mDisplayMetrics.widthPixels;
		mDisplayHeight = mDisplayMetrics.heightPixels;
        
		//set the framewidth of color place to width -50pixels
		// Initialize pool of View.
		// initViews();

		// Create and set on touch listener
		mFrame.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				// storing the third finger movement
				//int thirdFingerID = 0;

				switch (event.getActionMasked()) {

				// Show new MarkerView
				// first finger for color1
				case MotionEvent.ACTION_DOWN: {

					int pointerIndex = event.getActionIndex();
					//int pointerID = event.getPointerId(pointerIndex);

					// saving into hasmap to save the pointer and wheel
					//mActiveMarkers.put(pointerID, wheeler);

					// for setting positions
					wheeler.setXLoc(event.getX(pointerIndex) - padding);
					wheeler.setYLoc(event.getY(pointerIndex) - padding);
					// create a marker view
					marker.setXLoc(event.getX(pointerIndex));
					marker.setYLoc(event.getY(pointerIndex));
					mFrame.addView(wheeler);

					// saving origin location
					p1 = new MPoint(event.getX(pointerIndex), event
							.getY(pointerIndex));
					// dummy initialization
					p2 = new MPoint(0, 0);
					colorC = new ColorCalc(p1, p2);
					break;
				}

				case MotionEvent.ACTION_POINTER_DOWN: {
					if (!newFinger) {
						int pointerIndex = event.getActionIndex();
						//int pointerID = event.getPointerId(pointerIndex);

						// saving into hasmap to save the pointer and wheel
						//mActiveMarkers.put(pointerID, marker);

						//Log.i("Test", "Something strage" + pointerIndex);

						marker.setXLoc(event.getX());
						marker.setYLoc(event.getY());

						// setting the point 2 location
						p2 = new MPoint(event.getX(pointerIndex), event
								.getY(pointerIndex));

						xDiff = p2.x - p1.x;
						yDiff = p2.y - p1.y;

						// calculating colorValues
						rgb = new int[3];
						colorC = new ColorCalc(p1, p2);
						// calculate with value = 1;
						rgb = colorC.calculateHSVtoRGB(
								colorC.calculateAngleNormalized(),
								colorC.calculateDistanceNormalized(), mValue);

						// set the color of marker based on calc
						marker.setColor(255, rgb[0], rgb[1], rgb[2]);
						mFrame.addView(marker);
						// since marker is put over wheeler
						mFrame.bringChildToFront(wheeler);

						// new finger has been used now
						newFinger = true;

						break;
					} else {
						int pointerIndex = event.getActionIndex();
						//int pointerID = event.getPointerId(pointerIndex);

						//Log.i("Test", "new finger detected after first time"
							//	+ pointerIndex);

						marker.setXLoc(event.getX());
						marker.setYLoc(event.getY());

						// setting the point 2 location
						p2.x = event.getX(pointerIndex);
						p2.y = event.getY(pointerIndex);

						xDiff = p2.x - p1.x;
						yDiff = p2.y - p1.y;

						// calculating colorValues
						rgb = new int[3];
						// calculate with value = 1;
						rgb = colorC.calculateHSVtoRGB(
								colorC.calculateAngleNormalized(),
								colorC.calculateDistanceNormalized(), mValue);

						// set the color of marker based on calc
						marker.setColor(255, rgb[0], rgb[1], rgb[2]);
						// since marker is put over wheeler
						mFrame.bringChildToFront(wheeler);

						break;
					}

					// // if (pointerIndex == 1) {
					// // setting the point 2 location
					// p2 = new MPoint(event.getX(pointerIndex), event
					// .getY(pointerIndex));
					//
					// // calculating colorValues
					// rgb = new int[3];
					// colorC = new ColorCalc(p1, p2);
					// // calculate with value = 1;
					// rgb = colorC.calculateHSVtoRGB(
					// colorC.calculateAngleNormalized(),
					// colorC.calculateDistanceNormalized(), mValue);
					//
					// // set the color of marker based on calc
					// marker.setColor(255, rgb[0], rgb[1], rgb[2]);
					// mFrame.addView(marker);
					// // since marker is put over wheeler
					// mFrame.bringChildToFront(wheeler);
					//
					// break;
					// }
					//
					// else {
					// double normalizedScreenHeight = event
					// .getY(pointerIndex) / mDisplayHeight;
					// //top is brighter and bottom is darker
					// mValue = 1 - normalizedScreenHeight;
					//
					// // store the id of third finger for movement traction
					// thirdFingerID = event.getPointerId(pointerIndex);
					//
					// // calculate with value set by user touch;
					// rgb = colorC.calculateHSVtoRGB(
					// colorC.calculateAngleNormalized(),
					// colorC.calculateDistanceNormalized(), mValue);
					//
					// // set the color of marker based on calc
					// marker.setColor(255, rgb[0], rgb[1], rgb[2]);
					// marker.invalidate();
					// mFrame.bringChildToFront(wheeler);
					// break;
					// }
				}

				// Remove one MarkerView
				case MotionEvent.ACTION_UP: {

					// int pointerIndex = event.getActionIndex();
					// int pointerID = event.getPointerId(pointerIndex);
					mFrame.removeView(marker);
					mFrame.removeView(wheeler);
					newFinger = false;
					if(rgb != null)
						Log.i("RGB Value","Red: "+ rgb[0] + " Green: " + rgb[1] + " Blue: " + rgb[2]);
					break;
				}

				// case MotionEvent.ACTION_POINTER_UP: {
				// int pointerIndex = event.getActionIndex();
				// if (pointerIndex == 1)
				// mFrame.removeView(marker);
				// break;
				//
				// }

				case MotionEvent.ACTION_MOVE: {

					// for (int idx = 0; idx < event.getPointerCount(); idx++) {

					// int ID = event.getPointerId(idx);

					// View view = mActiveMarkers.get(ID);
					// if (null != marker) {

					// Redraw only if finger has traveled a minimum distance
					if (event.getActionIndex() == 0)
						if (Math.abs(wheeler.getXLoc() - event.getX(0)) > MIN_DXDY
								|| Math.abs(wheeler.getYLoc() - event.getY(0)) > MIN_DXDY) {

							// Set new location

							wheeler.setXLoc(event.getX(0) - padding);
							wheeler.setYLoc(event.getY(0) - padding);

							marker.setXLoc(event.getX(0));
							marker.setYLoc(event.getY(0));

							// set color change
							double normalizedScreenHeight = event.getY(0)
									/ mDisplayHeight;
							// top is brighter and bottom is darker
							mValue = 1 - normalizedScreenHeight;

							// set new point 1 location
							p1.x = event.getX(0);
							p1.y = event.getY(0);

							p2.x = p1.x + xDiff;
							p2.y = p1.y + yDiff;

							// recalculate colors

							rgb = colorC.calculateHSVtoRGB(
									colorC.calculateAngleNormalized(),
									colorC.calculateDistanceNormalized(),
									mValue);
							marker.setColor(255, rgb[0], rgb[1], rgb[2]);

							// Request re-draw
							marker.invalidate();
							wheeler.invalidate();
						}
					if (event.getPointerCount() > 1) {
						//Log.i("Test", "Happy");
						if (Math.abs(marker.getXLoc() - event.getX(1)) > MIN_DXDY
								|| Math.abs(marker.getYLoc() - event.getY(1)) > MIN_DXDY) {
							p2.x = event.getX(1);
							p2.y = event.getY(1);

							// recalculate colors
							
							rgb = colorC.calculateHSVtoRGB(
									colorC.calculateAngleNormalized(),
									colorC.calculateDistanceNormalized(),
									mValue);
							marker.setColor(255, rgb[0], rgb[1], rgb[2]);
							
							
							//saving x and y diff
							xDiff = p2.x - p1.x ;
							yDiff = p2.y - p1.y;
							// Request re-draw
							marker.invalidate();
							wheeler.invalidate();

						}
					}
					// }
					// }

					break;
				}

				default:

					//Log.i(TAG, "unhandled action");
				}

				return true;
			}

		});
	}

	private class MarkerView extends View {
		private float mX, mY;
		// determines the size of the circle.
		final private int MAX_SIZE = 550;
		// initially assuming touches to be zero.
		//private int mTouches = 0;
		// creating a paint object to draw on the canvas
		final private Paint mPaint = new Paint();

		// when color is not chosen
		public MarkerView(Context context, float x, float y) {
			super(context);
			mX = x + padding;
			mY = y + padding;
			mPaint.setStyle(Style.FILL);

			Random rnd = new Random();
			mPaint.setARGB(255, rnd.nextInt(256), rnd.nextInt(256),
					rnd.nextInt(256));
		}

		// when some color is chosen
		public MarkerView(Context context, float x, float y, int red,
				int green, int blue) {
			super(context);
			mX = x;
			mY = y;
			mPaint.setStyle(Style.FILL);

			mPaint.setARGB(255, red, green, blue);
		}

		float getXLoc() {
			return mX;
		}

		void setXLoc(float x) {
			mX = x;
		}

		float getYLoc() {
			return mY;
		}

		void setYLoc(float y) {
			mY = y;
		}

//		void setTouches(int touches) {
//			mTouches = touches;
//		}

		void setColor(int alpha, int red, int green, int blue) {
			mPaint.setARGB(255, red, green, blue);
		}

		// this is called before the view is projected onto the screen.
		// canvas ref is sent by the android system.
		@Override
		protected void onDraw(Canvas canvas) {
			canvas.drawCircle(mX, mY, MAX_SIZE, mPaint);
		}
	}

	private class WheelView extends View {
		private float mX, mY;
		// determines the size of the circle.
		//final static private int MAX_SIZE = 200;
		// initially assuming touches to be zero.
		//private int mTouches = 0;
		// creating a paint object to draw on the canvas
		final private Paint mPaint = new Paint();

		// when color is not chosen
		public WheelView(Context context, float x, float y) {
			super(context);
			mX = x + padding;
			mY = y + padding;
			mPaint.setStyle(Style.FILL);

			Random rnd = new Random();
			mPaint.setARGB(220, rnd.nextInt(256), rnd.nextInt(256),
					rnd.nextInt(256));
		}

		// when some color is chosen
		public WheelView(Context context, float x, float y, int red, int green,
				int blue) {
			super(context);
			mX = x;
			mY = y;
			mPaint.setStyle(Style.FILL);

			mPaint.setARGB(256, red, green, blue);
		}

		float getXLoc() {
			return mX;
		}

		void setXLoc(float x) {
			mX = x;
		}

		float getYLoc() {
			return mY;
		}

		void setYLoc(float y) {
			mY = y;
		}

		/*void setTouches(int touches) {
			mTouches = touches;
		}

		String getType() {
			return "wheeler";

		}*/

		/*
		 * void setColor(int alpha, int red, int green, int blue) {
		 * mPaint.setARGB(220, red, green, blue); }
		 */

		// this is called before the view is projected onto the screen.
		// canvas ref is sent by the android system.
		@Override
		protected void onDraw(Canvas canvas) {
			canvas.drawBitmap(bitmap, mX, mY, mPaint);
		}
	}
	
	private class MacbethView extends View {
		private float mX, mY;
		// determines the size of the circle.
		final static private int MAX_SIZE = 550;
		// initially assuming touches to be zero.
		//private int mTouches = 0;
		// creating a paint object to draw on the canvas
		final private Paint mPaint = new Paint();
		private Rect macRect = new Rect();

		// when color is not chosen
		public MacbethView(Context context, float x, float y) {
			super(context);
			mX = x;
			mY = y;
			mPaint.setStyle(Style.FILL);

			Random rnd = new Random();
			mPaint.setARGB(255, rnd.nextInt(256), rnd.nextInt(256),
					rnd.nextInt(256));
		}

		// when some color is chosen
		public MacbethView(Context context, float x, float y, int red,
				int green, int blue) {
			super(context);
			mX = x;
			mY = y;
			mPaint.setStyle(Style.FILL);

			mPaint.setARGB(220, red, green, blue);
		}

		float getXLoc() {
			return mX;
		}

		void setXLoc(float x) {
			mX = x;
		}

		float getYLoc() {
			return mY;
		}

		void setYLoc(float y) {
			mY = y;
		}

//		void setTouches(int touches) {
//			mTouches = touches;
//		}

		void setColor(int red, int green, int blue) {
			mPaint.setARGB(255, red, green, blue);
		}

		// this is called before the view is projected onto the screen.
		// canvas ref is sent by the android system.
		@Override
		protected void onDraw(Canvas canvas) {
			macRect.set((int)mX, (int)mY, (int)mX + 100, (int)mY + mDisplayHeight);
			canvas.drawRect(macRect, mPaint);
		}
	}
	
	public void updateColor(int color[]){
		MacbethView mVRight = new MacbethView(getApplicationContext(), mDisplayWidth - 100, 0);
		MacbethView mVLeft = new MacbethView(getApplicationContext(), 0, 0);
		
		//setting up for right
		mVRight.setColor(color[0], color[1], color[2]);
		mFrame.addView(mVRight);
		
		//setting up for left
		mVLeft.setColor(color[0], color[1], color[2]);
		mFrame.addView(mVLeft);
		
		mVLeft.invalidate();
		mVRight.invalidate();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int color[] = new int[3];
		switch (item.getItemId()) {
		case R.id.darkSkin:
			color[0] = 116;
			color[1] = 81;
			color[2] = 67;
			updateColor(color);
			return true;
			
		case R.id.lightSkin:
			color[0] = 199;
			color[1] = 147;
			color[2] = 129;
			updateColor(color);
			return true;
			
		case R.id.blueSky:
			color[0] = 91;
			color[1] = 122;
			color[2] = 156;
			updateColor(color);
			return true;
			
		case R.id.foliage:
			color[0] = 90;
			color[1] = 108;
			color[2] = 64;
			updateColor(color);
			return true;
			
		case R.id.blueFlower:
			color[0] = 130;
			color[1] = 128;
			color[2] = 176;
			updateColor(color);
			return true;
		
		case R.id.bluishGreen:
			color[0] = 92;
			color[1] = 190;
			color[2] = 172;
			updateColor(color);
			return true;
			
		case R.id.orange:
			color[0] = 224;
			color[1] = 124;
			color[2] = 47;
			updateColor(color);
			return true;
			
		case R.id.purplishBlue:
			color[0] = 68;
			color[1] = 91;
			color[2] = 170;
			updateColor(color);
			return true;
			
		case R.id.moderateRed:
			color[0] = 198;
			color[1] = 82;
			color[2] = 97;
			updateColor(color);
			return true;
			
		case R.id.purple:
			color[0] = 94;
			color[1] = 58;
			color[2] = 106;
			updateColor(color);
			return true;
			
		case R.id.yellowGreen:
			color[0] = 159;
			color[1] = 189;
			color[2] = 63;
			updateColor(color);
			return true;
		
		case R.id.orangeYellow:
			color[0] = 230;
			color[1] = 162;
			color[2] = 39;
			updateColor(color);
			return true;
			
		case R.id.blue:
			color[0] = 35;
			color[1] = 63;
			color[2] = 147;
			updateColor(color);
			return true;
		
			
		case R.id.green:
			color[0] = 67;
			color[1] = 149;
			color[2] = 74;
			updateColor(color);
			return true;
			
		case R.id.red:
			color[0] = 180;
			color[1] = 49;
			color[2] = 57;
			updateColor(color);
			return true;
	
		case R.id.yellow:
			color[0] = 238;
			color[1] = 198;
			color[2] = 20;
			updateColor(color);
			return true;
			
		case R.id.magenta:
			color[0] = 193;
			color[1] = 84;
			color[2] = 151;
			updateColor(color);
			return true;
	
		case R.id.cyan:
			color[0] = 0;
			color[1] = 136;
			color[2] = 170;
			updateColor(color);
			return true;
			
		case R.id.white:
			color[0] = 245;
			color[1] = 245;
			color[2] = 243;
			updateColor(color);
			return true;
			
		case R.id.neutral8:
			color[0] = 200;
			color[1] = 202;
			color[2] = 202;
			updateColor(color);
			return true;
			
		case R.id.neutral65:
			color[0] = 161;
			color[1] = 163;
			color[2] = 163;
			updateColor(color);
			return true;
		
		case R.id.neutral5:
			color[0] = 121;
			color[1] = 121;
			color[2] = 122;
			updateColor(color);
			return true;
			
		case R.id.neutral35:
			color[0] = 82;
			color[1] = 84;
			color[2] = 86;
			updateColor(color);
			return true;
			
		case R.id.black:
			color[0] = 49;
			color[1] = 49;
			color[2] = 51;
			updateColor(color);
			return true;
			
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
