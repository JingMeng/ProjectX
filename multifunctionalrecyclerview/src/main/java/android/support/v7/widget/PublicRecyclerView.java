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
import android.view.animation.Interpolator;

/**
 * 鍏紑閮ㄥ垎鍖呭唴绉佹湁鐨勬柟娉曞強鍙橀噺鐨凴ecyclerView
 * Created by Alex on 2017/11/8.
 */
@SuppressWarnings("all")
public class PublicRecyclerView extends RecyclerView {

    public PublicRecyclerView(Context context) {
        super(context);
    }

    public PublicRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PublicRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * 鑾峰彇榛樿婊氬姩琛ュ抚鍣?     *
     * @return 榛樿婊氬姩琛ュ抚鍣?     */
    public static Interpolator getScrollerInterpolator() {
        return sQuinticInterpolator;
    }

    @Override
    protected void setScrollState(int state) {
        super.setScrollState(state);
    }

    @Override
    protected void dispatchOnScrollStateChanged(int state) {
        if (onInterceptDispatchOnScrollStateChanged(state))
            return;
        super.dispatchOnScrollStateChanged(state);
    }

    /**
     * 鎷︽埅鍒嗗彂婊氬姩鐘舵€佸彉鍖?     *
     * @param state 鐘舵€佸彉鍖?     * @return 鏄惁鎷︽埅
     */
    protected boolean onInterceptDispatchOnScrollStateChanged(int state) {
        final LayoutManager layoutManager = getLayoutManager();
        return layoutManager instanceof PublicLinearLayoutManager &&
                ((PublicLinearLayoutManager) layoutManager)
                        .onInterceptDispatchOnScrollStateChanged(state);
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

