package com.cy.yangbo.linearcolorpicker.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.cy.yangbo.linearcolorpicker.LinearColorPickerView;

public class MainActivity extends AppCompatActivity {

    TextView mHorizontalTV, mVerticalTV;
    LinearColorPickerView mHorizontalLCPV, mVerticfalLCPV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        setListener();
    }

    private void initView(){
        mHorizontalLCPV = (LinearColorPickerView) findViewById(R.id.lcpv_h);
        mVerticfalLCPV = (LinearColorPickerView) findViewById(R.id.lcpv_v);

        mHorizontalTV = (TextView) findViewById(R.id.tv_h);
        mVerticalTV = (TextView) findViewById(R.id.tv_v);
    }

    private void setListener(){
        mHorizontalLCPV.setOnSelectedColorChangedListener(new LinearColorPickerView.OnSelectedColorChanged() {
            @Override
            public void onSelectedColorChanged(int color) {
                mHorizontalTV.setTextColor(color);
            }
        });

        mVerticfalLCPV.setOnSelectedColorChangedListener(new LinearColorPickerView.OnSelectedColorChanged() {
            @Override
            public void onSelectedColorChanged(int color) {
                mVerticalTV.setTextColor(color);
            }
        });
    }
}
