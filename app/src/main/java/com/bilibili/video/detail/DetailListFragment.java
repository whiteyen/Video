package com.bilibili.video.detail;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bilibili.video.R;
import com.bilibili.video.api.OnGetChannelAlbumListener;
import com.bilibili.video.api.SiteApi;
import com.bilibili.video.base.BaseFragment;
import com.bilibili.video.model.Album;
import com.bilibili.video.model.AlbumList;
import com.bilibili.video.model.Channel;
import com.bilibili.video.model.ErrorInfo;
import com.bilibili.video.model.Site;
import com.bilibili.video.utils.ImageUtils;
import com.bilibili.video.widget.PullLoadRecyclerView;

import butterknife.BindView;
import butterknife.Unbinder;


public class DetailListFragment extends BaseFragment {

    private static final String TAG = DetailListFragment.class.getSimpleName();
    private static int mSiteId;
    private static int mChannelId;
    private static final String CHANNEL_ID = "channelId";
    private static final String SITE_ID = "siteId";
    private int mColums;
    private Handler mHandler = new Handler(Looper.getMainLooper());//Handler在主线程中
    private static final int REFRESH_DURATION = 1500;
    private static final int LOADMORE_DURATION = 3000;
    private int pageNo;
    private int pageSize=30;

    @BindView(R.id.pullloadRecyclerView)
    PullLoadRecyclerView mRecyclerView;

