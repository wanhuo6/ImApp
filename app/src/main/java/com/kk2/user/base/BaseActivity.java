package com.kk2.user.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.WindowCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.gyf.immersionbar.ImmersionBar;
import com.kk2.user.R;
import com.kk2.user.manager.HttpManager;
import com.kk2.user.ui.widget.MyAppBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created on 17-8-1
 *
 * @author liuhuijie
 */

public abstract class BaseActivity<P extends BasePresenter> extends AppCompatActivity implements BaseView {
    MyAppBar mAppBar;
    View mSplitLine;
    LinearLayout mLLAppbar;
    @BindView(R.id.btn_refresh)
    Button mBtnRefresh;
    @BindView(R.id.ll_net_error)
    LinearLayout mLLNetError;
    FrameLayout mContentLayout;
    protected P mPresenter;

    protected Unbinder mUnBinder = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        supportRequestWindowFeature(WindowCompat.FEATURE_ACTION_MODE_OVERLAY);  //防止在WebView中长按复制出现标题栏显示错误
        initWindows();
        setContentView(getLayoutId());
        setPresenter();
        if (mPresenter != null) {
            mPresenter.setView(this);
        }
        initData();
    }

    protected void initWindows() {
       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0及以上
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {//4.4到5.0
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }*/
        ImmersionBar.with(this).fitsSystemWindows(true).statusBarColor(R.color.public_status_bar_bg_color).keyboardEnable(true).init();


        getDelegate().setContentView(R.layout.activity_base);
        mAppBar=findViewById(R.id.appBar);
        mLLAppbar=findViewById(R.id.ll_appbar);
        mSplitLine=findViewById(R.id.split_line);

    }

    @Override
    public void setContentView(View view) {
        mContentLayout.removeAllViews();
        mContentLayout.addView(view);
    }

    @Override
    public void setContentView(int layoutResID) {
        if (layoutResID != 0) {
            mContentLayout = (FrameLayout) findViewById(R.id.content);
            mContentLayout.removeAllViews();
            getLayoutInflater().inflate(layoutResID, mContentLayout, true);
            mUnBinder = ButterKnife.bind(this);
        }
    }

    public void setNetErrorLayout() {
        mLLNetError.setVisibility(View.VISIBLE);
        mContentLayout.setVisibility(View.GONE);
    }

    public void setNormalLayout() {
        mLLNetError.setVisibility(View.GONE);
        mContentLayout.setVisibility(View.VISIBLE);
    }

    protected abstract int getLayoutId();


    public abstract void initData();

    protected View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onViewClick(v);
        }
    };

    public abstract void refresh();

    protected void onViewClick(View v) {
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.removeView();
            mPresenter = null;
        }
        HttpManager.dismissLoading();
        mUnBinder.unbind();
    }
}
