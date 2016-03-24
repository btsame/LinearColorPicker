package com.cy.yangbo.linearcolorpicker;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Administrator on 2016/3/14.
 */
public class LinearColorPickerView  extends View {

    private int mColors[] = {//渐变色数组
            0xFF000000, 0xFFFF00FF, 0xFF0000FF, 0xFF00FFFF, 0xFF00FF00,
            0xFFFFFF00, 0xFFFF0000, 0xFFFFFFFF
    };

    /* private float mPositions[] = {
             0f, 0.2f, 0.36f, 0.4f, 0.56f, 0.68f, 0.8f, 1.0f
     };
    */
    private float mPositions[] = {
            0f, 0.25f, 0.38f, 0.42f, 0.55f, 0.65f, 0.75f, 1.0f
    };

    //默认选中的颜色
    private int defaultSelectedColor = 0xFFFF0000;
    private int defaultSelectedColorIndex = 1;
    private boolean mShowCursor = false;
    private int mCursorColor = 0xFFFFFFFF;
    private boolean mShowBorder = false;
    private float mBorderWidth = 0;
    private int mBorderColor = 0xFF00FF00;
    private boolean isVertical = false;
    private OnSelectedColorChanged mListener;

    private Paint mPaint, mCursorPaint, mBorderPaint;
    private LinearGradient mShaper;

    private float mCursorX = -1, mCursorY = -1;
    private final int CURSOR_WIDTH = 16;

    public LinearColorPickerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LinearColorPickerView);
        defaultSelectedColorIndex = a.getInt(R.styleable.LinearColorPickerView_selectedColor, defaultSelectedColorIndex);
        defaultSelectedColor = mColors[defaultSelectedColorIndex];
        mShowCursor = a.getBoolean(R.styleable.LinearColorPickerView_showCursor, mShowCursor);
        mCursorColor = a.getColor(R.styleable.LinearColorPickerView_cursorColor, mCursorColor);
        mShowBorder = a.getBoolean(R.styleable.LinearColorPickerView_showBorder, mShowBorder);
        mBorderWidth = a.getDimension(R.styleable.LinearColorPickerView_borderWidth, mBorderWidth);
        mBorderColor = a.getColor(R.styleable.LinearColorPickerView_borderColor, mBorderColor);
        isVertical = a.getBoolean(R.styleable.LinearColorPickerView_isVertical, isVertical);
        a.recycle();

        mPaint = new Paint();
        mCursorPaint = new Paint();
        mCursorPaint.setColor(mCursorColor);
        mCursorPaint.setStyle(Paint.Style.STROKE);
        mCursorPaint.setStrokeWidth(5);
        mBorderPaint = new Paint();
        mBorderPaint.setColor(mBorderColor);
    }

    public void setOnSelectedColorChangedListener(OnSelectedColorChanged listener){
        mListener = listener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //绘制边框
        if(mShowBorder){
            Rect[] borderRects = getBorderRects();
            canvas.drawRect(borderRects[0], mBorderPaint);
            canvas.drawRect(borderRects[1], mBorderPaint);
            canvas.drawRect(borderRects[2], mBorderPaint);
            canvas.drawRect(borderRects[3], mBorderPaint);
        }

        //绘制颜色选择区域
        Rect pickRect = getPickRegion();
        if(isVertical){
            mShaper = new LinearGradient(pickRect.left, pickRect.top, pickRect.left, pickRect.bottom, mColors, mPositions, Shader.TileMode.CLAMP);
        }else{
            mShaper = new LinearGradient(pickRect.left, pickRect.top, pickRect.right, pickRect.top, mColors, mPositions, Shader.TileMode.CLAMP);
        }
        mPaint.setShader(mShaper);
        canvas.drawRect(pickRect, mPaint);


        //绘制游标
        if(isVertical){
            if(mCursorY == -1){
                mCursorY = pickRect.height() * mPositions[defaultSelectedColorIndex];
            }
        }else{
            if(mCursorX == -1){
                mCursorX = pickRect.width() * mPositions[defaultSelectedColorIndex];
            }
        }

        if(mShowCursor){
            canvas.drawRoundRect(getCursorRect(), 2, 2, mCursorPaint);
        }
    }

    private Rect[] getBorderRects(){
        Rect[] results = new Rect[4];
        results[0] = new Rect(0, 0, (int)mBorderWidth, getHeight());//left
        results[1] = new Rect(0, 0, getWidth(), (int)mBorderWidth);//top
        results[2] = new Rect(getWidth() - (int)mBorderWidth, 0, getWidth(), getHeight());//right
        results[3] = new Rect(0, getHeight() - (int)mBorderWidth, getWidth(), getHeight());//bottom

        return results;
    }

    private Rect getPickRegion(){
        return new Rect((int)mBorderWidth, (int)mBorderWidth, getWidth() - (int)mBorderWidth, getHeight() - (int)mBorderWidth);
    }

    private RectF getCursorRect(){
        if(isVertical){
            return new RectF(0, mCursorY - CURSOR_WIDTH / 2, getWidth(), mCursorY + CURSOR_WIDTH / 2);
        }else{
            return new RectF(mCursorX - CURSOR_WIDTH / 2, 0, mCursorX + CURSOR_WIDTH / 2, getHeight());
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                if(isVertical){
                    mCursorY = event.getY();
                    if(mCursorY < mBorderWidth){
                        mCursorY = mBorderWidth;
                    }else if(mCursorY > getHeight() - mBorderWidth){
                        mCursorY = getHeight() - mBorderWidth;
                    }
                    if(mListener != null){
                        mListener.onSelectedColorChanged(interpColor(mColors, (mCursorY - mBorderWidth) / getPickRegion().height()));
                    }
                }else{
                    mCursorX = event.getX();
                    if(mCursorX < mBorderWidth){
                        mCursorX = mBorderWidth;
                    }else if(mCursorX > getWidth() - mBorderWidth){
                        mCursorX = getWidth() - mBorderWidth;
                    }
                    if(mListener != null){
                        mListener.onSelectedColorChanged(interpColor(mColors, (mCursorX - mBorderWidth) / getPickRegion().width()));
                    }
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                break;
            default:

        }
        return true;
    }

    private int interpColor(int colors[], float unit) {
        if (unit <= 0) {
            return colors[0];
        }
        if (unit >= 1) {
            return colors[colors.length - 1];
        }

        float p = 0;
        int i = 0;
        if(mPositions == null){
            p = unit * (colors.length - 1);
            i = (int)p;
            p -= i;
        }else{
            for(int j = 1; j < mPositions.length; j++){
                if(unit < mPositions[j]){
                    i = j - 1;
                    p = (unit - mPositions[i]) / (mPositions[j] - mPositions[i]);
                    break;
                }
            }
        }

        // now p is just the fractional part [0...1) and i is the index
        int c0 = colors[i];
        int c1 = colors[i+1];
        int a = ave(Color.alpha(c0), Color.alpha(c1), p);
        int r = ave(Color.red(c0), Color.red(c1), p);
        int g = ave(Color.green(c0), Color.green(c1), p);
        int b = ave(Color.blue(c0), Color.blue(c1), p);

        return Color.argb(a, r, g, b);
    }

    private int ave(int s, int d, float p) {
        return s + java.lang.Math.round(p * (d - s));
    }

    public interface OnSelectedColorChanged{
        void onSelectedColorChanged(int color);
    }
}