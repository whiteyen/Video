package com.bilibili.video.home;





import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Gravity;
import android.view.MenuItem;

import com.bilibili.video.FragmentManagerWrapper;
import com.bilibili.video.R;
import com.bilibili.video.base.BaseActivity;
import com.bilibili.video.base.BaseFragment;

import butterknife.BindView;

public class HomeActivity extends BaseActivity {
    @BindView(R.id.drawer_layout)
        DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView ;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private MenuItem mPreMenuItem;
    private FragmentManager mFragmentManager;
    private BaseFragment mResultFragment;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_home;
    }
    @Override
    protected void initView()  {

            setSupportActionBar();
            setActionBarIcon(R.drawable.ic_drawer_home);
            setTitle("首页");
            mNavigationView=findViewById(R.id.navigation_view);
            mActionBarDrawerToggle= new ActionBarDrawerToggle(this,mDrawerLayout,mToolbar,
                    R.string.drawer_open,R.string.drawer_close);
            mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);
            mActionBarDrawerToggle.syncState();
            mPreMenuItem  = mNavigationView.getMenu().getItem(0);
            mPreMenuItem.setChecked(true);
            initFragment();
            handleNavigationViewItem();
    }

    private void initFragment()  {
        mResultFragment = FragmentManagerWrapper.getInstance().createFragment(HomeFragment.class);
        mFragmentManager = getSupportFragmentManager();
        mFragmentManager.beginTransaction().add(R.id.fl_main_content,mResultFragment).commit();

    }

    private void handleNavigationViewItem() {
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if(mPreMenuItem!=null){
                    mPreMenuItem.setChecked(false);
                }
                switch (menuItem.getItemId()){
                    case R.id.navigation_about:
                        switchFragment(AboutFragment.class);
                        mToolbar.setTitle("关于");
                        break;
                    case R.id.navigation_blog:
                        switchFragment(BlogFragment.class);
                        mToolbar.setTitle("我的博客");
                        break;
                    case R.id.navigation_video:
                        switchFragment(HomeFragment.class);
                        mToolbar.setTitle("视频");
                        break;
                }
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawer(Gravity.LEFT);
                mPreMenuItem = menuItem;
                return false;
            }
        });
    }

    private void switchFragment(Class<?> clazz) {
        BaseFragment fragment = FragmentManagerWrapper.getInstance().createFragment(clazz);
        if (fragment.isAdded()){
            mFragmentManager.beginTransaction().hide(mResultFragment).show(fragment).commitAllowingStateLoss();
        }
        else {
            mFragmentManager.beginTransaction().hide(mResultFragment).add(R.id.fl_main_content,fragment).commitAllowingStateLoss();
        }
        mResultFragment = fragment;
    }


    @Override
    protected void initData() {


    }
}
