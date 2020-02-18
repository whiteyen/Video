package com.bilibili.video.favorite;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bilibili.video.R;
import com.bilibili.video.detail.AlbumDetailActivity;
import com.bilibili.video.model.Album;
import com.bilibili.video.model.AlbumList;
import com.bilibili.video.utils.ImageUtils;

import java.util.ArrayList;
import java.util.List;

public class FavoriteAdapter extends BaseAdapter {
    private static final int TYPE_COUNT = 2;
    private Context mContext;
    private AlbumList mAlbumList;
    private boolean mShowChecked;
    private List<FavoriteAlbum> mFavoriteList = new ArrayList<>();

    public FavoriteAdapter(Context context, AlbumList albumList) {
        mAlbumList = albumList;
        mContext = context;
        mShowChecked = false;
        for(Album album:mAlbumList){
            mFavoriteList.add(new FavoriteAlbum(album));
        }
    }

    @Override
    public int getCount() {
        return mAlbumList.size();
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_COUNT;
    }

    @Override
    public FavoriteAlbum getItem(int position) {
        return mFavoriteList.get(position);
    }
    public void OptionCheckedAllItem(boolean isSelected){
        for(FavoriteAdapter.FavoriteAlbum favoriteAlbum:mFavoriteList){
            favoriteAlbum.setChecked(isSelected);
        }
    }


    public List<FavoriteAlbum> getFavoriteList() {
        return mFavoriteList;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FavoriteAlbum favoriteAlbum = getItem(position);
        Album album = favoriteAlbum.getAlbum();
        ViewHolder holder;
        if(convertView==null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.favorite_item,null);
            holder = new ViewHolder();
            holder.mAlbumName = convertView.findViewById(R.id.tv_video_name);
            holder.mAlbumPost = convertView.findViewById(R.id.iv_album_poster);
            holder.mCbButton = convertView.findViewById(R.id.cb_favorite);
            holder.mContainer = convertView.findViewById(R.id.favorite_container);
            if(mShowChecked){
                holder.mCbButton.setVisibility(View.VISIBLE);
            }else {
                holder.mCbButton.setVisibility(View.GONE);
            }
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        if(mFavoriteList.size()>0){
            holder.mAlbumName.setText(album.getTitle());
            Point point = null;
            point = ImageUtils.getHorPostSize(mContext,2);
            RelativeLayout.LayoutParams params =
                        new RelativeLayout.LayoutParams(point.x,point.y);
            holder.mAlbumPost.setLayoutParams(params);
            if(album.getVerImgUrl()!=null){
                ImageUtils.displayImage(holder.mAlbumPost,album.getVerImgUrl(),point.x,point.y);
            }else if(album.getHorImgUrl()!=null){
                ImageUtils.displayImage(holder.mAlbumPost,album.getHorImgUrl(),point.x,point.y);
            }else {
                //TODO
            }
            holder.mCbButton.setChecked(favoriteAlbum.isChecked());
            if(!mShowChecked){
                holder.mContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //跳转详情页
                        AlbumDetailActivity.lauch((Activity) mContext,album);
                    }
                });
                holder.mContainer.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        favoriteAlbum.setChecked(true);
                        holder.mContainer.setVisibility(View.VISIBLE);
                        setShowChecked(true);
                        mFavoriteList.get(position).setChecked(true);
                        notifyDataSetChanged();
                        return true;
                    }
                });
            }else {
                holder.mContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean checked = favoriteAlbum.isChecked();
                        favoriteAlbum.setChecked(checked);
                        holder.mCbButton.setChecked(!checked);
                        favoriteAlbum.setChecked(!checked);
                    }
                });
            }
        }

        return convertView;
    }

    public void setShowChecked(boolean isSelected) {
        this.mShowChecked = isSelected;
    }

    public boolean isSelected() {
        return mShowChecked;
    }

    class ViewHolder{
        ImageView mAlbumPost;
        TextView mAlbumName;
        CheckBox mCbButton;
        RelativeLayout mContainer;
    }

    @Override
    public int getItemViewType(int position) {
            return mShowChecked? 1:0;
    }

    class FavoriteAlbum{
        private Album mAlbum;
        private boolean mIsChecked;
        public FavoriteAlbum(Album album){
            mAlbum = album;
            mIsChecked = false;
        }

        public Album getAlbum() {
            return mAlbum;
        }

        public void setAlbum(Album album) {
            mAlbum = album;
        }

        public boolean isChecked() {
            return mIsChecked;
        }

        public void setChecked(boolean checked) {
            mIsChecked = checked;
        }
    }
}
