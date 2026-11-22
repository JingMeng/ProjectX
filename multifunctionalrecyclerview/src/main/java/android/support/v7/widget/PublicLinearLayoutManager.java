/*
 * Copyright (C) 2017 AlexMofer
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

package android.support.v7.widget;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * 鍏紑閮ㄥ垎鍖呭唴绉佹湁鐨勬柟娉曞強淇濆瓨鐘舵€佺殑绾挎€у竷灞€绠＄悊鍣? * Created by Alex on 2017/11/3.
 */
@SuppressWarnings("all")
public class PublicLinearLayoutManager extends LinearLayoutManager {

    private RecyclerView mView;
    private boolean mSizeUnlimited = false;

    public PublicLinearLayoutManager(Context context) {
        super(context);
    }

    public PublicLinearLayoutManager(Context context, int orientation,
                                     boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public PublicLinearLayoutManager(Context context, AttributeSet attrs,
                                     int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onAttachedToWindow(RecyclerView view) {
        mView = view;
        super.onAttachedToWindow(view);
    }

    @Override
    public void onDetachedFromWindow(RecyclerView view, RecyclerView.Recycler recycler) {
        super.onDetachedFromWindow(view, recycler);
        mView = null;
    }

    /**
     * 鑾峰彇鐩爣RecyclerView
     *
     * @return RecyclerView
     */
    @Nullable
    protected RecyclerView getRecyclerView() {
        return mView;
    }

    /**
     * 鑾峰彇婊氬姩鐘舵€?     *
     * @return 婊氬姩鐘舵€?     */
    protected int getScrollState() {
        return mView == null ? RecyclerView.SCROLL_STATE_IDLE : mView.getScrollState();
    }

    /**
     * 璁剧疆婊氬姩鐘舵€?     *
     * @param state 婊氬姩鐘舵€?     */
    protected void setScrollState(int state) {
        if (mView == null)
            return;
        mView.setScrollState(state);
    }

    /**
     * 鎷︽埅鍒嗗彂婊氬姩鐘舵€佸彉鍖?     *
     * @param state 鐘舵€佸彉鍖?     * @return 鏄惁鎷︽埅
     */
    protected boolean onInterceptDispatchOnScrollStateChanged(int state) {
        return false;
    }

    @Override
    public int getHeightMode() {
        return mSizeUnlimited ? View.MeasureSpec.UNSPECIFIED : super.getHeightMode();
    }

    @Override
    public int getWidthMode() {
        return mSizeUnlimited ? View.MeasureSpec.UNSPECIFIED : super.getWidthMode();
    }

    /**
     * 璁剧疆灏哄涓嶅彈闄愬埗
     *
     * @param unlimited 鏄惁涓嶅彈闄愬埗
     */
    public void setSizeUnlimited(boolean unlimited) {
        if (mSizeUnlimited == unlimited)
            return;
        mSizeUnlimited = unlimited;
        requestLayout();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        onSaveInstanceState(bundle);
        return new BaseSavedState(bundle, (SavedState) super.onSaveInstanceState());
    }

    /**
     * 瀛樺偍鐘舵€?     *
     * @param bundle 鏁版嵁鍖?     */
    protected void onSaveInstanceState(Bundle bundle) {

    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        BaseSavedState savedState = (BaseSavedState) state;
        onRestoreInstanceState(savedState.getState());
        super.onRestoreInstanceState(savedState.getSuperState());
    }

    /**
     * 鎭㈠鐘舵€?     *
     * @param bundle 鏁版嵁鍖?     */
    protected void onRestoreInstanceState(Bundle bundle) {

    }

    private static class BaseSavedState implements Parcelable {

        public static final Creator<BaseSavedState> CREATOR
                = new Creator<BaseSavedState>() {
            @Override
            public BaseSavedState createFromParcel(Parcel in) {
                return new BaseSavedState(in);
            }

            @Override
            public BaseSavedState[] newArray(int size) {
                return new BaseSavedState[size];
            }
        };
        private final SavedState mSuperState;
        private final Bundle mState;

        private BaseSavedState(Bundle state, SavedState superState) {
            mState = state;
            mSuperState = superState;
        }

        private BaseSavedState(Parcel source) {
            mSuperState = source.readParcelable(SavedState.class.getClassLoader());
            mState = source.readBundle();
        }

        private Parcelable getSuperState() {
            return mSuperState;
        }

        private Bundle getState() {
            return mState;
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(mSuperState, flags);
            dest.writeBundle(mState);
        }
    }
}

