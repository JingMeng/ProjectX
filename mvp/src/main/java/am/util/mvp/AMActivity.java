/*
 * Copyright (C) 2015 AlexMofer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package am.util.mvp;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.view.View;
import android.widget.Toolbar;

import java.util.ArrayList;

/**
 * Activity
 * Created by Alex on 2017/10/28.
 */
@SuppressWarnings("all")
public abstract class AMActivity extends Activity {

    private final ArrayList<Dialog> mShowDialogs = new ArrayList<>();
    private ToolbarNavigationOnClickListener mToolbarListener;
    private LocalBroadcastManager mLocalBroadcastManager;// 搴旂敤鍐呴儴骞挎挱
    /**
     * 骞挎挱鎺ュ彈鍣?     */
    private BroadcastReceiver mLocalBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            onReceiveLocalBroadcast(context, intent);
        }
    };
    private Dialog mLoading;
    private boolean mShowLoading = false;
    private boolean mAttachedToWindow = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int layout = getContentViewLayout();
        if (layout != 0) {
            setContentView(layout);
        }
        initializeActivity(savedInstanceState);
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        final IntentFilter filter = new IntentFilter();
        onAddLocalAction(filter);
        mLocalBroadcastManager.registerReceiver(mLocalBroadcastReceiver, filter);
        onRegisteredLocalBroadcastReceiver();
        mLoading = getLoadingDialog();
        final AMPresenter presenter = getPresenter();
        if (null != presenter) {
            presenter.onCreated(savedInstanceState);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        final AMPresenter presenter = getPresenter();
        if (null != presenter) {
            presenter.onStarted();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        final AMPresenter presenter = getPresenter();
        if (null != presenter) {
            presenter.onResumed();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        final AMPresenter presenter = getPresenter();
        if (null != presenter) {
            presenter.onPaused();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        final AMPresenter presenter = getPresenter();
        if (null != presenter) {
            presenter.onStopped();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        final AMPresenter presenter = getPresenter();
        if (null != presenter) {
            presenter.onSaveInstanceState(outState);
        }
    }

    @Override
    protected void onDestroy() {
        if (!isLocalBroadcastUnregistered()) {
            mLocalBroadcastManager.unregisterReceiver(mLocalBroadcastReceiver);
            mLocalBroadcastManager = null;
            onUnregisteredLocalBroadcastReceiver();
        }
        super.onDestroy();
        final AMPresenter presenter = getPresenter();
        if (null != presenter) {
            presenter.onDestroyed();
            presenter.detach();
        }
    }

    /**
     * 鑾峰彇椤甸潰甯冨眬
     *
     * @return 甯冨眬ID
     */
    @LayoutRes
    protected abstract int getContentViewLayout();

    /**
     * 鍒濆鍖朅ctivity
     *
     * @param savedInstanceState 淇濆瓨鐨勫疄渚嬫暟鎹?     */
    protected abstract void initializeActivity(@Nullable Bundle savedInstanceState);

    /**
     * 鑾峰彇鍩虹Presenter
     *
     * @return 鍩虹Presenter
     */
    protected AMPresenter getPresenter() {
        return null;
    }

    /**
     * 鑾峰彇杞藉叆瀵硅瘽妗?     *
     * @return 杞藉叆瀵硅瘽妗?     */
    protected Dialog getLoadingDialog() {
        return null;
    }

    /**
     * 娣诲姞鏈湴骞挎挱鐩戝惉鎰忓浘
     *
     * @param filter 鎰忓浘杩囨护鍣?     */
    protected void onAddLocalAction(IntentFilter filter) {
    }

    /**
     * 鎺ユ敹鍒版湰鍦板箍鎾?     *
     * @param context 鍙戣捣骞挎挱鐨刢ontext
     * @param intent  鍙戣捣骞挎挱鐨勬剰鍥?     */
    protected void onReceiveLocalBroadcast(Context context, Intent intent) {
    }

    /**
     * 宸叉敞鍐屾湰鍦板箍鎾帴鏀跺櫒
     */
    protected void onRegisteredLocalBroadcastReceiver() {
    }

    /**
     * 宸插彇娑堟敞鍐屾湰鍦板箍鎾帴鏀跺櫒
     */
    protected void onUnregisteredLocalBroadcastReceiver() {
    }

    /**
     * 鍒ゆ柇鏈湴骞挎挱鎺ユ敹鍣ㄦ槸鍚﹀凡鍙栨秷娉ㄥ唽
     *
     * @return 鏄惁宸插彇娑堟敞鍐?     */
    public final boolean isLocalBroadcastUnregistered() {
        return mLocalBroadcastManager == null;
    }

    /**
     * 鍙戦€佹湰鍦板箍鎾?     *
     * @param action 骞挎挱鍔ㄤ綔
     */
    public final void sendLocalBroadcast(String action) {
        sendLocalBroadcast(new Intent(action));
    }

    /**
     * 鍙戦€佹湰鍦板箍鎾?     *
     * @param intent 骞挎挱鎰忓浘
     */
    public final void sendLocalBroadcast(Intent intent) {
        if (isLocalBroadcastUnregistered()) {
            return;
        }
        mLocalBroadcastManager.sendBroadcast(intent);
    }

    /**
     * 璁剧疆 Toolbar
     *
     * @param toolbarId Toolbar璧勬簮ID
     */
    @RequiresApi(21)
    public final void setActionBar(@IdRes int toolbarId) {
        setActionBar(toolbarId, false);
    }

    /**
     * 璁剧疆 Toolbar
     *
     * @param toolbarId Toolbar璧勬簮ID
     */
    @RequiresApi(21)
    public final void setActionBar(@IdRes int toolbarId, boolean showTitle) {
        setActionBar((Toolbar) findViewById(toolbarId));
        if (null != getActionBar()) {
            getActionBar().setDisplayShowTitleEnabled(showTitle);
        }
    }

    @Override
    @RequiresApi(21)
    public void setActionBar(@Nullable Toolbar toolbar) {
        super.setActionBar(toolbar);
        if (null == toolbar)
            return;
        if (null == mToolbarListener) {
            mToolbarListener = new ToolbarNavigationOnClickListener();
        }
        toolbar.setNavigationOnClickListener(mToolbarListener);
    }

    /**
     * 鏄剧ず榛樿杞藉叆
     */
    protected void showLoading() {
        if (mAttachedToWindow) {
            if (mLoading != null && !mLoading.isShowing())
                mLoading.show();
        } else {
            mShowLoading = true;
        }
    }

    /**
     * 闅愯棌榛樿杞藉叆
     */
    protected void dismissLoading() {
        if (mAttachedToWindow) {
            if (mLoading != null && mLoading.isShowing())
                mLoading.dismiss();
        } else {
            mShowLoading = false;
        }
    }

    /**
     * 鐐瑰嚮浜員oolbar鐨勮繑鍥炴寜閽?     *
     * @param v 杩斿洖鎸夐挳
     */
    protected void onToolbarNavigationClick(View v) {
        onBackPressed();
    }

    /**
     * 鏄剧ず瀵硅瘽妗?     *
     * @param dialog Dialog
     */
    protected void showDialog(Dialog dialog) {
        if (mAttachedToWindow) {
            dialog.show();
        } else {
            if (!mShowDialogs.contains(dialog))
                mShowDialogs.add(dialog);
        }
    }

    /**
     * 鍏抽棴瀵硅瘽妗?     *
     * @param dialog Dialog
     */
    protected void dismissDialog(Dialog dialog) {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        mShowDialogs.remove(dialog);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        mAttachedToWindow = true;
        if (mLoading != null && mShowLoading)
            mLoading.show();
        for (Dialog dialog : mShowDialogs) {
            dialog.show();
        }
        mShowDialogs.clear();
    }

    @Override
    public void onDetachedFromWindow() {
        mAttachedToWindow = false;
        if (mLoading != null && mLoading.isShowing())
            mLoading.dismiss();
        super.onDetachedFromWindow();
    }

    /**
     * 鏄惁闄勭潃鍒扮獥鍙ｅ眰
     *
     * @return 鏄惁
     */
    protected boolean isAttachedToWindow() {
        return mAttachedToWindow;
    }

    private class ToolbarNavigationOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            onToolbarNavigationClick(v);
        }
    }
}
