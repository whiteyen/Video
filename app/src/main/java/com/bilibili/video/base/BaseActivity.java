package com.bilibili.video.base;

import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.bilibili.video.R;

import butterknife.ButterKnife;


public abstract class BaseActivity extends AppCompatActivity {
    protected Toolbar mToolbar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getLayoutId());
        ButterKnife.bind(this);

        initView();

        initData();

    }
    protected abstract int getLayoutId();
    protected abstract void initView() ;
    protected abstract void initData();
    protected <T extends View> T bindViewId(int resId){
        return (T) findViewById(resId);
    }
    protected void setSupportActionBar(){
        mToolbar = bindViewId(R.id.toolbar);
        if(mToolbar!=null){
           setSupportActionBar(mToolbar);
        }
    }
    protected void setActionBarIcon(int resId){
        if(mToolbar!=null){
            mToolbar.setNavigationIcon(resId);
        }
    }
}
