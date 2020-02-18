package com.bilibili.video.widget;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.bilibili.video.R;

public class LoadingView extends LinearLayout {
    public LoadingView(Context context) {
        super(context);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.loading_view_layout,this);
    }
}
