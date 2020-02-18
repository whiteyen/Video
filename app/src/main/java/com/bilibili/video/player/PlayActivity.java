package com.bilibili.video.player;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bilibili.video.R;
import com.bilibili.video.base.BaseActivity;
import com.bilibili.video.model.sohu.Video;
import com.bilibili.video.utils.DataUtils;
import com.bilibili.video.utils.SysUtils;
import com.bilibili.video.widget.media.IjkVideoView;

import java.text.NumberFormat;
import java.util.Formatter;
import java.util.Locale;

import butterknife.BindView;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class PlayActivity extends BaseActivity implements GestureDetectorController.IGestureListener {
    private static final String TAG = PlayActivity.class.getSimpleName();
    @BindView(R.id.tv_horiontal_gesture)
    TextView mDragHoriontalView;
    @BindView(R.id.tv_vertical_gesture)
    TextView mVerticalView;
    private GestureDetectorController mGestureDetectorController;
    private static final int CHECK_TIME = 1;
    private static final int CHECK_BATTERY = 2;
    private static final int CHECK_PROGRESS = 3;
    private static final int AUTO_HIDE_TIME = 8000;
    @BindView(R.id.fl_player_top_container)
    FrameLayout mTopLayout;
    @BindView(R.id.ll_player_bottom_layout)
    LinearLayout mBottomLayout;
    @BindView(R.id.iv_playeer_close)
    ImageView mBackButton;
    @BindView(R.id.tv_player_video_name)
    TextView mVideoName;
    @BindView(R.id.tv_sys_time)
    TextView mSysTimeView;
    @BindView(R.id.iv_player_center_pause)
    ImageView mBigPauseButton;
    @BindView(R.id.cb_play_pause)
    CheckBox mPlayOrPauseButton;
    @BindView(R.id.tv_current_video_time)
    TextView mCurrentVideoTime;
    @BindView(R.id.tv_total_video_time)
    TextView mTotalVideoTime;
    @BindView(R.id.tv_bitstream)
    TextView mTvBitstream;
    @BindView(R.id.iv_battery)
    ImageView mBatteryView;
    @BindView(R.id.sb_player_seekbar)
    SeekBar mSeekBar;
    private Formatter mFormatter;
    private StringBuilder mFormatterBuilder;
    private String mUrl;
    private int mStreamType;
    private int mCurrentPosition;
    private boolean mIsMove;
    private Video mVideo;
    private long mScrollProgress;
    private IjkVideoView mVideoView;
    private RelativeLayout mLoadingLayout;
    private TextView mLoadingText;
    private EventHandler mHandler;
    private boolean mIsPanelShowing = false;
    private boolean mIsDragging;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_play;
    }

    @Override
    protected void initView() {
        mFormatterBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatterBuilder, Locale.getDefault());
        mHandler = new EventHandler(Looper.myLooper());
        initListener();
        initAudio();
        initLight();
        initGesture();
        mUrl = getIntent().getStringExtra("url");
        mLiveTitle = getIntent().getStringExtra("title");
        mStreamType = getIntent().getIntExtra("type", 0);
        mCurrentPosition = getIntent().getIntExtra("currentPosition", 0);
        mVideo = getIntent().getParcelableExtra("video");
        mVideoView = bindViewId(R.id.video_view);
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        mLoadingLayout = bindViewId(R.id.rl_loading_layout);
        mLoadingText = bindViewId(R.id.tv_loading_info);
        mLoadingText.setText("正在加载中...");
        mVideoView.setVideoURI(Uri.parse(mUrl));
        mVideoView.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer iMediaPlayer) {
                mVideoView.start();
            }
        });
        mVideoView.setOnInfoListener(new IMediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i1) {
                switch (i) {
                    case IjkMediaPlayer.MEDIA_INFO_BUFFERING_START:
                        mLoadingLayout.setVisibility(View.VISIBLE);
                        break;
                    case IjkMediaPlayer.MEDIA_INFO_AUDIO_RENDERING_START:
                    case IjkMediaPlayer.MEDIA_INFO_BUFFERING_END:
                        mLoadingLayout.setVisibility(View.GONE);
                        break;
                }
                return false;
            }
        });
        registerReceiver(mBatteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        toggleTopAndBottomLayout();
        mSeekBar.setMax(1000);
        mSeekBar.setOnSeekBarChangeListener(mSeekBarChangeListener);


    }
    private AudioManager mAudioManager;

    private void initAudio() {
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        mAudioManager.requestAudioFocus(null,AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN);
        mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) * 10;
        mCurrentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC)*10;
    }

    private void initLight() {
        mCurrentLight = SysUtils.getDefaultBrightness(this);
        if(mCurrentLight == -1){
            mCurrentLight = SysUtils.getBrightness(this);
        }


    }


    private void initGesture() {
        mGestureDetectorController = new GestureDetectorController(this, this);
    }

    private SeekBar.OnSeekBarChangeListener mSeekBarChangeListener
            = new SeekBar.OnSeekBarChangeListener() {
        //进度发生变化时回调
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (!fromUser) {
                return;
            }
            long duration = mVideoView.getDuration();//视频时长
            long nowPosition = (duration * progress) / 1000L;
            mCurrentVideoTime.setText(StringForTime((int) nowPosition));
        }

        //开始拖动时
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            mIsDragging = true;
        }

        //拖动完成时
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mIsDragging = false;
            int progress = seekBar.getProgress();
            long duration = mVideoView.getDuration();
            long newPosition = (duration / progress) / 1000L;//当前进度
            mVideoView.seekTo((int) newPosition);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    hideTopAndBottomLayout();
                }
            }, 3000);
        }
    };

    private void updateProgress() {
        int currentPosition = mVideoView.getCurrentPosition();
        int duration = mVideoView.getDuration();
        if (mSeekBar != null) {
            if (duration > 0) {

                long pos = currentPosition * 1000L / duration;
                mSeekBar.setProgress((int) pos);
            }
            int percent = mVideoView.getBufferPercentage();
            mSeekBar.setSecondaryProgress(percent);//设置缓存进度
            mCurrentVideoTime.setText(StringForTime(currentPosition));
            mTotalVideoTime.setText(StringForTime(duration));
        }
    }

    private String StringForTime(int timeMs) {
        int totalSeconds = timeMs / 1000;
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = (totalSeconds / 3600);
        mFormatterBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mVideoView != null) {
            mVideoView.stopPlayback();
        }
    }

    private void initListener() {
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mBigPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVideoView.start();
                updatePlayPauseStatus(true);
                //TODO
            }
        });

        mPlayOrPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handlePlayPause();
            }
        });
    }

    private void toggleTopAndBottomLayout() {

        if (mIsPanelShowing) {
            hideTopAndBottomLayout();
        } else {
            showTopAndBottomLayout();
            //先显示，没有任何操作就隐藏
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    hideTopAndBottomLayout();
                }
            }, AUTO_HIDE_TIME);
        }
    }

    private void showTopAndBottomLayout() {
        mIsPanelShowing = true;
        mTopLayout.setVisibility(View.VISIBLE);
        mBottomLayout.setVisibility(View.VISIBLE);
        updateProgress();
        if (mHandler != null) {
            mHandler.removeMessages(CHECK_TIME);
            Message msg = mHandler.obtainMessage(CHECK_TIME);
            mHandler.sendMessage(msg);

            mHandler.removeMessages(CHECK_BATTERY);
            Message batterymsg = mHandler.obtainMessage(CHECK_BATTERY);
            mHandler.sendMessage(batterymsg);

            mHandler.removeMessages(CHECK_PROGRESS);
            Message progressmsg = mHandler.obtainMessage(CHECK_PROGRESS);
            mHandler.sendMessage(progressmsg);

        }
        switch (mStreamType) {
            case SteamType.HIGH:
                mTvBitstream.setText("高清");
                break;
            case SteamType.NORMAL:
                mTvBitstream.setText("标清");
                break;
            case SteamType.SUPER:
                mTvBitstream.setText("超清");
                break;

        }
    }
    private boolean mIsHorizontalScroll;
    private boolean mIsVerticalScroll;
    private int mCurrentLight;
    private int mMaxLight;
    private int mCurrentVolume;
    private int mMaxVolume;
    @Override
    public void onScrollStart(GestureDetectorController.ScrollType type) {
        mIsMove = true;
        switch (type) {
            case HORIZONTAL:
                mDragHoriontalView.setVisibility(View.VISIBLE);
                mScrollProgress = -1;
                mIsHorizontalScroll = true;
                break;
            case VERTICAL_LEFT:
                //setComposeDrawableAndText(mDragHoriontalView,R.drawable.ic_light,this);
               // mDragHoriontalView.setVisibility(View.VISIBLE);
               // updateVerticalText(mCurrentLight,mMaxLight);
              //  mIsVerticalScroll = true;
                break;
            case VERTICAL_RIGHT:
                if(mCurrentVolume > 0){
                    setComposeDrawableAndText(mVerticalView,R.drawable.volume_normal,this);
                }else {
                    setComposeDrawableAndText(mVerticalView,R.drawable.volume_no,this);
                }
                mVerticalView.setVisibility(View.VISIBLE);
                updateVerticalText(mCurrentVolume,mMaxVolume);
                mIsVerticalScroll = true;
                break;
        }
    }
    //更新竖直方向上的文字
    private void updateVerticalText(int current,int total) {
        NumberFormat format = NumberFormat.getPercentInstance();
        format.setMaximumFractionDigits(0);//设置整数部分运行最大小数位
        String percent = format.format((double)(current)/(double)total);
        mVerticalView.setText(percent);
    }

    private void setComposeDrawableAndText(TextView textView,int drawableId,Context context) {
        Drawable drawable = context.getResources().getDrawable(drawableId);
        //设计在矩形区域
        drawable.setBounds(0,0,drawable.getMinimumWidth(),drawable.getMinimumHeight());
        textView.setCompoundDrawables(null,drawable,null,null);

    }

    @Override
    public void onScrollHorizontal(float x1, float x2) {
        int width = getResources().getDisplayMetrics().widthPixels;
        int MAX_SEEK_STEP = 300000;//最大滑动5分钟
        int offset = (int) ((x2/width * MAX_SEEK_STEP) + mVideoView.getCurrentPosition());;
        long progress = Math.max(0,Math.min(mVideoView.getDuration(),offset));
        mScrollProgress = progress;
        updateHorizontalText(progress);
    }
    //更新水平方向
    private void updateHorizontalText(long duration) {
        String text = StringForTime((int)duration)+"/"+StringForTime(mVideoView.getCurrentPosition());
        mDragHoriontalView.setText(text);
    }

    @Override
    public void onScrollLeft(float y1, float y2) {
            int height = getResources().getDisplayMetrics().heightPixels;
            int offset = (int)(mMaxLight * y1)/height;
            if(Math.abs(offset)>0){
                mCurrentLight += offset;
                mCurrentLight = Math.max(0,Math.min(mMaxLight,mCurrentLight));
                SysUtils.setBrightness(this,mCurrentLight);
                SharedPreferences.Editor editor = (SharedPreferences.Editor) PreferenceManager.getDefaultSharedPreferences(this);
                editor.putInt("shared_preferences_light",mCurrentLight);
                editor.commit();
                updateVerticalText(mCurrentLight,mMaxLight);
            }
    }

    @Override
    public void onScrollRight(float y1, float y2) {
        int height = getResources().getDisplayMetrics().heightPixels;
        int offset = (int)(mMaxVolume * y1)/height;
        if(Math.abs(offset)>0){
            mCurrentVolume += offset;
            mCurrentVolume = Math.max(0,Math.min(mCurrentVolume,mMaxVolume));
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,mCurrentVolume/10,0);
            updateVerticalText(mCurrentVolume,mMaxVolume);
        }

    }

    class SteamType {
        public static final int SUPER = 1;
        public static final int NORMAL = 2;
        public static final int HIGH = 3;

    }

    private void hideTopAndBottomLayout() {
        if (mIsDragging == true) {
            return;
        }
        mIsPanelShowing = false;
        mTopLayout.setVisibility(View.GONE);
        mBottomLayout.setVisibility(View.GONE);

    }

    private void handlePlayPause() {
        if (mVideoView.isPlaying()) {
            mVideoView.pause();
            updatePlayPauseStatus(false);
        } else {
            mVideoView.start();
            updatePlayPauseStatus(true);
        }
    }

    private void updatePlayPauseStatus(boolean isPlaying) {
        mBigPauseButton.setVisibility(isPlaying ? View.GONE : View.VISIBLE);
        mPlayOrPauseButton.invalidate();
        mPlayOrPauseButton.setChecked(isPlaying);
        mPlayOrPauseButton.refreshDrawableState();
    }

    private String mLiveTitle;
    @Override
    protected void initData() {
        if (mVideo != null) {
            mVideoName.setText(mVideo.getVideoName());
        }
        if(mLiveTitle!=null){
            mVideoName.setText(mLiveTitle);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private int mBatteryLevel;
    private BroadcastReceiver mBatteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mBatteryLevel = intent.getIntExtra("level", 0);
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        if (mBatteryReceiver != null) {
            unregisterReceiver(mBatteryReceiver);
            mBatteryReceiver = null;
        }
//        mAudioManager.abandonAudioFocusRequest(null);

    }

    class EventHandler extends Handler {
        public EventHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CHECK_TIME:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mSysTimeView.setText(DataUtils.getCurrentTime());
                        }
                    });
                    break;
                case CHECK_BATTERY:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setCurrentBattery(mBatteryLevel);
                        }
                    });

                    break;
                case CHECK_PROGRESS:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            long duration = mVideoView.getCurrentPosition();
                            long nowDuration = (mSeekBar.getProgress() * duration) / 1000L;
                            mCurrentVideoTime.setText(StringForTime((int) nowDuration));
                        }
                    });
                    break;

            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (mIsMove == false) {
                toggleTopAndBottomLayout();
            }else {
                mIsMove = false;
            }
            //水平方向 手指抬起时
            if (mIsHorizontalScroll) {
                mIsHorizontalScroll = false;
                mVideoView.seekTo((int) mScrollProgress);
                //一次down，up结束后隐藏
                mDragHoriontalView.setVisibility(View.GONE);
            }
            if(mIsVerticalScroll){
                mVerticalView.setVisibility(View.GONE);
                mIsVerticalScroll = false;
            }
        }

        return mGestureDetectorController.onTouchEvent(event);
    }

    private void setCurrentBattery(int level) {
        if (0 < level && level <= 10) {
            mBatteryView.setBackgroundResource(R.drawable.ic_battery_10);
        } else if (10 < level && level <= 20) {
            mBatteryView.setBackgroundResource(R.drawable.ic_battery_20);
        } else if (20 < level && level <= 50) {
            mBatteryView.setBackgroundResource(R.drawable.ic_battery_50);
        } else if (50 < level && level <= 80) {
            mBatteryView.setBackgroundResource(R.drawable.ic_battery_80);
        } else if (80 < level && level <= 100) {
            mBatteryView.setBackgroundResource(R.drawable.ic_battery_100);
        }
    }
    public static void launch(Context context,String url,String title){
        Intent intent = new Intent(context, PlayActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("url",url);
        intent.putExtra("title",title);
        context.startActivity(intent);

    }

}
