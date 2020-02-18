package com.bilibili.video.live;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.bilibili.video.R;
import com.bilibili.video.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LiveActivity extends BaseActivity {
    @BindView(R.id.ry_live)
    RecyclerView mRecyclerView;

    public static void lauch(Activity activity) {
        Intent intent = new Intent(activity, LiveActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        activity.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_live;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void initView() {
        setSupportActionBar();
        setSupportArrowActionBar(true);
        setTitle("电视节目直播");
        GridLayoutManager manager
                = new GridLayoutManager(this,1);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setFocusable(false);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.addItemDecoration(new MyDecoration(this));
        mRecyclerView.setAdapter(new LiveItemAdapter(this));
        mRecyclerView.scrollToPosition(0);//回到第一个位置
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
