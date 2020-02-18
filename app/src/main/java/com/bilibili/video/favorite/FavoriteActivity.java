package com.bilibili.video.favorite;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.TextView;

import com.bilibili.video.R;
import com.bilibili.video.base.BaseActivity;
import com.bilibili.video.model.Album;
import com.bilibili.video.model.AlbumList;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavoriteActivity extends BaseActivity {
    @BindView(R.id.gv_favorite)
    GridView mGridView;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.tv_empty)
    TextView mEmpty;
    private AlbumList mAlbumList = new AlbumList();
    private FavoriteDBHelper mFavoriteDBHelper;
    private FavoriteAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_favor;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_delete, menu);
        return true;
    }

    @Override
    protected void initView() {
        setSupportActionBar();
        setSupportArrowActionBar(true);
        setTitle("收藏");

        mSwipeRefreshLayout.setOnRefreshListener(mRefreshListener);
        mGridView.setOnScrollListener(mScrollListener);
        mFavoriteDBHelper = new FavoriteDBHelper(this);
        mAlbumList = mFavoriteDBHelper.getAllData();
        mAdapter = new FavoriteAdapter(this, mAlbumList);
        mGridView.setAdapter(mAdapter);
    }

    private GridView.OnScrollListener mScrollListener =
            new AbsListView.OnScrollListener() {

                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    boolean enable = false;
                    if (mGridView != null && mGridView.getChildCount() > 0) {
                        //第一个是否可见
                        boolean isFirstItemVisible = mGridView.getFirstVisiblePosition() == 0;
                        boolean topOfFirstItemVisible = (mGridView.getChildAt(0).getTop()) == 0;
                        enable = isFirstItemVisible && topOfFirstItemVisible;
                    }
                    //下拉刷新
                    mSwipeRefreshLayout.setEnabled(enable);

                }
            };
    private SwipeRefreshLayout.OnRefreshListener mRefreshListener
            = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            reloadData();
        }
    };
    public void reloadData(){
        mAlbumList = mFavoriteDBHelper.getAllData();
        mAdapter = new FavoriteAdapter(FavoriteActivity.this, mAlbumList);
        mGridView.setAdapter(mAdapter);
        mSwipeRefreshLayout.setRefreshing(false);
        mAdapter.setShowChecked(false);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home://左边箭头id
                finish();
                return true;
            case R.id.action_delete:
                if(mAdapter.isSelected()){
                    showDeleteDialog();
                }else {
                    setShowChecked(true);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setShowChecked(boolean isSelected){
        if(!isSelected){
            mAdapter.OptionCheckedAllItem(false);
        }
        mAdapter.setShowChecked(isSelected);
        mAdapter.notifyDataSetInvalidated();
    }

    private void  showDeleteDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage("是否确定删除？");
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                    deleteAllSelectedItem();
                    reloadData();
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //取消选中
                setShowChecked(false);
            }
        });
        dialog.show();
    }



    private void deleteAllSelectedItem() {
        List<Integer> list = new ArrayList<>();
        for(int i=0;i<mAdapter.getCount();i++){
            if(mAdapter.getItem(i).isChecked()){
                list.add(i);
            }
        }
        for(int k : list){
            Album album = mAdapter.getFavoriteList().get(k).getAlbum();
            mFavoriteDBHelper.delete(album.getAlbumId(),album.getSite().getSiteId());
        }
    }


    @Override
    protected void initData() {

    }

    public static void lauch(Context context) {
        Intent intent = new Intent(context, FavoriteActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
