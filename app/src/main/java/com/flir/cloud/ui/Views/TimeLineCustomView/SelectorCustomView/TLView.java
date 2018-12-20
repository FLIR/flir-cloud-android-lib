package com.flir.cloud.ui.Views.TimeLineCustomView.SelectorCustomView;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.flir.cloud.EventManager.LambdaAnalyticsEventManager;
import com.flir.cloud.MainApplication;
import com.flir.cloud.SharedPreferences.LambdaSharedPreferenceManager;
import com.flir.cloud.Utils.LambdaUtils;
import com.flir.cloud.ui.Access.AccessActivities.AccessCarouselFiles.AccessCameraItemView.IVideoViewAction;
import com.flir.cloud.ui.Access.AccessActivities.AccessCarouselFiles.CarouselEffectFiles.CarouselSettingsDialogFiles.CarouselSettingsDialogView;
import com.flir.cloud.ui.Views.TimeLineCustomView.GestureTap;
import com.flir.cloud.ui.Views.TimeLineCustomView.IUpdateTimeSelector;
import com.flir.sdk.Interceptors.PlaybackInterceptor;
import com.flir.sdk.models.Playback.ClipsResponse;
import com.flir.sdk.models.Playback.PlaybackUrlResponse;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

public class TLView extends View implements ISelectorView
{
	private static final long SEC_MILLIS = 1000;
	private static final long MIN_MILLIS = 60 * SEC_MILLIS;
	private static final long HOUR_MILLIS = 60 * MIN_MILLIS;
	private static final long DAY_MILLIS = HOUR_MILLIS * 24;

	private static final long LEFT_VALID_TIME_RANGE_IN_DAYS = 4;
	private static final long RIGHT_VALID_TIME_RANGE_IN_DAYS = 3;

	private static final long LEFT_VALID_TIME_RANGE_TO_DRAW = DAY_MILLIS * LEFT_VALID_TIME_RANGE_IN_DAYS;
	private static final long RIGHT_VALID_TIME_RANGE_TO_DRAW = DAY_MILLIS * RIGHT_VALID_TIME_RANGE_IN_DAYS;

	private static final long VALID_RANGE_TO_DRAW = DAY_MILLIS * 45;

	private static final long SIGNBOARD_WIDTH_IN_PIXELS = 200;
	private static final long SIGNBOARD_HEIGHT_IN_PIXELS = 100;

	private IUpdateTimeSelector mIUpdateTimeSelector;

	@Inject
	PlaybackInterceptor mPlaybackInterceptor;
	private LambdaAnalyticsEventManager mLambdaAnalyticsEventManager;
	private LambdaSharedPreferenceManager mLambdaSharedPreferenceManager;

	private TimeLineViewPresenter mTimeLineViewPresenter;

	private Paint blackline, redline, magentaline, redtext20, blacktext30, blacktext20, blacktext15, backgroundgradient;

	private GestureDetector mGestureTap;
	private Context mContext;
	private String mSerial;
	private IVideoViewAction mIVideoViewAction;
	private Canvas mTLVCanvas;
	/** width of view in pixels */
	int width;

	/** height of view in pixels */
	int height;

	/** left and right limit of the ruler in view, in milliseconds */
	long left, right;

	long oldLeft,oldRight;
	/** how many fingers are being used? 0, 1, 2 */
	int fingers;
	/** holds pointer id of #1/#2 fingers */
	int finger1id, finger2id;
	/** holds x/y in pixels of #1/#2 fingers from last frame */
	float finger1x, finger1y, finger2x, finger2y;

	private int ruleryHeight = 200;

	private SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

	/** width of the view in milliseconds, cached value of (right-left) */
	float span;
	/** how many pixels does each millisecond correspond to? */
	float pixels_per_milli;
	/** length in pixels of time units, at current zoom scale */
	float sec_pixels, min_pixels, hour_pixels, day_pixels;

	clip[] clipsArray;
	/** reusable calendar class object for rounding time to nearest applicable unit in onDraw */
	Calendar acalendar;

