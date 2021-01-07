package com.example.myschool.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.res.ResourcesCompat;

import com.example.myschool.R;

public class EditTextWithClear extends AppCompatEditText {

    private Drawable clearButtonImage;

    public EditTextWithClear(Context context) {
        super(context);
        init();
    }

    public EditTextWithClear(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EditTextWithClear(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        clearButtonImage = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_clear_black_24dp, null);
        setOnTouchListener(new View.OnTouchListener(){

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(getCompoundDrawablesRelative()[2] != null){
                    float clearButtonStart;
                    float clearButtonEnd;
                    boolean isCLearbuttonClicked = false;

                    if(getLayoutDirection() == LAYOUT_DIRECTION_RTL){
                        clearButtonEnd = clearButtonImage.getIntrinsicWidth() + getPaddingStart();
                        if(event.getX() < clearButtonEnd)
                            isCLearbuttonClicked = true;
                    }
                    else{
                        clearButtonStart = (getWidth() - getPaddingEnd() - clearButtonImage.getIntrinsicWidth());
                        if(event.getX() > clearButtonStart)
                            isCLearbuttonClicked = true;
                    }

                    if(isCLearbuttonClicked){
                        if(event.getAction() == MotionEvent.ACTION_DOWN){
                            clearButtonImage = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_clear_dark_24dp, null);
                            showClearButton();
                        }
                        if(event.getAction() == MotionEvent.ACTION_UP){
                            clearButtonImage = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_clear_black_24dp, null);
                            getText().clear();
                            hideClearButton();
                            return true;
                        }
                    }
                    else
                        return false;
                }
                return false;
            }
        });

        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                showClearButton();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void showClearButton(){
        setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, clearButtonImage, null);
    }

    private void hideClearButton(){
        setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null);
    }
}


