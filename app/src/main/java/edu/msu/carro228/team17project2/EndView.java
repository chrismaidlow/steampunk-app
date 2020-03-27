package edu.msu.carro228.team17project2;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * View class for end screen
 */
public class EndView extends View{
    public EndView(Context context) {
        super(context);
        init();
    }

    public EndView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EndView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
