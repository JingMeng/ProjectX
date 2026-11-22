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

package am.widget.basetabstrip;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * BasePagerTabStrip ViewPager婊戝姩瀵瑰簲鍙樺寲鏁堟灉
 *
 * @author Alex
 */
@ViewPager.DecorView
public abstract class BaseTabStrip extends View {

    private final PageListener mPageListener = new PageListener();
    private final Rect mRefreshRect = new Rect();
    private final Rect mRefreshTempRect = new Rect();
    private ViewPager mPager;
    private WeakReference<PagerAdapter> mWatchingAdapter;
    private int mLastKnownPosition = 0;
    private float mLastKnownPositionOffset = -1;
    private int mCurrentPager = 0;
    private int mPosition = 0;
    private boolean clickSelectedItem = false;
    private Drawable mTabItemBackground;
    private ArrayList<Drawable> mTabItemBackgrounds = new ArrayList<>();
    private boolean tabClickable;
    private boolean clickSmoothScroll;
    private TabStripGestureDetector tabStripGestureDetector;
    private OnItemClickListener clickListener;
    private ArrayList<OnChangeListener> changeListeners;

    public BaseTabStrip(Context context) {
        super(context);
        initView();
    }

    public BaseTabStrip(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public BaseTabStrip(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        setItemClickable(false);
        setClickSmoothScroll(false);
        tabStripGestureDetector = new TabStripGestureDetector();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        final ViewParent parent = getParent();
        if (mPager == null && parent != null && parent instanceof ViewPager) {
            bindViewPager((ViewPager) parent);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        bindViewPager(null);
        clearItemBackground();
    }

    /**
     * 鎹嗙粦ViewPager
     *
     * @param pager 鍏宠仈鐨刅iewPager
     */
    public void bindViewPager(ViewPager pager) {
        PagerAdapter oldAdapter = null;
        PagerAdapter newAdapter = null;
        if (mPager != null) {
            mPager.removeOnPageChangeListener(mPageListener);
            mPager.removeOnAdapterChangeListener(mPageListener);
            oldAdapter = mPager.getAdapter();
        }
        mPager = pager;
        if (mPager != null) {
            mPager.addOnPageChangeListener(mPageListener);
            mPager.addOnAdapterChangeListener(mPageListener);
            newAdapter = mPager.getAdapter();
        }
        bindPagerAdapter(oldAdapter, newAdapter);
    }

    private void clearItemBackground() {
        for (Drawable drawable : mTabItemBackgrounds) {
            drawable.setCallback(null);
        }
        mTabItemBackgrounds.clear();
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!tabClickable) {
            return super.onTouchEvent(ev);
        }
        final boolean tab = tabStripGestureDetector.onTouchEvent(ev);
        return super.onTouchEvent(ev) || tab;
    }

    @Override
    @Deprecated
    public boolean performClick() {
        return super.performClick();
    }

    /**
     * 鐐瑰嚮
     *
     * @param position 浣嶇疆
     * @return 鐐瑰嚮鎴愬姛
     */
    @SuppressWarnings("unused")
    public boolean performClick(int position) {
        return performClick(position, false, true);
    }

    /**
     * 鐐瑰嚮
     *
     * @param position       浣嶇疆
     * @param smoothScroll   鏄惁骞虫粦婊氬姩
     * @param notifyListener 鏄惁閫氱煡鐩戝惉鍣?     * @return 鐐瑰嚮鎴愬姛
     */
    public boolean performClick(int position, boolean smoothScroll, boolean notifyListener) {
        if (getViewPager() != null && position >= 0 && position < getItemCount()) {
            clickSelectedItem = position == mPosition;
            mPosition = position;
            if (!clickSelectedItem) {
                if (!smoothScroll) {
                    mCurrentPager = position;
                    mLastKnownPosition = mCurrentPager;
                    mLastKnownPositionOffset = 0;
                    jumpTo(mCurrentPager);
                    notifyJumpTo(mCurrentPager);
                }
                getViewPager().setCurrentItem(position, smoothScroll);
            }
            if (clickListener != null && notifyListener) {
                clickListener.onItemClick(mPosition);
            }
            playSoundEffect(SoundEffectConstants.CLICK);
            return true;
        }
        return false;
    }

    /**
     * 鎹嗙粦PagerAdapter
     *
     * @param oldAdapter 鏃dapter
     * @param newAdapter 鏂癆dapter
     */
    protected void bindPagerAdapter(PagerAdapter oldAdapter, PagerAdapter newAdapter) {
        if (oldAdapter != null) {
            oldAdapter.unregisterDataSetObserver(mPageListener);
            mWatchingAdapter = null;
        }
        if (newAdapter != null) {
            newAdapter.registerDataSetObserver(mPageListener);
            mWatchingAdapter = new WeakReference<>(newAdapter);
        }
        createItemBackgrounds();
        onBindPagerAdapter();
        checkCurrentItem();
        requestLayout();
        invalidate();
    }

    /**
     * 鍒ゆ柇鏄惁鏈夊瓙椤硅儗鏅?     *
     * @return 鏄惁瀛愰」鑳屾櫙
     */
    protected boolean hasItemBackgrounds() {
        return mTabItemBackground != null;
    }

    /**
     * 鍒涘缓瀛愰」鑳屾櫙
     */
    protected void createItemBackgrounds() {
        if (!hasItemBackgrounds())
            return;
        int count = getItemCount();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                if (i < mTabItemBackgrounds.size()) {
                    mTabItemBackgrounds.get(i).setState(onCreateDrawableState(0));
                } else {
                    Drawable tag;
                    if (mTabItemBackground.getConstantState() != null) {
                        tag = mTabItemBackground.getConstantState().newDrawable().mutate();
                    } else {
                        tag = mTabItemBackground.mutate();
                    }
                    tag.setCallback(this);
                    mTabItemBackgrounds.add(tag);
                }
            }
        } else {
            for (Drawable drawable : mTabItemBackgrounds) {
                drawable.setState(onCreateDrawableState(0));
            }
        }
    }

    /**
     * 閲嶆柊鍒涘缓瀛愰」鑳屾櫙
     */
    protected void recreateItemBackgrounds() {
        clearItemBackground();
        if (!hasItemBackgrounds())
            return;
        int count = getItemCount();
        for (int i = 0; i < count; i++) {
            Drawable tag;
            if (mTabItemBackground.getConstantState() != null) {
                tag = mTabItemBackground.getConstantState().newDrawable().mutate();
            } else {
                tag = mTabItemBackground.mutate();
            }
            tag.setCallback(this);
            mTabItemBackgrounds.add(tag);
        }
    }

    /**
     * 鑾峰彇褰撳墠閫変腑鐨勫瓙椤?     *
     * @return 褰撳墠閫変腑鐨勫瓙椤?     */
    public int getCurrentItem() {
        return mPager == null ? -1 : mPager.getCurrentItem();
    }

    /**
     * 妫€鏌ラ€変腑瀛愰」
     */
    public void checkCurrentItem() {
        final int position = getCurrentItem();
        mPosition = position;
        if (position >= 0 && position != mCurrentPager) {
            mCurrentPager = position;
            mLastKnownPosition = mCurrentPager;
            mLastKnownPositionOffset = 0;
            jumpTo(mCurrentPager);
            notifyJumpTo(mCurrentPager);
        }
    }

    /**
     * 鎹嗙粦PagerAdapter
     */
    protected void onBindPagerAdapter() {

    }

    /**
     * 鐢辫Е鎽哥偣杞负Position
     *
     * @param x X鍧愭爣
     * @param y Y鍧愭爣
     * @return 鍧愭爣瀵瑰簲浣嶇疆
     */
    @SuppressWarnings("unused")
    protected int pointToPosition(float x, float y) {
        return 0;
    }

    @Override
    protected void drawableStateChanged() {
        final float downMotionX = tabStripGestureDetector.getDownMotionX();
        final float downMotionY = tabStripGestureDetector.getDownMotionY();
        int position = pointToPosition(downMotionX, downMotionY);
        if (position >= 0 && position < mTabItemBackgrounds.size()) {
            for (int i = 0; i < mTabItemBackgrounds.size(); i++) {
                Drawable tag = mTabItemBackgrounds.get(i);
                if (i == position) {
                    DrawableCompat.setHotspot(tag, getHotspotX(tag, position, downMotionX, downMotionY),
                            getHotspotY(tag, position, downMotionX, downMotionY));
                    if (tag.isStateful()) {
                        tag.setState(getDrawableState());
                    }
                } else {
                    if (tag.isStateful()) {
                        tag.setState(new int[] { 0 });
                        DrawableCompat.jumpToCurrentState(tag);
                    }
                }

            }
        }
        super.drawableStateChanged();
    }

    /**
     * set hotspot's x location
     *
     * @param background 鑳屾櫙鍥?     * @param position   鍥剧墖Position
     * @param motionX    鐐瑰嚮浣嶇疆X
     * @param motionY    鐐瑰嚮浣嶇疆Y
     * @return x location
     */
    @SuppressWarnings("unused")
    protected float getHotspotX(Drawable background, int position, float motionX, float motionY) {
        return background.getIntrinsicWidth() * 0.5f;
    }

    /**
     * set hotspot's y location
     *
     * @param background 鑳屾櫙鍥?     * @param position   鍥剧墖Position
     * @param motionX    鐐瑰嚮浣嶇疆X
     * @param motionY    鐐瑰嚮浣嶇疆Y
     * @return y location
     */
    @SuppressWarnings("unused")
    protected float getHotspotY(Drawable background, int position, float motionX, float motionY) {
        return background.getIntrinsicHeight() * 0.5f;
    }

    @Override
    protected boolean verifyDrawable(@NonNull Drawable who) {
        boolean isTag = false;
        for (Drawable tag : mTabItemBackgrounds) {
            if (who == tag) {
                isTag = true;
                break;
            }
        }
        return isTag || super.verifyDrawable(who);
    }

    /**
     * 鑾峰彇Tab瀛愰」鑳屾櫙
     *
     * @param position 浣嶇疆
     * @return 鑳屾櫙
     */
    @SuppressWarnings("unused")
    protected Drawable getItemBackground(int position) {
        return position < mTabItemBackgrounds.size() ? mTabItemBackgrounds.get(position) : null;
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        BaseTabStripSavedState ss = new BaseTabStripSavedState(superState);
        ss.currentPager = mCurrentPager;
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof BaseTabStripSavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        BaseTabStripSavedState ss = (BaseTabStripSavedState) state;
        performClick(ss.currentPager, false, false);
        super.onRestoreInstanceState(ss.getSuperState());
    }

    /**
     * 鏇存柊View
     *
     * @param position       浣嶇疆
     * @param positionOffset 浣嶇疆鍋忕Щ
     * @param force          鏄惁寮哄埗鏇存柊
     */
    private void updateView(int position, float positionOffset, boolean force) {
        if (mLastKnownPositionOffset == -1) {
            mLastKnownPositionOffset = positionOffset;
        }
        if (!force && positionOffset == mLastKnownPositionOffset) {
            return;
        }
        float mPositionOffset = positionOffset;
        if (mLastKnownPositionOffset == 0 || mLastKnownPositionOffset == 1)
            if (mPositionOffset > 0.5f)
                mLastKnownPositionOffset = 1;
            else
                mLastKnownPositionOffset = 0;
        int nextPager;
        if (position > mLastKnownPosition) {
            mLastKnownPosition = position - 1;
            if (mLastKnownPositionOffset > mPositionOffset) {
                if (mPositionOffset == 0) {
                    mPositionOffset = 1;
                } else {
                    mLastKnownPosition = position;
                }
                mCurrentPager = mLastKnownPosition;
                nextPager = mLastKnownPosition + 1;
                gotoRight(mCurrentPager, nextPager, mPositionOffset);
                notifyGotoRight(mCurrentPager, nextPager, mPositionOffset);
            } else {
                mCurrentPager = mLastKnownPosition + 1;
                nextPager = mLastKnownPosition;
                gotoLeft(mCurrentPager, nextPager, mPositionOffset);
                notifyGotoLeft(mCurrentPager, nextPager, mPositionOffset);
            }
        } else {
            mLastKnownPosition = position;
            if (mLastKnownPositionOffset > mPositionOffset) {
                mCurrentPager = mLastKnownPosition + 1;
                nextPager = mLastKnownPosition;
                gotoLeft(mCurrentPager, nextPager, mPositionOffset);
                notifyGotoLeft(mCurrentPager, nextPager, mPositionOffset);
            } else {
                mPositionOffset = mPositionOffset == 0 ? 1 : mPositionOffset;
                mCurrentPager = mLastKnownPosition;
                nextPager = mLastKnownPosition + 1;
                gotoRight(mCurrentPager, nextPager, mPositionOffset);
                notifyGotoRight(mCurrentPager, nextPager, mPositionOffset);
            }
        }
        mLastKnownPosition = position;
        mLastKnownPositionOffset = positionOffset;
    }

    /**
     * 閫氱煡璺宠浆鍒?     *
     * @param current 浣嶇疆
     */
    private void notifyJumpTo(int current) {
        if (changeListeners == null)
            return;
        for (OnChangeListener listener : changeListeners) {
            listener.jumpTo(current);
        }
    }

    /**
     * 閫氱煡婊戝悜宸﹁竟
     *
     * @param current 褰撳墠椤?     * @param next    鐩爣椤?     * @param offset  鍋忕Щ
     */
    private void notifyGotoLeft(int current, int next, float offset) {
        if (changeListeners == null)
            return;
        for (OnChangeListener listener : changeListeners) {
            listener.gotoLeft(current, next, offset);
        }
    }

    /**
     * 閫氱煡婊戝悜鍙宠竟
     *
     * @param current 褰撳墠椤?     * @param next    鐩爣椤?     * @param offset  鍋忕Щ
     */
    private void notifyGotoRight(int current, int next, float offset) {
        if (changeListeners == null)
            return;
        for (OnChangeListener listener : changeListeners) {
            listener.gotoRight(current, next, offset);
        }
    }

    /**
     * 鐩存帴璺宠浆鍒?     *
     * @param current 浣嶇疆
     */
    protected abstract void jumpTo(int current);

    /**
     * 婊戝悜宸﹁竟
     *
     * @param current 褰撳墠椤?     * @param next    鐩爣椤?     * @param offset  鍋忕Щ
     */
    protected abstract void gotoLeft(int current, int next, float offset);

    /**
     * 婊戝悜鍙宠竟
     *
     * @param current 褰撳墠椤?     * @param next    鐩爣椤?     * @param offset  鍋忕Щ
     */
    protected abstract void gotoRight(int current, int next, float offset);

    /**
     * 鑾峰彇ViewPager
     *
     * @return ViewPager
     */
    protected ViewPager getViewPager() {
        return mPager;
    }

    /**
     * 鑾峰彇瀛愰」鎬绘暟
     *
     * @return 瀛愰」鎬绘暟
     */
    protected int getItemCount() {
        try {
            return mWatchingAdapter.get().getCount();
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 鑾峰彇瀛愰」鏂囧瓧
     *
     * @param position 瀛愰」鍧愭爣
     * @return 瀛愰」鏂囧瓧
     */
    @SuppressWarnings("unused")
    protected CharSequence getItemText(int position) {
        try {
            return mWatchingAdapter.get().getPageTitle(position);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 璁剧疆Tab瀛愰」鑳屾櫙
     *
     * @param background 鑳屾櫙
     */
    public void setItemBackground(Drawable background) {
        if (mTabItemBackground != background) {
            mTabItemBackground = background;
            recreateItemBackgrounds();
            requestLayout();
            invalidate();
        }
    }

    /**
     * 璁剧疆Tab瀛愰」鑳屾櫙
     *
     * @param background 鑳屾櫙
     */
    @SuppressWarnings("unused")
    public void setItemBackground(int background) {
        setItemBackground(ContextCompat.getDrawable(getContext(), background));
    }

    /**
     * 璁剧疆Tab鏄惁鍙互鐐瑰嚮
     *
     * @param clickable 鏄惁鍙互鐐瑰嚮
     */
    public void setItemClickable(boolean clickable) {
        tabClickable = clickable;
        if (tabClickable) {
            setClickable(true);
        }
    }

    /**
     * Tab鏄惁鍙互鐐瑰嚮
     *
     * @return Tab鏄惁鍙互鐐瑰嚮
     */
    @SuppressWarnings("unused")
    public boolean isTabClickable() {
        return tabClickable;
    }

    /**
     * 鏄惁鐐瑰嚮鏃跺钩婊戞粴鍔?     *
     * @return 鐐瑰嚮鏃舵槸鍚﹀钩婊戞粴鍔?     */
    @SuppressWarnings("unused")
    public boolean isClickSmoothScroll() {
        return clickSmoothScroll;
    }

    /**
     * 璁剧疆鐐瑰嚮鏃舵槸鍚﹀钩婊戞粴鍔?     *
     * @param smooth 鐐瑰嚮鏃舵槸鍚﹀钩婊戞粴鍔?     */
    public void setClickSmoothScroll(boolean smooth) {
        clickSmoothScroll = smooth;
    }

    /**
     * 璁剧疆鐐瑰嚮鐩戝惉
     *
     * @param listener 鐩戝惉鍣?     */
    @SuppressWarnings("unused")
    public void setOnItemClickListener(OnItemClickListener listener) {
        clickListener = listener;
    }

    /**
     * 娣诲姞鍙樺寲鐩戝惉鍣?     *
     * @param listener 鍙樺寲鐩戝惉鍣?     */
    @SuppressWarnings("unused")
    public void addOnChangeListener(OnChangeListener listener) {
        if (listener == null)
            return;
        if (changeListeners == null)
            changeListeners = new ArrayList<>();
        changeListeners.add(listener);
        listener.jumpTo(mCurrentPager);
    }

    /**
     * 绉婚櫎鍙樺寲鐩戝惉鍣?     *
     * @param listener 鍙樺寲鐩戝惉鍣?     */
    @SuppressWarnings("unused")
    public void removeOnChangeListener(OnChangeListener listener) {
        if (changeListeners == null)
            return;
        changeListeners.remove(listener);
    }

    /**
     * 鑾峰彇榛樿Tag鑳屾櫙
     *
     * @return 榛樿Tag鑳屾櫙
     */
    @SuppressWarnings("unused")
    protected Drawable getDefaultTagBackground() {
        final float density = getResources().getDisplayMetrics().density;
        final GradientDrawable mBackground = new GradientDrawable();
        mBackground.setShape(GradientDrawable.RECTANGLE);
        mBackground.setColor(0xffff4444);
        mBackground.setCornerRadius(100000 * density);
        mBackground.setSize((int) (10 * density), (int) (10 * density));
        return mBackground;
    }

    /**
     * 鑾峰彇瀛愰」鑳屾櫙鏈€灏忓搴?     *
     * @return 瀛愰」鑳屾櫙鏈€灏忓搴?     */
    @SuppressWarnings("unused")
    protected int getMinItemBackgroundWidth() {
        if (!hasItemBackgrounds())
            return 0;
        return mTabItemBackground.getMinimumWidth();
    }

    /**
     * 鑾峰彇瀛愰」鑳屾櫙鏈€灏忛珮搴?     *
     * @return 瀛愰」鑳屾櫙鏈€灏忛珮搴?     */
    @SuppressWarnings("unused")
    protected int getMinItemBackgroundHeight() {
        if (!hasItemBackgrounds())
            return 0;
        return mTabItemBackground.getMinimumHeight();
    }

    /**
     * 鍙岃壊鍚堟垚
     *
     * @param normalColor   鏅€氶鑹?     * @param selectedColor 閫変腑棰滆壊
     * @param offset        鍋忕Щ鍊?     * @return 鍚堟垚鑹?     */
    @SuppressWarnings("unused")
    protected int getColor(int normalColor, int selectedColor, float offset) {
        int normalAlpha = Color.alpha(normalColor);
        int normalRed = Color.red(normalColor);
        int normalGreen = Color.green(normalColor);
        int normalBlue = Color.blue(normalColor);
        int selectedAlpha = Color.alpha(selectedColor);
        int selectedRed = Color.red(selectedColor);
        int selectedGreen = Color.green(selectedColor);
        int selectedBlue = Color.blue(selectedColor);
        int a = (int) Math.ceil((selectedAlpha - normalAlpha) * offset);
        int r = (int) Math.ceil((selectedRed - normalRed) * offset);
        int g = (int) Math.ceil((selectedGreen - normalGreen) * offset);
        int b = (int) Math.ceil((selectedBlue - normalBlue) * offset);
        return Color.argb(normalAlpha + a, normalRed + r, normalGreen + g,
                normalBlue + b);
    }

    /**
     * 鍒锋柊鎸囧畾鍖哄煙
     *
     * @param dirty 鍖哄煙闆?     */
    @SuppressWarnings("unused")
    public void invalidate(int... dirty) {
        if (dirty == null || dirty.length <= 0)
            return;
        mRefreshRect.set(0, 0, 0, 0);
        for (int position : dirty) {
            mRefreshTempRect.set(0, 0, 0, 0);
            getItemRect(position, mRefreshTempRect);
            mRefreshRect.union(mRefreshTempRect);
        }
        if (!mRefreshTempRect.isEmpty())
            invalidate(mRefreshTempRect);
    }

    /**
     * 鍒锋柊浣嶇疆
     * ViewPager鍋氫簡璺宠浆锛屽彲鎵ц璇ユ柟娉曢噸鏂板畾浣?     */
    @SuppressWarnings("unused")
    public void invalidatePosition() {
        jumpTo(getCurrentItem());
    }

    /**
     * 鑾峰彇瀛愰」杈圭晫
     *
     * @param position 瀛愰」
     * @param bound    杈圭晫
     */
    @SuppressWarnings("unused")
    protected void getItemRect(int position, Rect bound) {

    }

    /**
     * 鐐瑰嚮鐩戝惉
     */
    public interface OnItemClickListener {
        /**
         * 鐐瑰嚮瀛愰」
         *
         * @param position 浣嶇疆
         */
        void onItemClick(int position);

        /**
         * 鐐瑰嚮宸查€変腑瀛愰」
         *
         * @param position 浣嶇疆
         */
        void onSelectedClick(int position);

        /**
         * 鍙屽嚮瀛愰」
         *
         * @param position 浣嶇疆
         */
        void onDoubleClick(int position);
    }

    /**
     * 鍙樺寲鐩戝惉
     */
    @SuppressWarnings("all")
    public interface OnChangeListener {
        /**
         * 璺宠浆鍒板綋鍓嶄綅缃?         *
         * @param correct 褰撳墠浣嶇疆
         */
        void jumpTo(int correct);

        /**
         * 寰€宸︽粴鍔?         *
         * @param correct 褰撳墠浣嶇疆
         * @param next    灏嗚鎶佃揪浣嶇疆
         * @param offset  绉诲姩渚垮疁
         */
        void gotoLeft(int correct, int next, float offset);

        /**
         * 寰€鍙虫粴鍔?         *
         * @param correct 褰撳墠浣嶇疆
         * @param next    灏嗚鎶佃揪浣嶇疆
         * @param offset  绉诲姩渚垮疁
         */
        void gotoRight(int correct, int next, float offset);
    }

    /**
     * 瑙掓爣鏁版嵁瀹瑰櫒Adapter
     */
    @SuppressWarnings("unused")
    public interface ItemTabAdapter {

        /**
         * 鏄惁鍚敤瑙掓爣
         *
         * @param position Item浣嶇疆
         * @return 鏄惁鍚敤
         */
        boolean isTagEnable(int position);

        /**
         * 鑾峰彇瑙掓爣鍊?         *
         * @param position Item浣嶇疆
         * @return 瑙掓爣鍊?         */
        String getTag(int position);
    }

    private static class BaseTabStripSavedState extends BaseSavedState {
        public static final Creator<BaseTabStripSavedState> CREATOR =
                new Creator<BaseTabStripSavedState>() {
                    public BaseTabStripSavedState createFromParcel(Parcel in) {
                        return new BaseTabStripSavedState(in);
                    }

                    public BaseTabStripSavedState[] newArray(int size) {
                        return new BaseTabStripSavedState[size];
                    }
                };
        int currentPager;

        BaseTabStripSavedState(Parcelable superState) {
            super(superState);
        }

        private BaseTabStripSavedState(Parcel in) {
            super(in);
            currentPager = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(currentPager);
        }
    }

    /**
     * Tag浣嶇疆
     */
    @SuppressWarnings("all")
    protected static class TagLocation {
        public static final int LOCATION_CONTENT = 0;//璐磋繎鍐呭
        public static final int LOCATION_EDGE = 1;// 璐磋繎杈圭紭
        private int location = LOCATION_CONTENT;// 浣嶇疆

        private int paddingLeft;
        private int paddingRight;
        private int paddingTop;
        private int paddingBottom;

        private int marginLeft;
        private int marginRight;
        private int marginTop;
        private int marginBottom;

        public TagLocation() {
            this(LOCATION_CONTENT);
        }

        public TagLocation(int location) {
            setLocation(location);
            setPadding(0, 0, 0, 0);
            setMargin(0, 0, 0, 0);
        }

        public boolean setLocation(int location) {
            if (location != LOCATION_CONTENT && location != LOCATION_EDGE)
                return false;
            if (this.location != location) {
                this.location = location;
                return true;
            }
            return false;
        }

        public boolean setPadding(int left, int top, int right, int bottom) {
            if (left < 0 || top < 0 || right < 0 || bottom < 0 ||
                    (paddingLeft == left && paddingTop == top
                            && paddingRight == right && paddingBottom == bottom))
                return false;
            paddingLeft = left;
            paddingTop = top;
            paddingRight = right;
            paddingBottom = bottom;
            return true;
        }

        public boolean setMargin(int left, int top, int right, int bottom) {
            if (left < 0 || top < 0 || right < 0 || bottom < 0 ||
                    (marginLeft == left && marginTop == top
                            && marginRight == right && marginBottom == bottom))
                return false;
            marginLeft = left;
            marginTop = top;
            marginRight = right;
            marginBottom = bottom;
            return true;
        }

        public int getLocation() {
            return location;
        }

        public int getPaddingLeft() {
            return paddingLeft;
        }

        public int getPaddingRight() {
            return paddingRight;
        }

        public int getPaddingTop() {
            return paddingTop;
        }

        public int getPaddingBottom() {
            return paddingBottom;
        }

        public int getMarginLeft() {
            return marginLeft;
        }

        public int getMarginRight() {
            return marginRight;
        }

        public int getMarginTop() {
            return marginTop;
        }

        public int getMarginBottom() {
            return marginBottom;
        }
    }

    private class PageListener extends DataSetObserver implements
            ViewPager.OnPageChangeListener, ViewPager.OnAdapterChangeListener {
        private int mScrollState;

        @Override
        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels) {
            updateView(position, positionOffset, false);
        }

        @Override
        public void onPageSelected(int position) {
            if (mScrollState == ViewPager.SCROLL_STATE_IDLE) {
                float offset = mLastKnownPositionOffset >= 0 ? mLastKnownPositionOffset : 0;
                updateView(position, offset, false);
            }
            mPosition = position;
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            mScrollState = state;
        }

        @Override
        public void onAdapterChanged(@NonNull ViewPager viewPager,
                                     @Nullable PagerAdapter oldAdapter,
                                     @Nullable PagerAdapter newAdapter) {
            bindPagerAdapter(oldAdapter, newAdapter);
        }

        @Override
        public void onChanged() {
            final float offset = mLastKnownPositionOffset >= 0 ? mLastKnownPositionOffset : 0;
            final int position = getCurrentItem();
            mPosition = position;
            updateView(position, offset, true);
        }
    }

    private class TabStripGestureDetector {

        private final int DOUBLE_TAP_TIMEOUT = ViewConfiguration.getDoubleTapTimeout();
        private float mDownMotionX;
        private float mDownMotionY;
        private int mDownPosition;
        private int mLastPosition;
        private long mLastUpTime;

        boolean onTouchEvent(MotionEvent ev) {
            boolean isClick = false;
            final int action = ev.getAction();
            final float x = ev.getX();
            final float y = ev.getY();

            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    mDownMotionX = x;
                    mDownMotionY = y;
                    mDownPosition = pointToPosition(x, y);
                    mLastPosition = getCurrentItem();
                    break;
                case MotionEvent.ACTION_UP:
                    final int clickPosition = pointToPosition(x, y);
                    if (mDownPosition == clickPosition) {
                        isClick = onItemClick(clickPosition);
                        if (mLastPosition == clickPosition) {
                            final long downTime = ev.getDownTime();
                            if (mLastUpTime != 0 && downTime - mLastUpTime < DOUBLE_TAP_TIMEOUT) {
                                onDoubleClick(clickPosition);
                            } else {
                                onSelectedClick(clickPosition);
                            }
                        }
                        mLastUpTime = ev.getEventTime();
                    } else {
                        mLastUpTime = 0;
                    }
                    break;
            }
            return isClick;
        }

        private boolean onItemClick(int position) {
            performClick(position, clickSmoothScroll, true);
            return true;
        }

        private void onSelectedClick(int position) {
            if (clickSelectedItem && clickListener != null) {
                clickListener.onSelectedClick(position);
            }
        }

        /**
         * 鍙屽嚮瀛愰」
         *
         * @param position 浣嶇疆
         */
        private void onDoubleClick(int position) {
            if (clickListener != null) {
                clickListener.onDoubleClick(position);
            }
        }

        float getDownMotionX() {
            return mDownMotionX;
        }

        float getDownMotionY() {
            return mDownMotionY;
        }
    }
}
