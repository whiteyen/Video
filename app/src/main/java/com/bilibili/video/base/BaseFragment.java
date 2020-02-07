package com.bilibili.video.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

public abstract class BaseFragment extends Fragment {
    private View mContentView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mContentView = getActivity().getLayoutInflater().inflate(getLayoutID(), null,false);
        ButterKnife.bind(this, mContentView);
        initView();
        initData();
        return mContentView;
    }

    protected abstract void initData();
    protected abstract int getLayoutID();
    protected abstract void initView();
}
