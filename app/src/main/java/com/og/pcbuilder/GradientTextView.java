package com.og.pcbuilder;

import android.content.Context;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatTextView;

public class GradientTextView extends AppCompatTextView {
    public GradientTextView(Context context) {
        super(context);
    }

    public GradientTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GradientTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            getPaint().setShader(new LinearGradient(
                    0, 0, getPaint().measureText(getText().toString()), 0,
                    new int[]{
                            0xFF6094EA,
                            0xE6AA4FF4,
                            0xB3FF00FF,
                            0xFFFF35A3
                    },
                    new float[]{0.0f, 0.39f, 0.76f, 0.94f},
                    Shader.TileMode.CLAMP
            ));
        }
    }
} 