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

package am.widget.scrollbarrecyclerview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build;
import androidx.annotation.IntDef;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import am.widget.itemanimatorcontrollablerecyclerview.ItemAnimatorControllableRecyclerView;


/**
 * 甯︽粴鍔ㄦ潯鐨凴ecyclerView
 * Created by Alex on 2017/10/18.
 */
@SuppressWarnings("all")
public class ScrollbarRecyclerView extends ItemAnimatorControllableRecyclerView {

    private final Rect mChildBound = new Rect();
    private Scrollbar mScrollbar;// 婊氬姩鏉?    private IndicatorAdapter mAdapter;
    private boolean mInterceptTouch;

    public ScrollbarRecyclerView(Context context) {
        super(context);
        initView(context, null);
    }

    public ScrollbarRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public ScrollbarRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        setWillNotDraw(false);
        if (useDefaultSlider()) {
            setScrollbar(new DefaultScrollbar());
        }
        onConstruct(context, attrs);
    }

    /**
     * 鏄惁浣跨敤榛樿鐨勬粴鍔ㄦ潯
     *
     * @return 鏄惁浣跨敤榛樿
     */
    protected boolean useDefaultSlider() {
        return true;
    }

    /**
     * 鍒濆鍖朧iew
     *
     * @param context Context
     * @param attrs   灞炴€?     */
    protected void onConstruct(Context context, @Nullable AttributeSet attrs) {
        if (mScrollbar != null)
            mScrollbar.onConstruct(this, context, attrs);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (Build.VERSION.SDK_INT < 23 && mScrollbar != null) {
            mScrollbar.onDraw(this, canvas);
        }
    }

    @Override
    public void onDrawForeground(Canvas canvas) {
        if (mScrollbar != null) {
            mScrollbar.onDraw(this, canvas);
        }
        super.onDrawForeground(canvas);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mInterceptTouch = false;
                if (mScrollbar != null && mScrollbar.onInterceptTouchEvent(this, event)) {
                    mInterceptTouch = true;
                    final boolean touch = mScrollbar.onTouchEvent(this, event);
                    setScrollState(SCROLL_STATE_DRAGGING);
                    return touch;
                }
                break;
            default:
                if (mInterceptTouch) {
                    final boolean touch = mScrollbar.onTouchEvent(this, event);
                    if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL)
                        setScrollState(SCROLL_STATE_IDLE);
                    return touch;
                }
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        if (mScrollbar != null) {
            mScrollbar.onScrollStateChanged(this, state);
        }
    }

    protected String getScrollbarIndicator() {
        if (isInEditMode())
            return "鈽?;
        if (mAdapter != null) {
            return mAdapter.getIndicator(this);
        }
        final View target = findChildViewNear(
                getPaddingLeft() + (getWidth() - getPaddingLeft() - getPaddingRight()) * 0.5f,
                getPaddingTop() + (getHeight() - getPaddingTop() - getPaddingBottom()) * 0.5f);
        if (target == null)
            return null;
        final int position = getChildAdapterPosition(target);
        return Integer.toString(position + getDefaultScrollbarIndicatorOffset());
    }

    protected int getDefaultScrollbarIndicatorOffset() {
        return 1;
    }

    @Nullable
    public View findChildViewNear(float x, float y) {
        if (getChildCount() <= 0)
            return null;
        if (getChildCount() == 1)
            return getChildAt(0);
        final LayoutManager layoutManager = getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            final LinearLayoutManager linear = (LinearLayoutManager) layoutManager;
            float distance = Float.MAX_VALUE;
            View target = null;
            final int count = getChildCount();
            for (int i = count - 1; i >= 0; i--) {
                final View child = getChildAt(i);
                mChildBound.set(child.getLeft(), child.getTop(),
                        child.getRight(), child.getBottom());
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)
                        child.getLayoutParams();
                if (linear.getOrientation() == HORIZONTAL) {
                    if ((mChildBound.left < x && mChildBound.right > x)
                            || mChildBound.left == x || mChildBound.right == y) {
                        // 鐐瑰湪View鍐呴儴
                        return child;
                    } else if (mChildBound.left > x) {
                        final float dis = mChildBound.left - x;
                        if (dis < distance) {
                            distance = dis;
                            target = child;
                        }
                    } else {
                        final float dis = x - mChildBound.right;
                        if (dis < distance) {
                            distance = dis;
                            target = child;
                        }
                    }
                } else {
                    if ((mChildBound.top < y && mChildBound.bottom > y)
                            || mChildBound.top == y || mChildBound.bottom == y) {
                        // 鐐瑰湪View鍐呴儴
                        return child;
                    } else if (mChildBound.top > y) {
                        final float dis = mChildBound.top - y;
                        if (dis < distance) {
                            distance = dis;
                            target = child;
                        }
                    } else {
                        final float dis = y - mChildBound.bottom;
                        if (dis < distance) {
                            distance = dis;
                            target = child;
                        }
                    }
                }
            }
            if (target != null)
                return target;
        }
        final View child = findChildViewUnder(x, y);
        if (child != null)
            return child;
        return null;
    }

    /**
     * 鑾峰彇婊氬姩鏉?     *
     * @return 婊氬姩鏉?     */
    public Scrollbar getScrollbar() {
        return mScrollbar;
    }

    /**
     * 璁剧疆婊氬姩鏉?     *
     * @param scrollbar 婊氬姩鏉?     */
    public void setScrollbar(Scrollbar scrollbar) {
        if (mScrollbar == scrollbar)
            return;
        if (mScrollbar != null)
            mScrollbar.onDetachedFromView(this);
        mScrollbar = scrollbar;
        if (mScrollbar != null)
            mScrollbar.onAttachedToView(this);
        invalidate();
    }

    /**
     * 璁剧疆瑙︽懜鎸囩ず鏍囧唴瀹规彁渚涜€?     *
     * @param adapter 瑙︽懜鎸囩ず鏍囧唴瀹规彁渚涜€?     */
    public void setIndicatorAdapter(IndicatorAdapter adapter) {
        mAdapter = adapter;
        invalidate();
    }

    /**
     * 瑙︽懜鎸囩ず鏍囧唴瀹规彁渚涜€?     */
    public interface IndicatorAdapter {

        /**
         * 鑾峰彇鎸囩ず鏍囨枃鏈唴瀹?         *
         * @param view 瑙嗗浘
         * @return 鏂囨湰鍐呭
         */
        String getIndicator(ScrollbarRecyclerView view);
    }

    /**
     * 婊氬姩鏉?     */
    public static abstract class Scrollbar {

        public static final int SHOW_NONE = 0;// 涓嶆樉绀?        public static final int SHOW_HORIZONTAL = 1;// 鏄剧ず姘村钩婊氬姩鏉?        public static final int SHOW_VERTICAL = 2;// 鏄剧ず鍨傜洿婊氬姩鏉?        public static final int SHOW_ALL = 3;// 鏄剧ず鍏ㄩ儴婊氬姩鏉?        private ScrollbarRecyclerView mView;

        /**
         * 闄勭潃瑙嗗浘
         *
         * @param view 瑙嗗浘
         */
        protected void onAttachedToView(ScrollbarRecyclerView view) {
            mView = view;
        }

        /**
         * 鑴辩瑙嗗浘
         *
         * @param view 瑙嗗浘
         */
        protected void onDetachedFromView(ScrollbarRecyclerView view) {
            mView = null;
        }

        /**
         * 鑾峰彇闄勭潃鐨勮鍥?         *
         * @return 闄勭潃鐨勮鍥?         */
        @Nullable
        protected ScrollbarRecyclerView getAttachedView() {
            return mView;
        }

        /**
         * 瑙嗗浘鎺т欢鍒濆鍖?         * 浠呭湪ScrollbarRecyclerView灏氭湭鏋勫缓涔嬪墠灏卞凡缁忚缃簡Scrollbar锛屽叾鎵嶄細璋冪敤銆傚叿浣撴柟娉曪細
         * 閲嶅啓{@link ScrollbarRecyclerView#useDefaultSlider()}鏂规硶杩斿洖flase锛屼繚璇佷笉浣跨敤榛樿婊氬姩鏉°€?         * 閲嶅啓{@link ScrollbarRecyclerView#onConstruct(Context, AttributeSet)}鏂规硶锛?         * 骞跺湪璋冪敤鍏秙uper鏂规硶涔嬪墠璁剧疆鑷繁鐨凷crollbar銆?         *
         * @param view    瑙嗗浘
         * @param context Context
         * @param attrs   灞炴€?         */
        protected abstract void onConstruct(ScrollbarRecyclerView view, Context context,
                                            @Nullable AttributeSet attrs);

        /**
         * 缁樺埗
         *
         * @param view   瑙嗗浘
         * @param canvas 鐢诲竷
         */
        protected abstract void onDraw(ScrollbarRecyclerView view, Canvas canvas);

        /**
         * 鎷︽埅瑙︽懜浜嬩欢
         *
         * @param view  瑙嗗浘
         * @param event 瑙︽懜浜嬩欢
         * @return 鏄惁鎷︽埅
         */
        protected abstract boolean onInterceptTouchEvent(ScrollbarRecyclerView view, MotionEvent event);

        /**
         * 鍝嶅簲瑙︽懜浜嬩欢
         *
         * @param view  瑙嗗浘
         * @param event 瑙︽懜浜嬩欢
         * @return 鏄惁鍝嶅簲
         */
        protected abstract boolean onTouchEvent(ScrollbarRecyclerView view, MotionEvent event);

        /**
         * 婊氬姩鐘舵€佸凡鏀瑰彉
         *
         * @param view  瑙嗗浘
         * @param state 婊氬姩鐘舵€?         */
        protected abstract void onScrollStateChanged(ScrollbarRecyclerView view, int state);

        @IntDef({SHOW_NONE, SHOW_HORIZONTAL, SHOW_VERTICAL, SHOW_ALL})
        @Retention(RetentionPolicy.SOURCE)
        public @interface ShowType {
        }
    }
}

