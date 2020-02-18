package com.bilibili.video.home;


import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.bilibili.video.R;
import com.bilibili.video.base.BaseFragment;
import com.daimajia.numberprogressbar.NumberProgressBar;

import butterknife.BindView;
import butterknife.Unbinder;

public class BlogFragment extends BaseFragment {


    @BindView(R.id.progressbar)
    NumberProgressBar mProgressBar;
    @BindView(R.id.webview)
    WebView mWebView;
    Unbinder unbinder;
    private static final String BLOG_URL = "https://blog.csdn.net/qq_42193011";
    @Override
    protected void initData() {

    }

    @Override
    protected int getLayoutID() {
        return R.layout.fragment_blog;
    }

    @Override
    protected void initView() {
        WebSettings settings = mWebView.getSettings();
        settings.setBuiltInZoomControls(true);
        settings.setJavaScriptEnabled(true);
        settings.setAppCacheEnabled(true);

        mProgressBar.setMax(100);
        mWebView.loadUrl(BLOG_URL);
        mWebView.setWebChromeClient(mWebChromeClient);
    }
    private WebChromeClient mWebChromeClient = new WebChromeClient(){
        @Override
        public void onProgressChanged(WebView view, int newProgress) {

            mProgressBar.setProgress(newProgress);//update the progress
            if (newProgress==100){
                mProgressBar.setVisibility(View.GONE);
            }
            super.onProgressChanged(view, newProgress);
        }
    };

}
