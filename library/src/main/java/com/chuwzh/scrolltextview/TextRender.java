package com.chuwzh.scrolltextview;

import android.graphics.Canvas;
import android.view.MotionEvent;

import java.util.ArrayList;

/**
 * Created by chuwzh on 16-3-1.
 */
public abstract class TextRender {

    protected ScrollTextView mScrollTextView;

    public TextRender(ScrollTextView scrollTextView) {
        this.mScrollTextView = scrollTextView;
    }

    protected abstract void startScroll(ArrayList<String> textList);

    protected abstract void render(Canvas canvas);

    protected abstract void stopScroll();

    protected abstract boolean onTouchEvent(MotionEvent event);
}
