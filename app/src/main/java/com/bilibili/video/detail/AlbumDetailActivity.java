package com.bilibili.video.detail;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bilibili.video.AppManger;
import com.bilibili.video.R;
import com.bilibili.video.api.OnGetAlbumDetailListener;
import com.bilibili.video.api.OnGetVideoPlayUrlListener;
import com.bilibili.video.api.SiteApi;
import com.bilibili.video.base.BaseActivity;
import com.bilibili.video.favorite.FavoriteDBHelper;
import com.bilibili.video.history.HistoryDBHelper;
import com.bilibili.video.model.Album;
import com.bilibili.video.model.ErrorInfo;
import com.bilibili.video.model.sohu.Video;
import com.bilibili.video.player.PlayActivity;
import com.bilibili.video.utils.ImageUtils;

import butterknife.BindView;

public class AlbumDetailActivity extends BaseActivity {

    public Album mAlbum;
    private static final String TAG = AlbumDetailActivity.class.getSimpleName();
    @BindView(R.id.iv_album_image)
    ImageView mAlbumImage;
    @BindView(R.id.tv_album_name)
    TextView mAlbumName;
    @BindView(R.id.tv_album_director)
    TextView mAlbumDirector;
    @BindView(R.id.tv_album_mainactor)
    TextView mAlbumMainactor;
    @BindView(R.id.tv_album_desc)
    TextView mAlbumDesc;
    @BindView(R.id.bt_super)
    Button mSuperBitstreamButtom;
    @BindView(R.id.bt_normal)
    Button mNormalBitstreamButtom;
    @BindView(R.id.bt_high)
    Button mHighBitstreamButtom;
    private boolean mIsFavor;
    private boolean mIsShowDesc;
    private int mVideoNo;
    private Fragment mFragment;
    private int mCurrentVideoPosition;
    private FavoriteDBHelper mFavoriteDBHelper;
    private HistoryDBHelper mHistoryDBHelper;

