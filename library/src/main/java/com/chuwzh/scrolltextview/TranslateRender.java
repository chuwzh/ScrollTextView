package com.chuwzh.scrolltextview;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;

import java.util.ArrayList;

/**
 * Created by chuwzh on 16-3-1.
 */
public class TranslateRender extends TextRender {
    private final static String TAG = "TranslateRender";

    private static final float TEXT_GAP = 88; //px
    private static final float LEFT_STEP_LENGTH = -3.0f; //px
    private static final int INVALIDATE_INTERVAL = 30; //ms

    private Paint mPaint;
    private Paint mMeasurePaint;

    private ArrayList<String> mTextList;
    private ArrayList<TextItem> mTextItems;
    private int mFirstItemIndex;
    private boolean mScrolling;

    private int mActivePointerId;
    private float mLastMotionX;

    public TranslateRender(ScrollTextView scrollTextView) {
        super(scrollTextView);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mMeasurePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void startScroll(ArrayList<String> textList) {
        mTextList = textList;
        mScrolling = true;
        mScrollTextView.postInvalidate();
    }

    @Override
    public void render(Canvas canvas) {
        canvas.save();

        if (mScrolling) {
            autoFlush();
        }

        float startY = mScrollTextView.getBaseline();
        if (mTextItems != null) {
            for (TextItem textItem : mTextItems) {
                mPaint.setColor(textItem.textColor);
                mPaint.setTextSize(textItem.textSize);
                canvas.drawText(textItem.text, 0, textItem.text.length(), textItem.translate, startY, mPaint);
            }
        }

        canvas.restore();

        if (mScrolling) {
            mScrollTextView.postInvalidateDelayed(INVALIDATE_INTERVAL);
        }
    }

    @Override
    public void stopScroll() {

    }

    @Override
    protected boolean onTouchEvent(MotionEvent event) {
        int keyAction = event.getAction();
        switch (keyAction) {
            case MotionEvent.ACTION_DOWN:
                mScrolling = false;
                mActivePointerId = event.getPointerId(0);
                mLastMotionX = event.getX();
                highlightSelectText();
                break;
            case MotionEvent.ACTION_MOVE:
                int pointerIndex = event.findPointerIndex(mActivePointerId);
                if (pointerIndex == -1) {
                    break;
                }

                float x = event.getX(pointerIndex);
                dragFlush(x - mLastMotionX);

                mLastMotionX = event.getX(pointerIndex);
                mScrollTextView.postInvalidate();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                recoverTextColor();
                mScrolling = true;
                mScrollTextView.postInvalidate();
                break;
        }
        return true;
    }

    private void highlightSelectText() {
        if (mTextItems == null) {
            return;
        }
        for (TextItem textItem : mTextItems) {
            mMeasurePaint.setTextSize(textItem.textSize);
            if (Float.compare(mLastMotionX, textItem.translate) > 0
                    && Float.compare(mLastMotionX, textItem.translate + mMeasurePaint.measureText(textItem.text)) < 0) {
                textItem.textColor = mScrollTextView.getSelectedColor();
                mScrollTextView.postInvalidate();
                return;
            }
        }
    }

    private void recoverTextColor() {
        if (mTextItems == null) {
            return;
        }
        for (TextItem textItem : mTextItems) {
            textItem.textColor = mScrollTextView.getCurrentTextColor();
        }
        mScrollTextView.postInvalidate();
    }

    private void autoFlush() {
        if (mTextItems == null) {
            mFirstItemIndex = 0;
            mTextItems = new ArrayList<TextItem>();
            TextItem textItem = new TextItem();
            textItem.text = mTextList.get(0);
            textItem.translate = mScrollTextView.getCompoundPaddingLeft() + mScrollTextView.getPaddingLeft();
            textItem.textColor = mScrollTextView.getCurrentTextColor();
            textItem.textSize = mScrollTextView.getTextSize();
            mTextItems.add(textItem);
        } else {
            //计算第一个字符串是否还在显示范围内
            removeFirstItemIfNeed(LEFT_STEP_LENGTH);
            doTranslate(LEFT_STEP_LENGTH);
        }
        addItemToTailIfNeed();
    }

    private void dragFlush(float translate) {
        if (Float.compare(translate, 0) > 0) {
            removeFirstItemIfNeed(translate);
            addItemToHeadIfNeed();
        } else {
            removeLastItemIfNeed(translate);
            addItemToTailIfNeed();
        }
        doTranslate(translate);
    }

    private void doTranslate(float translate) {
        for (int i = 0; i < mTextItems.size(); i++) {
            TextItem textItem = mTextItems.get(i);
            textItem.translate += translate;
        }
    }

    private void removeFirstItemIfNeed(float step) {
        if (mTextItems == null) {
            return;
        }
        int size = mTextList.size();
        TextItem textItem = mTextItems.get(0);
        mMeasurePaint.setTextSize(textItem.textSize);
        if (textItem.translate + step + mMeasurePaint.measureText(textItem.text) <= 0) {
            mFirstItemIndex++;
            if (mFirstItemIndex >= size) {
                mFirstItemIndex = mFirstItemIndex % size;
            }

            mTextItems.remove(textItem);
        }
    }

    private void removeLastItemIfNeed(float step) {
        if (mTextItems == null) {
            return;
        }
        TextItem textItem = mTextItems.get(mTextItems.size() - 1);
        if (textItem.translate + step >= mScrollTextView.getMeasuredWidth()) {
            mTextItems.remove(textItem);
        }
    }

    private void addItemToHeadIfNeed() {
        if (mTextItems == null) {
            return;
        }
        TextItem headTextItem = mTextItems.get(0);
        if (headTextItem.translate > TEXT_GAP) {
            mFirstItemIndex--;
            if (mFirstItemIndex < 0) {
                mFirstItemIndex = mTextList.size() - 1;
            }
            int addIndex = (mFirstItemIndex) % mTextList.size();

            TextItem textItem = new TextItem();
            textItem.text = mTextList.get(addIndex);
            textItem.textColor = mScrollTextView.getCurrentTextColor();
            textItem.textSize = mScrollTextView.getTextSize();
            mMeasurePaint.setTextSize(textItem.textSize);
            textItem.translate = headTextItem.translate - mMeasurePaint.measureText(textItem.text) - TEXT_GAP;
            mTextItems.add(0, textItem);
        } else {
            return;
        }
    }

    private void addItemToTailIfNeed() {
        if (mTextItems == null) {
            return;
        }
        int size = mTextItems.size();
        for (int i = size - 1; i < size; i++) {
            TextItem tailTextItem = mTextItems.get(i);
            mMeasurePaint.setTextSize(tailTextItem.textSize);
            float tailOfText = tailTextItem.translate + mMeasurePaint.measureText(tailTextItem.text) + TEXT_GAP;
            if (tailOfText < mScrollTextView.getMeasuredWidth()) {
                int addIndex = (mFirstItemIndex + i + 1) % mTextList.size();

                TextItem textItem = new TextItem();
                textItem.text = mTextList.get(addIndex);
                textItem.translate = tailOfText;
                textItem.textColor = mScrollTextView.getCurrentTextColor();
                textItem.textSize = mScrollTextView.getTextSize();
                mTextItems.add(textItem);
                size++;
            } else {
                return;
            }
        }
    }
}
