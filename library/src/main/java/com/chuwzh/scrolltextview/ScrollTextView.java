package com.chuwzh.scrolltextview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

import com.example.scrolltextview.R;

import java.util.ArrayList;

/**
 * Created by chuwzh on 16-2-24.
 */
public class ScrollTextView extends TextView {

    private TextRender mTextRender;
    private int mTextColorSelected;

    public ScrollTextView(Context context) {
        this(context, null);
    }

    public ScrollTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrollTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ScrollTextView);
        mTextColorSelected = typedArray.getColor(R.styleable.ScrollTextView_textColorSelected, 0);
    }

    public void setTextRender(TextRender textRender) {
        mTextRender = textRender;
    }

    public int getSelectedColor() {
        return mTextColorSelected;
    }

    public void startScroll(ArrayList<String> textList) {
        mTextRender.startScroll(textList);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mTextRender.render(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mTextRender.onTouchEvent(event);
    }
}
