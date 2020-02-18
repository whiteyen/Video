package com.bilibili.video.indicator;

import android.content.Context;
import android.database.DataSetObservable;
import android.database.DataSetObserver;

public abstract class ViewPagerIndicatorAdapter {
    public abstract int getCount();
    public abstract IPagerTitle getTitle(Context context,int index);
    public abstract IPagerIndicatorView getIndicator(Context context);

    public float getTitleWeight(){
        return 1;
    }
    private final DataSetObservable mDataSetObservable = new DataSetObservable();

    public  final void registerDataSetObservable(DataSetObserver dataSetObservable){
        mDataSetObservable.registerObserver(dataSetObservable);
    }
    public  final void unregisterDataSetObservable(DataSetObserver dataSetObservable){
        mDataSetObservable.unregisterObserver(dataSetObservable);
    }

    public final void notifySetDataChanged(){
        mDataSetObservable.notifyChanged();
    }

}
