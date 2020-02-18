package com.bilibili.video.live;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;

public class MyDecoration extends RecyclerView.ItemDecoration {
    private Context mContext;
    public static final int []ATTR = new int []{
            android.R.attr.listDivider
    };
    private Drawable mDevider;
    public MyDecoration(Context context){
        mContext = context;
        TypedArray typedArray = context.obtainStyledAttributes(ATTR);
        mDevider = typedArray.getDrawable(0);
        typedArray.recycle();
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDraw(c, parent, state);
        drawHorizonalLine(c,parent,state);
    }

    private void drawHorizonalLine(Canvas c, RecyclerView parent, RecyclerView.State state) {
            int left = parent.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight();
            int childCount = parent.getChildCount();
            for(int i=0;i<childCount;i++){
                View childView = parent.getChildAt(i);
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)childView.getLayoutParams();
                int top = childView.getBottom() + params.bottomMargin;
                int bottom = top + mDevider.getIntrinsicHeight();
                mDevider.setBounds(left, top, right, bottom);
                mDevider.draw(c);
            }
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.set(0,0,0,mDevider.getMinimumHeight());
    }
}