	public TLView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		mContext = context;
		mLambdaAnalyticsEventManager = new LambdaAnalyticsEventManager(mContext);
		init();
	}

	public TLView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		mContext = context;
		mLambdaAnalyticsEventManager = new LambdaAnalyticsEventManager(mContext);
		init();
	}

	public TLView(Context context)
	{
		super(context);
		mContext = context;
		mLambdaAnalyticsEventManager = new LambdaAnalyticsEventManager(mContext);
		init();
	}

	public void setInterfaceView(IUpdateTimeSelector v){
		mIUpdateTimeSelector = v;
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus)
	{
		super.onWindowFocusChanged(hasWindowFocus);
		// width/height are set here

		//backgroundgradient = new Paint();
        //backgroundgradient.setShader(new LinearGradient(0, 0, 0, getHeight(), Color.WHITE, 0xFFAAAAAA, Shader.TileMode.CLAMP));
	}

	private void init()
	{
		blackline = new Paint();
		blackline.setColor(Color.BLACK);
		blackline.setStrokeWidth(1f);
		blackline.setAntiAlias(true);
		blackline.setStyle(Style.STROKE);

		blacktext20 = new Paint();
		blacktext20.setColor(Color.BLACK);
		blacktext20.setStrokeWidth(1f);
		blacktext20.setAntiAlias(true);
		blacktext20.setStyle(Style.FILL);
		blacktext20.setTextSize(20);

		blacktext15 = new Paint();
		blacktext15.setColor(Color.BLACK);
		blacktext15.setStrokeWidth(1f);
		blacktext15.setAntiAlias(true);
		blacktext15.setStyle(Style.FILL);
		blacktext15.setTextSize(15);

		blacktext30 = new Paint();
		blacktext30.setColor(Color.BLACK);
		blacktext30.setStrokeWidth(1f);
		blacktext30.setAntiAlias(true);
		blacktext30.setStyle(Style.STROKE);
		blacktext30.setTextSize(30);

		redline = new Paint();
		redline.setColor(Color.RED);
		redline.setStrokeWidth(5f);
		redline.setAntiAlias(true);
		redline.setStyle(Style.STROKE);

		redtext20 = new Paint();
		redtext20.setColor(Color.RED);
		redtext20.setStrokeWidth(1f);
		redtext20.setAntiAlias(true);
		redtext20.setStyle(Style.FILL);
		redtext20.setTextSize(20);

		magentaline = new Paint();
		magentaline.setColor(Color.MAGENTA);
		magentaline.setStrokeWidth(1f);
		magentaline.setAntiAlias(true);
		magentaline.setStyle(Style.STROKE);

		acalendar = new GregorianCalendar();

		// start the view off somewhere, +/- some time around now
		left = System.currentTimeMillis() - (DAY_MILLIS / 2);
		right = System.currentTimeMillis() + (DAY_MILLIS / 2);
		span = right - left;
		///========================
		((MainApplication)mContext.getApplicationContext()).getApplicationComponent().inject(this);
		mGestureTap = new GestureDetector(mContext, new GestureTap(this));
		mTimeLineViewPresenter = new TimeLineViewPresenter(mPlaybackInterceptor,this);
		mLambdaSharedPreferenceManager = LambdaSharedPreferenceManager.getInstance();
	}


	public void initParam(String serial, IVideoViewAction aIVideoViewAction) {
		mSerial = serial;
		mIVideoViewAction = aIVideoViewAction;
	}

	private void updateClipsArray(List<ClipsResponse> aClipsResponse) {
		clip[] clips = new clip[aClipsResponse.size()];
		clip p;
		long start;
		long end;
		String timezone = LambdaSharedPreferenceManager.getInstance().getLambdaPrefsValue(mSerial + LambdaSharedPreferenceManager.DEVICE_SETTINGS_TIME_ZONE , CarouselSettingsDialogView.DEVICE_DEFAULT_TIME_ZONE);
		for (int i = 0; i < clips.length; i++) {
			start = LambdaUtils.fromISOtoTimestamp(aClipsResponse.get(i).startTime, timezone);
			end = LambdaUtils.fromISOtoTimestamp(aClipsResponse.get(i).endTime, timezone);
				if (start < end) {
					p = new clip(start, end);
					clips[i] = p;
				}
		}
		clipsArray = clips;
	}

	private String DayShort(int daynumber, boolean shortnotlong)
	{
		if (shortnotlong)
		{
			switch (daynumber % 7)
				{
				case 0:
					return "s";
				case 1:
					return "u";
				case 2:
					return "m";
				case 3:
					return "t";
				case 4:
					return "w";
				case 5:
					return "r";
				case 6:
					return "f";
				default:
					return "-";
				}
		}
		else
		{
			switch (daynumber % 7)
				{
				case 0:
					return "Saturday";
				case 1:
					return "Sunday";
				case 2:
					return "Monday";
				case 3:
					return "Tuesday";
				case 4:
					return "Wednesday";
				case 5:
					return "Thursday";
				case 6:
					return "Friday";
				default:
					return "derpDay";
				}
		}
	}


	public boolean checkLeftTimeInRange(long leftNew) {
		return leftNew >= System.currentTimeMillis() - LEFT_VALID_TIME_RANGE_TO_DRAW;
	}

	public boolean checkRightTimeInRange(long rightNew) {
		return rightNew <= System.currentTimeMillis() + RIGHT_VALID_TIME_RANGE_TO_DRAW;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		mTLVCanvas = canvas;
		long next, now;

		width = getWidth(); // width of view in pixels
		height = getHeight(); // height of view in pixels
		ruleryHeight = (int) (height * 0.70);
		// calculate span/width
		pixels_per_milli = (float) width / (float) span;
		sec_pixels = (float) SEC_MILLIS * pixels_per_milli;
		min_pixels = (float) MIN_MILLIS * pixels_per_milli;
		hour_pixels = (float) HOUR_MILLIS * pixels_per_milli;
		day_pixels = (float) DAY_MILLIS * pixels_per_milli;

		// draw background
		//canvas.drawPaint(backgroundgradient);
		//draw Signboard on canvas
		//drawSignboard(now, canvas)
		// draw 'now' line

		now = System.currentTimeMillis();
		if (left < now && now < right) {
			float dist = (now - left) / span;
			int nowx = (int) (dist * width);
			canvas.drawLine(nowx, ruleryHeight - 80, nowx, ruleryHeight + 80, redline);
		//  draw signboard to current time
		//	canvas.drawRect(nowx - SIGNBOARD_WIDTH_IN_PIXELS / 2, ruleryHeight + 50 ,nowx + SIGNBOARD_WIDTH_IN_PIXELS / 2, ruleryHeight + 50 + SIGNBOARD_HEIGHT_IN_PIXELS, redline );
		}

		// draw finger circles
		/*if (fingers > 0) {
			canvas.drawCircle(finger1x, finger1y, 60, redline);
			if (fingers > 1) {
				canvas.drawCircle(finger2x, finger2y, 60, magentaline);
			}
		}*/

		// draw ruler
		canvas.drawLine(0, ruleryHeight, width, ruleryHeight, blackline);

		// round calendar down to leftmost hour
		acalendar.setTimeInMillis(left);
		// floor the calendar to various time units to find where (in ms) they start
		acalendar.set(Calendar.MILLISECOND, 0); // second start
		acalendar.set(Calendar.SECOND, 0); // minute start
		acalendar.set(Calendar.MINUTE, 0); // hour start

		// draw Minutes
		if (min_pixels > 5) {
			drawMinutes(canvas);
		}

		// draw hours
		if (hour_pixels > 2) {
			drawHours(canvas);
		}

		// draw months
		drawMonths(canvas);

		//===============================
		if(clipsArray != null) {
			drawVideoClipsOnCanvas(mTLVCanvas, clipsArray);
		}

		//Draw Go To Line
		canvas.drawLine(getWidth() / 2, ruleryHeight - 50, getWidth() / 2, ruleryHeight + 50, redline);

		//Update left & right time signboards
		if(mIUpdateTimeSelector != null) {
			mIUpdateTimeSelector.updateLeftText(getLeftDateString() +"\n" + getLeftTimeString());
			mIUpdateTimeSelector.updateRightText(getRightDateString() +"\n" + getRightTimeString());
			mIUpdateTimeSelector.updateGoToText(getGoToDateString() +"\n" + getGoToTimeString());
		}
	}

	private void drawMonths(Canvas canvas) {
		long next;// round calendar down to leftmost month
		acalendar.set(Calendar.HOUR_OF_DAY, 0); // day start
		acalendar.set(Calendar.DAY_OF_MONTH, 1); // month start
		next = acalendar.getTimeInMillis(); // set to start of leftmost month
		do {
			// draw each month
			int monthnumber = acalendar.get(Calendar.MONTH);
			int daysthismonth = acalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
			String monthnamelong = acalendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US);
			String monthnameshort = acalendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US);
			int daynumber = acalendar.get(Calendar.DAY_OF_WEEK);

			long daymsx = next; // first day starts at start of month
			int x = (int) ((daymsx - left) / span * width); // convert to pixels
			canvas.drawLine(x, ruleryHeight - 100, x, ruleryHeight + 20, blackline);

			if (monthnumber == 0) {
				canvas.drawLine(x, ruleryHeight - 130, x, ruleryHeight - 110, blackline);
				String year = Integer.toString(acalendar.get(Calendar.YEAR));
				canvas.drawText(year, x + 8, ruleryHeight - 167, blacktext30);
			}

			// draw month names
			if (day_pixels < 1) ;
			else if (day_pixels < 2) {
				// sideways month name
				canvas.save();
				canvas.rotate(-90, x, ruleryHeight);
				canvas.drawText(monthnameshort, x + 50, ruleryHeight + 30, blacktext30);
				canvas.restore();
			} else if (day_pixels < 5) {
				// short month name
				canvas.drawText(monthnameshort, x + 5, ruleryHeight - 80, blacktext30);
			} else {
				// long month name
				canvas.drawText(monthnamelong, x + 5, ruleryHeight - 80, blacktext30);
			}

			// draw days, weeks
			for (int date = 1; date <= daysthismonth; date++, daynumber++, daymsx += DAY_MILLIS) {
				x = (int) ((daymsx - left) / span * width);

				if (daynumber == 7) daynumber = 0;

				if (day_pixels < 3) ;
				if (day_pixels < 10) {
					// really tiny days
					if (daynumber == 1)
						canvas.drawLine(x, ruleryHeight - 10, x, ruleryHeight + 10, blackline);
				} else if (day_pixels < 40) {
					// tiny days
					if (daynumber == 1)
						canvas.drawLine(x, ruleryHeight - 50, x, ruleryHeight + 10, blackline);
					else
						canvas.drawLine(x, ruleryHeight - 50, x, ruleryHeight, blackline);
				} else if (day_pixels > 170) {
					// big days
					if (daynumber == 1)
						canvas.drawLine(x, ruleryHeight - 100, x, ruleryHeight + 50, blackline);
					else
						canvas.drawLine(x, ruleryHeight - 100, x, ruleryHeight + 10, blackline);

					canvas.drawText(Integer.toString(date), x + 10, ruleryHeight - 40, blacktext30);
					canvas.drawText(DayShort(daynumber, false), x + 10, ruleryHeight - 10, blacktext30);
				} else {
					// sideways days
					if (daynumber == 1)
						canvas.drawLine(x, ruleryHeight - 60, x, ruleryHeight + 30, blackline);
					else
						canvas.drawLine(x, ruleryHeight - 60, x, ruleryHeight, blackline);

					canvas.save();
					canvas.rotate(-90, x, ruleryHeight);
					canvas.drawText(Integer.toString(date), x + 10, ruleryHeight + 30, blacktext30);
					canvas.drawText(DayShort(daynumber, true), x + 40, ruleryHeight + 30, blacktext30);
					canvas.restore();
				}
			}

			acalendar.add(Calendar.MONTH, 1);
			next = acalendar.getTimeInMillis();
		} while (next < right);
	}

	private void drawHours(Canvas canvas) {
		long next;
		int thehourofday = acalendar.get(Calendar.HOUR_OF_DAY);
		next = acalendar.getTimeInMillis(); // set to start of leftmost hour

		for (long i = next; i < right; i += HOUR_MILLIS) {
            float x = ((float) (i - left) / span * (float) width);
            int h24 = thehourofday % 24;
            int h12 = thehourofday % 12;
            if (h12 == 0) h12 = 12;

            if (hour_pixels < 4) {
                if (h24 == 12)
                    canvas.drawLine(x, ruleryHeight - 10, x, ruleryHeight + 10, blackline);
                if (h12 == 6)
                    canvas.drawLine(x, ruleryHeight - 10, x, ruleryHeight, blackline);
            } else if (hour_pixels < 20) {
                if (h24 == 12)
                    canvas.drawLine(x, ruleryHeight - 10, x, ruleryHeight + 10, blackline);
                else if (h12 == 6)
                    canvas.drawLine(x, ruleryHeight - 10, x, ruleryHeight, blackline);
                else if ((h12 == 3) || (h12 == 9))
                    canvas.drawLine(x, ruleryHeight - 5, x, ruleryHeight, blackline);
            } else if (hour_pixels < 60) {
                if (h24 == 12) {
                    canvas.drawLine(x, ruleryHeight - 20, x, ruleryHeight + 10, blackline);
                    if (h24 % 6 == 0)
                        drawHourText(canvas, x, ruleryHeight - 20, h24, h12);
                } else if (h12 == 6) {
                    canvas.drawLine(x, ruleryHeight - 15, x, ruleryHeight, blackline);
                    if (h12 % 3 == 0)
                        drawHourText(canvas, x, ruleryHeight - 20, h24, h12);
                } else if ((h12 == 3) || (h12 == 9)) {
                    canvas.drawLine(x, ruleryHeight - 10, x, ruleryHeight, blackline);
                } else
                    canvas.drawLine(x, ruleryHeight - 5, x, ruleryHeight, blackline);
            } else if (hour_pixels < 80) {
                if (h24 == 12) {
                    canvas.drawLine(x, ruleryHeight - 30, x, ruleryHeight + 15, blackline);
                } else if (h12 == 6) {
                    canvas.drawLine(x, ruleryHeight - 20, x, ruleryHeight + 10, blackline);
                } else if ((h12 == 3) || (h12 == 9)) {
                    canvas.drawLine(x, ruleryHeight - 15, x, ruleryHeight + 5, blackline);
                } else {
                    canvas.drawLine(x, ruleryHeight - 10, x, ruleryHeight, blackline);
                }
                if (h24 % 3 == 0)
                    drawHourText(canvas, x, ruleryHeight - 30, h24, h12);
            } else {
                if (h24 == 12) {
                    canvas.drawLine(x, ruleryHeight - 30, x, ruleryHeight + 15, blackline);
                } else if (h12 == 6) {
                    canvas.drawLine(x, ruleryHeight - 20, x, ruleryHeight + 10, blackline);
                } else if ((h12 == 3) || (h12 == 9)) {
                    canvas.drawLine(x, ruleryHeight - 15, x, ruleryHeight + 5, blackline);
                } else {
                    canvas.drawLine(x, ruleryHeight - 10, x, ruleryHeight, blackline);
                }
                //if(h24 % 2 == 0)
                drawHourText(canvas, x, ruleryHeight - 30, h24, h12);
            }

            thehourofday++;

            /*for (int j = (int) (x + (float)min_pixels/6f); j < x + min_pixels && j < right; j += (float)min_pixels/6f)
            {
                canvas.drawLine(i, ruleryHeight-40, i, ruleryHeight+10, blackline);
            }*/
        }
	}

	private void drawMinutes(Canvas canvas) {
		long next;
		int minOfHour = acalendar.get(Calendar.MINUTE);
		next = acalendar.getTimeInMillis(); // set to start of leftmost hour
		for (long i = next; i < right; i += MIN_MILLIS) {
            float x = ((float) (i - left) / span * (float) width);
            int h60 = minOfHour % 60;

            if (min_pixels < 7.5) {

                if (h60 % 10 == 0) {
                    canvas.drawLine(x, ruleryHeight - 10, x, ruleryHeight + 10, blackline);
                }
                if (h60 % 30 == 0) {
                    canvas.drawText(String.valueOf(h60), x - 10, ruleryHeight + 40, blacktext30);
                }
            } else if (min_pixels < 12) {
                if (h60 % 5 == 0) {
                    canvas.drawLine(x, ruleryHeight - 10, x, ruleryHeight + 10, blackline);
                }
                if (h60 % 10 == 0) {
                    canvas.drawText(String.valueOf(h60), x - 10, ruleryHeight + 40, blacktext30);
                }
            } else {
                canvas.drawLine(x, ruleryHeight - 10, x, ruleryHeight + 10, blackline);
                if (h60 % 10 == 0) {
                    canvas.drawText(String.valueOf(h60), x - 10, ruleryHeight + 40, blacktext30);
                }
            }
            minOfHour++;
        }
	}

	private void drawVideoClipsOnCanvas(Canvas canvas, clip[] p) {

		Paint ClipPaint = new Paint();
		ClipPaint.setStyle(Style.FILL);
		ClipPaint.setARGB(127,20,43,255);
		//ClipPaint.setStrokeWidth(50);
		for (clip aP : p) {
			if (aP != null) {
				float dist = (aP.start - left) / span;
				int startX = (int) (dist * width);

				float distX = (aP.end - left) / span;
				int endX = (int) (distX * width);

				canvas.drawRect(startX, ruleryHeight - 100, endX, ruleryHeight + 100, ClipPaint);
			}
		}

	}

	private String getLeftTimeString() {
		return mDateFormat.format(left).split(" ")[1];
	}

	private String getRightTimeString() {
		return mDateFormat.format(right).split(" ")[1];
	}

	private String getLeftDateString() {
		return mDateFormat.format(left).split(" ")[0];
	}

	private String getRightDateString() {
		return mDateFormat.format(right).split(" ")[0];
	}
	private String getGoToTimeString() {
		return mDateFormat.format((left + right)/ 2).split(" ")[1];
	}

	private String getGoToDateString() {
		return mDateFormat.format((left + right)/ 2).split(" ")[0];
	}


	private void drawSignboard(long now, Canvas canvas) {

		Paint myPaint = new Paint();
		myPaint.setStyle(Style.STROKE);
		myPaint.setColor(Color.rgb(0, 0, 0));
		myPaint.setStrokeWidth(10);

		Paint textPaint = new Paint();
		textPaint.setColor(Color.BLACK);
		textPaint.setTextSize(26);

		long diff = (right - left);
		long ep = diff / width;
		long fingerXTime = (long) (left + (ep * finger1x));

		SimpleDateFormat newDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String datetimeFingerX = newDateFormat.format(fingerXTime);
		String[] dateSpiltArrayFingerX = datetimeFingerX.split(" ");
		String dateNowFingerX = dateSpiltArrayFingerX[0];
		String timeNowFingerX = dateSpiltArrayFingerX[1];
		//dateFormat.format(left);

		//System.out.println(datetime);
		Rect bounds = new Rect();
		//textPaint = textView.getPaint();
		textPaint.getTextBounds(dateNowFingerX, 0, dateNowFingerX.length(), bounds);
		int textWidth = bounds.width();
		int textHeight = bounds.height();
		//Log.d("textWidth",textWidth + "");


		int nowx = (int) finger1x;
		canvas.drawLine(nowx, ruleryHeight - 50, nowx, ruleryHeight + 50, redline);
		canvas.drawRect(nowx - SIGNBOARD_WIDTH_IN_PIXELS / 2, ruleryHeight + 50, nowx + SIGNBOARD_WIDTH_IN_PIXELS / 2, ruleryHeight + 50 + SIGNBOARD_HEIGHT_IN_PIXELS, myPaint);

		canvas.drawText(dateNowFingerX, 0, dateNowFingerX.length(), nowx - (textWidth / 2), ruleryHeight + 50 + (SIGNBOARD_HEIGHT_IN_PIXELS / 2 - (textHeight / 2)), textPaint);
		canvas.drawText(timeNowFingerX, 0, timeNowFingerX.length(), nowx - (textWidth / 2), ruleryHeight + 50 + (SIGNBOARD_HEIGHT_IN_PIXELS) / 2 + (textHeight), textPaint);


	}

	private void drawHourText(Canvas canvas, float x, float y, int h24, int h12)
	{
		if (h24 < 12)
			canvas.drawText(Integer.toString(h12) + "am", x, y, blacktext30);
		else
			canvas.drawText(Integer.toString(h12) + "pm", x, y, blacktext30);
	}

	@Override
	public boolean onTouchEvent(MotionEvent motionevent)
	{
		mGestureTap.onTouchEvent(motionevent);

		switch (motionevent.getActionMasked())
			{
			// First finger down, start panning
			case MotionEvent.ACTION_DOWN:

				fingers = 1; // panning mode

				// save id and coords
				finger1id = motionevent.getPointerId(motionevent.getActionIndex());
				finger1x = motionevent.getX();
				finger1y = motionevent.getY();

				invalidate(); // redraw
				return true;

				// Second finger down, start scaling
			case MotionEvent.ACTION_POINTER_DOWN:

				if (fingers == 2) // if already tracking 2 fingers
					break; // ignore 3rd finger
				// else fingers == 1
				fingers = 2; // scaling mode

				// save id and coords
				finger2id = motionevent.getPointerId(motionevent.getActionIndex());
				finger2x = motionevent.getX(finger2id);
				finger2y = motionevent.getY(finger2id);

				invalidate(); // redraw
				return true;

			case MotionEvent.ACTION_MOVE:

				if (fingers == 0) // if not tracking fingers as down
					return false; // ignore move events

				float new1x,
				new1y,
				new2x,
				new2y; // Hold new positions of two fingers

				// get finger 1 position
				int pointerindex = motionevent.findPointerIndex(finger1id);
				if (pointerindex == -1) // no change
				{
					new1x = finger1x; // use values from previous frame
					new1y = finger1y;
				} else
				// changed
				{
					// get new values
					new1x = motionevent.getX(pointerindex);
					new1y = motionevent.getY(pointerindex);
				}

				// get finger 2 position
				pointerindex = motionevent.findPointerIndex(finger2id);
				if (pointerindex == -1)
				{
					new2x = finger2x;
					new2y = finger2y;
				} else
				{
					new2x = motionevent.getX(pointerindex);
					new2y = motionevent.getY(pointerindex);
				}

				// panning
				if (fingers == 1)
				{
					// how far to scroll in milliseconds to match the scroll input in pixels
					long delta1xinmillis = (long) ((finger1x - new1x) * span / width); // (deltax)*span/width
																						// = delta-x
																						// in
																						// milliseconds
					left += delta1xinmillis;
					right += delta1xinmillis;
					invalidate();

				}
				// scaling
				else if (fingers == 2)
				{
					// don't scale if fingers too close, or past each other
					if (Math.abs(new1x - new2x) < 10) return true;
					if (finger1x > finger2x) if (new1x < new2x) return true;
					if (finger1x < finger2x) if (new1x > new2x) return true;
					
					// find ruler time in ms under each finger at start of move
					// y = mx+b, b = left, span = right - left [ms]
					double m = (double) span / (double) width; // m = span/width
					double y1 = m * finger1x + left; // ms at finger1
					double y2 = m * finger2x + left; // ms at finger2
					// y values are set to the millisecond time shown at the old finger1x and
					// finger2x, using old left and right span
					// construct a new line equation through points (new1x,y1),(new2x,y2)
					// f(x) = y1 + (x - new1x) * (y2 - y1) / (new2x - new1x)

					if(right - left <  VALID_RANGE_TO_DRAW) {
						oldLeft = left;
						oldRight = right;
						left = (long) (y1 + (0 - new1x) * (y2 - y1) / (new2x - new1x));
						right = (long) (y1 + (width - new1x) * (y2 - y1) / (new2x - new1x));
						span = right - left; // span of milliseconds in view
						invalidate();
					}else {
						left = oldLeft;
						right = oldRight;
						span = right - left;
					}
				}

				// save
				finger1x = new1x;
				finger1y = new1y;
				finger2x = new2x;
				finger2y = new2y;

				//invalidate(); // redraw with new left,right
				return true;

			case MotionEvent.ACTION_POINTER_UP:
				int id = motionevent.getPointerId(motionevent.getActionIndex());

				if (id == finger1id)
				{
					// 1st finger went up, make 2nd finger new firstfinger and go back to panning
					finger1id = finger2id;
					finger1x = finger2x; // copy coords so view won't jump to other finger
					finger1y = finger2y;
					fingers = 1; // panning
				} else if (id == finger2id)
				{
					// 2nd finger went up, just go back to panning
					fingers = 1; // panning
				} else
				{
					return false; // ignore 3rd finger ups
				}
				invalidate(); // redraw
				return true;

			case MotionEvent.ACTION_CANCEL:
				return true;

			case MotionEvent.ACTION_UP:
				// last pointer up, no more motionevents
				fingers = 0;
				invalidate(); // redraw
				return true;
			}
		return super.onTouchEvent(motionevent);
	}

	public void moveToCurrentTime() {
		left = System.currentTimeMillis() - (DAY_MILLIS / 2);
		right = System.currentTimeMillis() + (DAY_MILLIS / 2);
		span = right - left;
	}

	public void doOnSingleTapClicked(float x) {

		DisplayMetrics metrics = new DisplayMetrics();
		((Activity)mContext).getWindowManager().getDefaultDisplay().getMetrics(metrics);
		long timestampTapPosition = left + ((long) ((right - left) / (metrics.widthPixels / x)));
		String isoString = LambdaUtils.fromTimestampToISO(timestampTapPosition);

		mTimeLineViewPresenter.getPlaybackStreamUrl(mSerial, mLambdaSharedPreferenceManager.getSelectedChannel(mSerial), isoString);
		moveTimeLineToTapPosition(timestampTapPosition);

		mLambdaAnalyticsEventManager.sendEvent(LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_CATEGORY_CAMERA_ITEM_ACTIVITY,
				LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_ACTION_TIME_LINE_VIEW_TAPED,
				LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_EVENT_SEND_REQUEST , mSerial + "/" + isoString);
	}

	private void moveTimeLineToTapPosition(long timestampTapPosition) {
		long centerPosition =  left + (right - left) / 2;
		if(timestampTapPosition  < centerPosition){
			startTimeLineMovementEffect(centerPosition, timestampTapPosition, true);
		}else{
			startTimeLineMovementEffect(centerPosition ,timestampTapPosition, false);
		}

	}

	private void startTimeLineMovementEffect(long centerPosition, long aTimestampTapPosition, boolean leftSideClicked) {
		long timeToMove;

		ValueAnimator animation = ValueAnimator.ofFloat(0, 100);
		if(leftSideClicked){
			timeToMove = centerPosition - aTimestampTapPosition;
			//32 is the avr times for this duration.
			long interval = timeToMove / 32;
			animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
				@Override
				public void onAnimationUpdate(ValueAnimator animation) {
					left -= interval;
					right -= interval;
					invalidate(); 
				}
			});

		}else{
			timeToMove = aTimestampTapPosition - centerPosition;
			long interval = timeToMove / 32;
			animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
				@Override
				public void onAnimationUpdate(ValueAnimator animation) {
					left += interval;
					right += interval;
					invalidate();
				}
			});

		}

		animation.addListener(new Animator.AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animation) {

			}

			@Override
			public void onAnimationEnd(Animator animation) {
				if (centerPosition != aTimestampTapPosition) {
					left = aTimestampTapPosition - ((long) span / 2);
					right = aTimestampTapPosition + ((long) span / 2);
					span = right - left;
					invalidate();
				}
			}

			@Override
			public void onAnimationCancel(Animator animation) {

			}

			@Override
			public void onAnimationRepeat(Animator animation) {

			}
		});
		animation.setInterpolator(new AccelerateDecelerateInterpolator());
		animation.setDuration(500);
		animation.start();

	}


	@Override
	public void response(PlaybackUrlResponse aPlaybackUrlResponse) {
		mIVideoViewAction.playPlaybackVideo(aPlaybackUrlResponse.url);
		mIVideoViewAction.removeThumbnail();
	}

	@Override
	public void response(List<ClipsResponse> aClipsResponse) {
		updateClipsArray(aClipsResponse);
		//Refresh View
		invalidate();
		requestLayout();
	}

	@Override
	public void sendEvent(String aCategory, String aAction, String aEvent, String aComment) {
		mLambdaAnalyticsEventManager.sendEvent(aCategory, aAction, aEvent, aComment);
	}

	public void getClipsFromServer() {
		mTimeLineViewPresenter.getPlaybackClips(mSerial,  mLambdaSharedPreferenceManager.getSelectedChannel(mSerial), getLeftTimeString(), getRightTimeString());
	}

    public void takeTimeLineForward(long timeToJump) {
        left += timeToJump;
        right += timeToJump;
        invalidate();
    }


    public class clip {
		long start;
		long end;

		public clip() {
		}

		public clip(long start, long end) {
			this.start = start;
			this.end = end;
		}
	}

}
