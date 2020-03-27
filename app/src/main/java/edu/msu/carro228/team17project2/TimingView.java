package edu.msu.carro228.team17project2;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class TimingView extends View {

    /**
     * The image bitmap for displaying the timer. None initially.
     */
    private Bitmap timerBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.streamgauge), 350, 350, true);

    /**
     * Paint for drawing the hand on the timer.
     */
    private Paint timerHandPaint;

    /**
     * Current timer needle angle.
     */
    private float cAngle;

    /**
     * Time remaining on the animation.
     */
    private long cTime;

    private static String remTime = "Time";

    private static String curAngle = "Angle";

    /**
     * Animator for our line rotation.
     */
    private ValueAnimator animator;

    /**
     * Paint for drawing the center point on the timer.
     */
    private Paint timerMiddlePaint;

    /**
     * X value for the center of the timer.
     */
    private float startX = timerBitmap.getWidth()/2;

    /**
     * Y value for the center of the timer.
     */
    private float startY = timerBitmap.getHeight()/2;

    /**
     * X value of endpoint of the timer needle that isnt in the center.
     */
    private float newX = startX-(startX*.441f);

    /**
     * Y value of endpoint of the timer needle that isnt in the center.
     */
    private float newY = startY+(startX*.441f);

    /**
     * X value of the of the timer needle finish point.
     */
    private float endX = startX+(startX*.441f);

    /**
     * Y value of the of the timer needle finish point.
     */
    private float endY = startY+(startX*.441f);

    /**
     * Constructor
     */
    public TimingView(Context context)
    {
        super(context);
        init();
    }

    /**
     * Constructor
     */
    public TimingView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    /**
     * Constructor
     */
    public TimingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    /**
     * Initializes the timing view
     */
    private void init() {
        timerHandPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        timerHandPaint.setColor(Color.RED);
        timerHandPaint.setStrokeWidth((int)(.04f*startX));

        timerMiddlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        timerMiddlePaint.setColor(Color.BLACK);
        reset_timer();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float wid = canvas.getWidth();
        float hit = canvas.getHeight();

        float iWid = .5f * timerBitmap.getWidth();
        float iHit = .5f * timerBitmap.getHeight();

        // Determine the top and left margins to center
        float marginLeft = (wid - iWid) / 2;
        float marginTop = (hit - iHit) / 2;

        canvas.scale(0.5f, 0.5f);

        canvas.drawBitmap(timerBitmap, 0, 0, null);

        canvas.drawLine(startX, startY, newX, newY, timerHandPaint);

        canvas.drawCircle(startX, startY, (int)(.0882f*startX), timerMiddlePaint);
    }

    /**
     * creates and executes the animation for rotating the line around the center of the timer
     */
    public void rotate_line() {


        animator = ValueAnimator.ofFloat(cAngle, (float)Math.toRadians(0));
        animator.setDuration(cTime);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float x = (float) animation.getAnimatedValue();

                cAngle = x;
                cTime = animation.getDuration()-animation.getCurrentPlayTime();

                newX = (float)(startX + (endX-startX)*Math.cos(x) - (endY-startY)*Math.sin(x));
                newY = (float)(startY + (endX-startX)*Math.sin(x) + (endY-startY)*Math.cos(x));
                invalidate();
            }

        });
        animator.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                GameActivity host = (GameActivity) getActivity();
                host.incrementMisses();
                host.activityEndTurn();
            }
        });
        animator.start();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int width = timerBitmap.getWidth()/2;
        int height = timerBitmap.getHeight()/2;

        setMeasuredDimension(width, height);
    }

    /**
     * Resets the timer animation variables
     */
    public void reset_timer()
    {
        if (animator != null) {
            animator.removeAllListeners();
            animator.removeAllUpdateListeners();
        }
        cAngle = (float)Math.toRadians(-270);
        cTime = 30000;
    }

    /**
     * Save the view state to a bundle.
     * @param bundle bundle to save to
     */
    public void putToBundle(Bundle bundle) {
        if (animator != null) {
            animator.removeAllListeners();
            animator.removeAllUpdateListeners();
        }
        bundle.putLong(remTime, cTime);
        bundle.putFloat(curAngle, cAngle);
    }

    /**
     * Get the view state from a bundle
     * @param bundle bundle to load from
     */
    public void getFromBundle(Bundle bundle) {
        cTime = bundle.getLong(remTime);
        cAngle = bundle.getFloat(curAngle);
        rotate_line();
    }

    /**
     * Get the current activity
     */
    private Activity getActivity() {
        Context context = getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity)context;
            }
            context = ((ContextWrapper)context).getBaseContext();
        }
        return null;
    }
}
