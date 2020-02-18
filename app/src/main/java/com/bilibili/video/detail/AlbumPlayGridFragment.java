package com.bilibili.video.detail;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bilibili.video.R;
import com.bilibili.video.api.OnGetVideoListener;
import com.bilibili.video.api.SiteApi;
import com.bilibili.video.base.BaseFragment;
import com.bilibili.video.model.Album;
import com.bilibili.video.model.ErrorInfo;
import com.bilibili.video.model.sohu.Video;
import com.bilibili.video.model.sohu.VideoList;
import com.bilibili.video.widget.CustomGridView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class AlbumPlayGridFragment extends BaseFragment {
    private static final String TAG = AlbumPlayGridFragment.class.getSimpleName();
    private static final String ARGS_ALBUM = "album";
    private static final String ARGS_IS_SHOWDESC = "isShowDesc";
    private static final String ARGS_INIT_POSITION = "initVideoPosition";

    private Album mAlbum;
    private int mInitPosition;
    private boolean mIsShowDesc;
    private int mPageNo;
    private int mPageSize;
    private VideoItemAdapter mVideoItemAdatper;
    private CustomGridView mCustomGridView;
    private int mPageTotal;
    private View mEmptyView;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private boolean mIsFristSelection = true;

    private int mCurrentPosition;

    private OnPlayVideoSelectedListener mOnPlayVideoSelectedListener;
    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x11) {
                Log.d(TAG, String.valueOf(mVideoItemAdatper.getCount()));
                mVideoItemAdatper.notifyDataSetChanged();
               // mCustomGridView.setAdapter(mVideoItemAdatper);
            }
        }
    };



    public void setPlayVideoSelectedListener(OnPlayVideoSelectedListener listener) {
        mOnPlayVideoSelectedListener = listener;
    }
    public interface OnPlayVideoSelectedListener{
        void OnPlayVideoSelected(Video video, int position);
    }

    public AlbumPlayGridFragment() {

    }

    public static AlbumPlayGridFragment newInstance(Album album, boolean isShowDesc, int initVideoPosition) {
        AlbumPlayGridFragment fragment = new AlbumPlayGridFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARGS_ALBUM, album);
        bundle.putBoolean(ARGS_IS_SHOWDESC, isShowDesc);
        bundle.putInt(ARGS_INIT_POSITION, initVideoPosition);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, ">> oncreate ");
        if (getArguments() != null) {
            mAlbum = getArguments().getParcelable(ARGS_ALBUM);
            mIsShowDesc = getArguments().getBoolean(ARGS_IS_SHOWDESC);
            mInitPosition = getArguments().getInt(ARGS_INIT_POSITION);
            mCurrentPosition = mInitPosition;
            mPageNo = 0;
            mPageSize = 200;
            mPageTotal = (mAlbum.getVideoTotal() + mPageSize -1)/ mPageSize;
            loadData();
        }
    }

    private OnVideoSelectedListener mVideoSelectedListner = new OnVideoSelectedListener() {

        @Override
        public void onVideoSelected(Video video, int position) {
            if(mCustomGridView!=null){
              //  Log.d(TAG, String.valueOf(position));
                mCustomGridView.setSelection(position);
                mCustomGridView.setItemChecked(position, true);
                mCurrentPosition = position;
                if (mOnPlayVideoSelectedListener != null) {
                    mOnPlayVideoSelectedListener.OnPlayVideoSelected(video, position);
                }
            }
        }
    };

    private void loadData() {
        Log.d(TAG, ">> loadData ");
        mPageNo ++;
        SiteApi.onGetVideo(mPageSize, mPageNo, mAlbum, new OnGetVideoListener() {
            @Override
            public void onGetVideoSuccess(VideoList videoList) {
                for (Video video : videoList) {
                    mVideoItemAdatper.addVideo(video);
                   // Log.d(TAG, ">> OnGetVideoSuccess video " + video.toString());
                    //Log.d(TAG, ">> OnGetVideoSuccess video " +  mVideoItemAdatper.getCount());

                }
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mEmptyView.setVisibility(View.GONE);
                        if(mVideoItemAdatper.getCount()>mInitPosition && mIsFristSelection ){
                            mCustomGridView.setSelection(mInitPosition);
                            mCustomGridView.setItemChecked(mInitPosition,true);
                            mIsFristSelection = true;
                            SystemClock.sleep(100);
                            mCustomGridView.smoothScrollToPosition(mInitPosition);
                        }
                    }
                });
                Message message=new Message();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //FIXME 这里直接更新ui是不行的
                        //还有其他更新ui方式,runOnUiThread()等
                        message.what = 0x11;
                        handler.sendMessage(message);
                    }
                }).start();

            }

            @Override
            public void onGetVideoFailed(ErrorInfo info) {

            }
        });

    }



    @Override
    protected void initView() {
        mEmptyView = bindViewId(R.id.tv_empty);
        mEmptyView.setVisibility(View.GONE);
        mCustomGridView = bindViewId(R.id.gv_video_layout);
        //mIsShowDesc 表示同样是剧集,综艺节目是xx期,电视剧集是数字, 1表示综艺,或纪录片类,6表示动漫,电视剧
        mCustomGridView.setNumColumns(mIsShowDesc ? 1 : 6);
        mVideoItemAdatper = new VideoItemAdapter(getActivity(), mAlbum.getVideoTotal(), mVideoSelectedListner);
        mVideoItemAdatper.setIsShowTitleContent(mIsShowDesc);
        mCustomGridView.setAdapter(mVideoItemAdatper);
        if (mAlbum.getVideoTotal() > 0 && mAlbum.getVideoTotal() > mPageSize) {
            mCustomGridView.setHasMoreItem(true);
        } else {
            mCustomGridView.setHasMoreItem(false);
        }
        mCustomGridView.setOnLoadMoreListener(new CustomGridView.OnLoadMoreListener() {
            @Override
            public void onLoadMoreItems() {
                loadData();
            }
        });

    }

    @Override
    protected void initData() {

    }

    @Override
    protected int getLayoutID() {
        return R.layout.fragment_album_desc;
    }

}