    @BindView(R.id.empty_view)
    TextView mTextView;
    Unbinder unbinder1;
    private DetalListAdapter mAdapter;
    public static Fragment newInstance(int siteId, int channelId) {
        DetailListFragment fragment = new DetailListFragment();
        //Log.d(TAG, mSiteId+"<<<"+ mChannelId);
        mSiteId = siteId;
        mChannelId = channelId;
        Bundle bundle = new Bundle();
        bundle.putInt(SITE_ID, siteId);
        bundle.putInt(CHANNEL_ID, channelId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        pageNo = 0;
        if(getArguments()!=null){
            mSiteId = getArguments().getInt(SITE_ID);
            mChannelId = getArguments().getInt(CHANNEL_ID);
        }
        mAdapter = null;
        mAdapter = new DetalListAdapter(getActivity(),new Channel(mChannelId+1,getActivity()));
        loadData();

        if(mSiteId == Site.LETV){//乐视下两列
            mColums = 2;
            mAdapter.setColumns(mColums);
        }else {
            mColums = 3;
            mAdapter.setColumns(mColums);
        }

        Toast.makeText(getActivity(),"加载到最新数据",Toast.LENGTH_SHORT).show();
    }

    @Override
    protected int getLayoutID() {
        return R.layout.fragment_detail_list;
    }

    @Override
    protected void initView() {
        mTextView.setText("");
        mRecyclerView.setGridLayout(mColums);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setOnPullLoadMoreListener(new PullLoadMoreListener());
    }


    class PullLoadMoreListener implements PullLoadRecyclerView.OnPullLoadMoreListener {
        @Override
        public void reFresh() {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                       reFreshData();
                       mRecyclerView.setRefreshCompleted();
                }
            },REFRESH_DURATION);
        }

        @Override
        public void loadMore() {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadData();
                    mRecyclerView.setLoadMoreCompleted();
                }
            },1500);
        }

    }

    private void loadData() {
        //TODO
        pageNo++;
        //Log.d(TAG,"ddddd");
      //  Log.d(TAG,String.valueOf(mSiteId));
        SiteApi.onGetChannelAlbums(getActivity(), pageNo, pageSize, mSiteId,mChannelId+1 , new OnGetChannelAlbumListener() {
            @Override
            public void onGetChannelAlbumSuccess(AlbumList albumList) {
               mHandler.post(new Runnable() {
                   @Override
                   public void run() {
                     mTextView.setVisibility(View.GONE);
                   }
               });
               for(Album album:albumList){
                   if(mAdapter!=null){
                       mAdapter.setData(album);
                   }
               }
               mHandler.post(new Runnable() {
                   @Override
                   public void run() {
                       mAdapter.notifyDataSetChanged();
                   }
               });
//                for(Album album:albumList){
//                    Log.d(TAG,">>album"+album
//                    .toString());
//                }

            }

            @Override
            public void onGetChannelAlbumFailed(ErrorInfo errorInfo) {
                Log.d(TAG,errorInfo.getExceptionString());
            }
        });
    }

    private void reFreshData() {
            //TODO 请求接口
    }

    class DetalListAdapter extends RecyclerView.Adapter {
        private Context mContext;
        private Channel mChannel;
        private AlbumList mAlbumList = new AlbumList();
        private int mColumns;
        public DetalListAdapter(Context context,Channel channel){
            mContext = context;
            mChannel = channel;
        }
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view =  ((Activity)mContext).getLayoutInflater().inflate(R.layout.detaillist_item,null);
            ItemViewHolder itemViewHolder = new ItemViewHolder((view));
            view.setTag(itemViewHolder);
            return itemViewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            if(mAlbumList.size() == 0){
                return;
            }
            Album album = getItem(i);
            if(viewHolder instanceof  ItemViewHolder){
                ItemViewHolder itemViewHolder = (ItemViewHolder)viewHolder ;
                itemViewHolder.albumName.setText(album.getTitle());
                if(album.getTip().isEmpty()){
                    itemViewHolder.albumName.setVisibility(View.GONE);
                }else {
                    itemViewHolder.albumTip.setText(album.getTip());
                }
                Point point = null;
                if(mColumns == 2){
                     point = ImageUtils.getHorPostSize(mContext,mColumns);
                    RelativeLayout.LayoutParams params =
                            new RelativeLayout.LayoutParams(point.x,point.y);
                    itemViewHolder.albumPoster.setLayoutParams(params);
                }else {
                    point = ImageUtils.getVerPostSize(mContext, mColumns);
                    RelativeLayout.LayoutParams params =
                            new RelativeLayout.LayoutParams(point.x, point.y);
                    itemViewHolder.albumPoster.setLayoutParams(params);
                }

                if(album.getVerImgUrl()!=null){
                    ImageUtils.displayImage(itemViewHolder.albumPoster,album.getVerImgUrl(),point.x,point.y);
                }else if(album.getHorImgUrl()!=null){
                    ImageUtils.displayImage(itemViewHolder.albumPoster,album.getHorImgUrl(),point.x,point.y);

                }else {
                    //TODO

                }
                itemViewHolder.resultContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mChannelId == Channel.DOCUMENTRY|| mChannelId== Channel.VARIETY || mChannelId == Channel.MUSIC){
                                AlbumDetailActivity.lauch(getActivity(),album,0,true);
                        }else {
                            AlbumDetailActivity.lauch(getActivity(),album);
                        }
                    }
                });
            }

        }
        private Album getItem(int position){
            return  mAlbumList.get(position);
        }


        @Override
        public int getItemCount() {
            if(mAlbumList.size()>0){
                return mAlbumList.size();
            }
            return 0;
        }

        public  void setColumns(int columns){
                mColumns = columns;
        }
        public void setData(Album album){
            mAlbumList.add(album);
        }
        public class ItemViewHolder extends RecyclerView.ViewHolder{
                private LinearLayout resultContainer;
                private ImageView albumPoster;
                private TextView albumName;
                private TextView albumTip;

                public ItemViewHolder(View view){
                    super(view);
                    albumName = view.findViewById(R.id.tv_album_name);
                    albumTip = view.findViewById(R.id.tv_album_tip);
                    albumPoster = view.findViewById(R.id.iv_album_poster);
                    resultContainer = view.findViewById(R.id.album_container);

                }

        }
    }


}