    public static void lauch(Activity activity, Album album, int vidoeNo, boolean isshow) {
        Intent intent = new Intent(activity, AlbumDetailActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("album", album);
        intent.putExtra("videoNo", vidoeNo);
        intent.putExtra("isShowDesc", isshow);
        activity.startActivity(intent);
    }

    public static void lauch(Activity activity, Album album) {
        Intent intent = new Intent(activity, AlbumDetailActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("album", album);
        activity.startActivity(intent);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_album_list;
    }

    @Override
    protected void initView() {
        mAlbum = getIntent().getParcelableExtra("album");
        mVideoNo = getIntent().getIntExtra("videoNo", 0);
        mIsShowDesc = getIntent().getBooleanExtra("isShowDesc", false);

        setSupportActionBar();
        setSupportArrowActionBar(true);
        setTitle(mAlbum.getTitle());

        mHistoryDBHelper = new HistoryDBHelper(this);
        mFavoriteDBHelper = new FavoriteDBHelper(this);
        mIsFavor = mFavoriteDBHelper.getAlbumById(mAlbum.getAlbumId(),mAlbum.getSite().getSiteId())!=null;


        mSuperBitstreamButtom.setOnClickListener(mOnSuperClickListener);
        mHighBitstreamButtom.setOnClickListener(mOnHighClickListener);
        mNormalBitstreamButtom.setOnClickListener(mOnNormalClickListener);


    }
    private View.OnClickListener mOnSuperClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            handleButtonClick(v);
        }
    };
    private View.OnClickListener mOnHighClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            handleButtonClick(v);
        }
    };
    private View.OnClickListener mOnNormalClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
                handleButtonClick(v);
        }
    };
    private void handleButtonClick(View v){
        Button button = (Button) v;
        String url = (String) button.getTag(R.id.key_video_url);
        int type = (int) button.getTag(R.id.key_video_stream);
        Video video = (Video) button.getTag(R.id.key_video);
        int currentPosition = (int) button.getTag(R.id.key_current_video_number);
        if(AppManger.isNetWorkAvailable()){
                mHistoryDBHelper.add(mAlbum);
                Intent intent = new Intent(AlbumDetailActivity.this,PlayActivity.class);
                intent.putExtra("url",url);
                intent.putExtra("type",type);
                intent.putExtra("currentPosition",currentPosition);
                intent.putExtra("video",video);
                startActivity(intent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home://左边箭头id
                finish();
                return true;
            case R.id.action_favor_item:
                if (mIsFavor) {
                    mIsFavor = false;
                    //收藏状态更新
                    mFavoriteDBHelper.delete(mAlbum.getAlbumId(),mAlbum.getSite().getSiteId());
                    invalidateOptionsMenu();
                    Toast.makeText(this, "已取消收藏", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.action_unfavor_item:
                if (!mIsFavor) {
                    mIsFavor = true;
                    mFavoriteDBHelper.add(mAlbum);
                    invalidateOptionsMenu();
                    Toast.makeText(this, "已添加收藏", Toast.LENGTH_SHORT).show();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.album_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem favitem = menu.findItem(R.id.action_favor_item);
        MenuItem unfavitem = menu.findItem(R.id.action_unfavor_item);
        favitem.setVisible(mIsFavor);
        unfavitem.setVisible(!mIsFavor);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void initData() {
        updateInfo();
        SiteApi.onGetAlbumDetail( mAlbum, new OnGetAlbumDetailListener() {
            @Override
            public void onGetAlbumDetailSuccess(final Album album) {
                mAlbum = album;
                // Log.d(TAG,">>onGetAlbumDetailSuccess album"+ album.getVideoTotal());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateInfo();
                        mFragment = AlbumPlayGridFragment.newInstance(mAlbum, mIsShowDesc, 0);
                        ((AlbumPlayGridFragment) mFragment).setPlayVideoSelectedListener(mPlayVideoSelectedListener);
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.fragment_container, mFragment).commit();
                        //  Log.d(TAG, String.valueOf(mFragment.getClass()));
                        getFragmentManager().executePendingTransactions();//延迟构造
                    }
                });

            }

            @Override
            public void onGetAlbumDetailFailed(ErrorInfo info) {

            }

        });


    }

    private void updateInfo() {
        mAlbumName.setText(mAlbum.getTitle());
        //Log.d(TAG,mAlbum.getDirector());
        // Log.d(TAG,mAlbum.getMainActor());
        if (!TextUtils.isEmpty(mAlbum.getDirector())) {
            mAlbumDirector.setText("导演: " + mAlbum.getDirector());
            mAlbumDirector.setVisibility(View.VISIBLE);
        } else {
            mAlbumDirector.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(mAlbum.getMainActor())) {
            mAlbumMainactor.setText("主演: " + mAlbum.getMainActor());
            mAlbumMainactor.setVisibility(View.VISIBLE);
        } else {
            mAlbumMainactor.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(mAlbum.getAlbumDesc())) {
            mAlbumDesc.setText("描述" + mAlbum.getAlbumDesc());
            mAlbumDesc.setVisibility(View.VISIBLE);
        } else {
            mAlbumDesc.setVisibility(View.GONE);
        }
        // 海报
        if (!TextUtils.isEmpty(mAlbum.getHorImgUrl())) {
            ImageUtils.displayImage(mAlbumImage, mAlbum.getHorImgUrl());
        } else if (!TextUtils.isEmpty(mAlbum.getVerImgUrl())) {
            ImageUtils.displayImage(mAlbumImage, mAlbum.getVerImgUrl());
        }
    }

    private AlbumPlayGridFragment.OnPlayVideoSelectedListener mPlayVideoSelectedListener
            = new AlbumPlayGridFragment.OnPlayVideoSelectedListener() {
        @Override
        public void OnPlayVideoSelected(Video video, int position) {
            mCurrentVideoPosition = position;
            SiteApi.onGetVideoPlayUrl(video, mVideoPlayUrlListener);
        }
    };
    private OnGetVideoPlayUrlListener mVideoPlayUrlListener = new OnGetVideoPlayUrlListener() {
        @Override
        public void onGetSuperUrl(final Video video,final String url) {
            Log.d(TAG, ">> onGetSuperUrl" + url + ",video" + video);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mSuperBitstreamButtom.setVisibility(View.VISIBLE);
                    mSuperBitstreamButtom.setTag(R.id.key_video_url,url);//视频url
                    mSuperBitstreamButtom.setTag(R.id.key_video,video);//视频info
                    mSuperBitstreamButtom.setTag(R.id.key_current_video_number,mCurrentVideoPosition);
                    mSuperBitstreamButtom.setTag(R.id.key_video_stream, SteamType.SUPER);
                }
            });

        }
           class SteamType{
            public static final int SUPER = 1;
            public static final int NORMAL = 2;
            public static final int HIGH = 3;

        }

        @Override
        public void onGetNormalUrl(final Video video,final  String url) {
            Log.d(TAG, ">> onGetNormalUrl" + url + ",video" + video);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mNormalBitstreamButtom.setVisibility(View.VISIBLE);
                    mNormalBitstreamButtom.setTag(R.id.key_video_url,url);//视频url
                    mNormalBitstreamButtom.setTag(R.id.key_video,video);//视频info
                    mNormalBitstreamButtom.setTag(R.id.key_current_video_number,mCurrentVideoPosition);
                    mNormalBitstreamButtom.setTag(R.id.key_video_stream, SteamType.NORMAL);
                }
            });

        }

        @Override
        public void onGetHighUrl(final Video video,final String url) {
            Log.d(TAG, ">> onGetHighUrl" + url + ",video" + video);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mHighBitstreamButtom.setVisibility(View.VISIBLE);
                    mHighBitstreamButtom.setTag(R.id.key_video_url,url);//视频url
                    mHighBitstreamButtom.setTag(R.id.key_video,video);//视频info
                    mHighBitstreamButtom.setTag(R.id.key_current_video_number,mCurrentVideoPosition);
                    mHighBitstreamButtom.setTag(R.id.key_video_stream, SteamType.HIGH);
                }
            });

        }

        @Override
        public void onGetFailed(ErrorInfo info) {

            Log.d(TAG, ">> onGetFailed");
            hideAllButton();//not show
        }
    };
    private void hideAllButton(){
        mSuperBitstreamButtom.setVisibility(View.GONE);
        mNormalBitstreamButtom.setVisibility(View.GONE);
        mHighBitstreamButtom.setVisibility(View.GONE);

    }


}
