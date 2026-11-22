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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Fragment
 * Created by Alex on 2017/3/14.
 */
@SuppressWarnings("unused")
public abstract class AMSupportFragment extends Fragment {

    private LocalBroadcastManager mLocalBroadcastManager;// 搴旂敤鍐呴儴骞挎挱
    /**
     * 骞挎挱鎺ュ彈鍣?     */
    private BroadcastReceiver mLocalBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            onReceiveLocalBroadcast(context, intent);
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final AMPresenter presenter = getPresenter();
        if (null != presenter) {
            presenter.onCreated(savedInstanceState);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(
                getContentViewLayout(inflater, container, savedInstanceState),
                container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initializeFragment(savedInstanceState);
        if (isLocalBroadcastEnable()) {
            mLocalBroadcastManager = LocalBroadcastManager.getInstance(getActivity());
            IntentFilter filter = new IntentFilter();
            onAddLocalAction(filter);
            mLocalBroadcastManager.registerReceiver(mLocalBroadcastReceiver, filter);
            onRegisteredLocalBroadcastReceiver();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        final AMPresenter presenter = getPresenter();
        if (null != presenter) {
            presenter.onStarted();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        final AMPresenter presenter = getPresenter();
        if (null != presenter) {
            presenter.onResumed();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        final AMPresenter presenter = getPresenter();
        if (null != presenter) {
            presenter.onPaused();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        final AMPresenter presenter = getPresenter();
        if (null != presenter) {
            presenter.onStopped();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        final AMPresenter presenter = getPresenter();
        if (null != presenter) {
            presenter.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onDestroy() {
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
     * 鑾峰彇鍐呭Layout
     *
     * @param inflater           甯冨眬瀹瑰櫒
     * @param container          鏍筕iew
     * @param savedInstanceState 淇濆瓨鐨勭姸鎬?     * @return 璧勬簮id
     */
    @LayoutRes
    protected abstract int getContentViewLayout(LayoutInflater inflater,
                                                ViewGroup container, Bundle savedInstanceState);

    /**
     * 鍒濆鍖朏ragment
     *
     * @param savedInstanceState 淇濆瓨鐨勫疄渚嬫暟鎹?     */
    protected abstract void initializeFragment(@Nullable Bundle savedInstanceState);

    /**
     * 鑾峰彇鍩虹Presenter
     *
     * @return 鍩虹Presenter
     */
    @Nullable
    protected AMPresenter getPresenter() {
        return null;
    }

    /**
     * 鍒ゆ柇鏄惁寮€鍚簲鐢ㄥ唴骞挎挱
     * 瀵逛簬Fragment鏉ヨ鍏朵负闈炲繀椤婚€夐」
     *
     * @return 鏄惁寮€鍚?     */
    protected boolean isLocalBroadcastEnable() {
        return false;
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
     * 閫氳繃ID鏌ユ壘View
     *
     * @param id  View 鐨勮祫婧怚D
     * @param <V> View绫诲瀷
     * @return 瀵瑰簲璧勬簮ID鐨刅iew
     */
    @SuppressWarnings("unchecked")
    public final <V extends View> V findViewById(int id) {
        if (null == getView()) {
            // 鍦ㄩ敊璇殑鏃舵満璋冪敤
            throw new IllegalStateException("Fragment " + this
                    + " has not created its view yet.");
        }
        return (V) getView().findViewById(id);
    }

    /**
     * 鍙戦€佸箍鎾?     *
     * @param action 骞挎挱鍔ㄤ綔
     */
    public final void sendLocalBroadcast(String action) {
        sendLocalBroadcast(new Intent(action));
    }

    /**
     * 鍙戦€佸箍鎾?     *
     * @param intent 骞挎挱鎰忓浘
     */
    public final void sendLocalBroadcast(Intent intent) {
        if (isLocalBroadcastUnregistered()) {
            return;
        }
        mLocalBroadcastManager.sendBroadcast(intent);
    }
}
