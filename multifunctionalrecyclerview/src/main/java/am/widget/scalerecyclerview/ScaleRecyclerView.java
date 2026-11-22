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

package am.widget.scalerecyclerview;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewParent;

import am.widget.multifunctionalrecyclerview.layoutmanager.PagingLayoutManager;
import am.widget.scrollbarrecyclerview.ScrollbarRecyclerView;

/**
 * 鍙缉鏀剧殑RecyclerView
 * Created by Alex on 2017/11/8.
 */
@SuppressWarnings("all")
public class ScaleRecyclerView extends ScrollbarRecyclerView {

    public static final int TYPE_ADJUST_AUTO = 0;
    public static final int TYPE_ADJUST_HORIZONTAL = 1;
    public static final int TYPE_ADJUST_VERTICAL = 2;
    public static final int TYPE_ADJUST_ALL = 3;
    public static final int SCROLL_STATE_SCALING = 3;
    private static final String KEY_SCALE = "am.widget.scalerecyclerview.ScaleRecyclerView.KEY_SCALE";
    private final ScaleHelper mScaleHelper = new ScaleHelper(this);
    private boolean mScaleEnable = false;
    private GestureDetectorCompat mGestureDetector;
    private boolean mInterceptTouch = false;
    private boolean mShouldReactDoubleTab = true;
    private boolean mShouldReactSingleTab = true;
    private ScaleGestureDetector mScaleGestureDetector;
    private boolean mScaleBegin = false;
    private OnTabListener mListener;
    private float mScale = 1;
    private float mMaxScale = 6;
    private float mMinScale = 0.000000001f;
    private int mAdjustType = TYPE_ADJUST_AUTO;

    public ScaleRecyclerView(Context context) {
        super(context);
        initView();
    }

    public ScaleRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ScaleRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private void initView() {
        mGestureDetector = new GestureDetectorCompat(getContext(), new DoubleTapListener());
        mScaleGestureDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!mScaleEnable)
            return super.dispatchTouchEvent(ev);
        final int action = ev.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            mScaleHelper.stop();
            mShouldReactDoubleTab = true;
            mShouldReactSingleTab = true;
        }
        final boolean superResult = super.dispatchTouchEvent(ev);
        if (!mInterceptTouch) {
            // 鐐瑰嚮涓庡弻鍑讳簨浠朵笉浼氳鎷︽埅
            // 鏀剧疆姝ゅ鏄繚璇佺偣鍑讳笌鍙屽嚮浜嬩欢鍙戠敓鍦ㄥ瓙椤硅Е鎽镐簨浠朵箣鍚庯紝浠ヤ究瀛愰」鎺у埗鍏舵槸鍚﹀搷搴斻€?            mGestureDetector.onTouchEvent(ev);
        }
        return superResult;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        if (!mScaleEnable)
            return super.onInterceptTouchEvent(e);
        mInterceptTouch = super.onInterceptTouchEvent(e);
        return mInterceptTouch;
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        if (disallowIntercept) {
            mShouldReactDoubleTab = false;
            mShouldReactSingleTab = false;
        }
        super.requestDisallowInterceptTouchEvent(disallowIntercept);
    }

    @Override
    @SuppressWarnings("all")
    public boolean onTouchEvent(MotionEvent e) {
        if (!mScaleEnable)
            return super.onTouchEvent(e);
        boolean superResult = false;
        // 妫€鏌ョ缉鏀句簨浠?        if (e.getPointerCount() >= 2) {
            // 澶氭寚
            if (!mScaleBegin) {
                // 寮€濮嬬缉鏀撅紝瀵瑰垪琛ㄦā鎷熶竴涓狝CTION_UP浜嬩欢
                final int action = e.getAction();
                e.setAction(MotionEvent.ACTION_UP);
                setForceInterceptDispatchOnScrollStateChanged(true);
                superResult = super.onTouchEvent(e);
                setForceInterceptDispatchOnScrollStateChanged(false);
                e.setAction(action);
                mScaleBegin = true;
            }
            superResult = mScaleGestureDetector.onTouchEvent(e) || superResult;
        } else {
            // 鍗曟寚
            final int action = e.getAction();
            boolean dispatch = false;
            if (action == MotionEvent.ACTION_UP && getScrollState() == SCROLL_STATE_IDLE) {
                dispatch = true;
            }
            if (mScaleBegin) {
                mScaleBegin = false;
                // 缁撴潫缂╂斁锛屽鍒楄〃妯℃嫙涓€涓狝CTION_DOWN浜嬩欢
                e.setAction(MotionEvent.ACTION_DOWN);
                super.onInterceptTouchEvent(e);
                e.setAction(action);
            }
            superResult = super.onTouchEvent(e);
            if (dispatch) {
                dispatchOnScrollStateChanged(SCROLL_STATE_IDLE);
            }
        }
        return superResult;
    }

    private void setForceInterceptDispatchOnScrollStateChanged(boolean force) {
        final LayoutManager layoutManager = getLayoutManager();
        if (layoutManager instanceof PagingLayoutManager) {
            ((PagingLayoutManager) layoutManager)
                    .setForceInterceptDispatchOnScrollStateChanged(force);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putFloat(KEY_SCALE, mScale);
    }

    @Override
    protected void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        mScale = bundle.getFloat(KEY_SCALE, 1f);
        invalidateLayoutManagerScale();
        invalidate();
    }

    protected void invalidateLayoutManagerScale() {
        setLayoutManagerScale(mScale);
    }

    protected void setLayoutManagerScale(float scale) {
        final LayoutManager layoutManager = getLayoutManager();
        if (layoutManager instanceof ScaleLinearLayoutManager) {
            ScaleLinearLayoutManager lm = (ScaleLinearLayoutManager) layoutManager;
            lm.setChildScale(scale);
        }
    }

    /**
     * 鍒嗗彂鍗曞嚮浜嬩欢
     */
    protected boolean dispatchSingleTap() {
        return mShouldReactSingleTab && mListener != null && mListener.onSingleTap(this);
    }

    /**
     * 鍒嗗彂鍙屽嚮浜嬩欢
     *
     * @param e 瑙︽懜浜嬩欢
     */
    protected boolean dispatchDoubleTapEvent(MotionEvent e) {
        // 浠呭湪鍙屽嚮浜嬩欢绗簩娆＄偣鍑荤殑ACTION_UP鍑虹幇鏃惰Е鍙戝弻鍑荤缉鏀俱€?        if (e.getAction() != MotionEvent.ACTION_UP)
            return false;
        // 鍦ㄦ杩囩▼涓紝涓€浜涜Е鎽镐簨浠跺彲鑳戒細瀵艰嚧浜嬩欢鍙栨秷锛屽叾娆¤浜嬩欢鍙互琚瓙View鍙栨秷銆?        if (!mShouldReactDoubleTab)
            return false;
        // 鐩戝惉鎷︽埅鍙屽嚮浜嬩欢
        if (mListener != null && mListener.onDoubleTap(this))
            return true;
        // 鎵ц鍙屽嚮缂╂斁浜嬩欢
        final float targetScale = getDoubleTapScale(mScale);
        if (targetScale == mScale)
            return false;
        mScaleHelper.scale(mScale, targetScale, e.getX(), e.getY());
        return true;
    }

    private float getDoubleTapScale(float scale) {
        final float targetScale = scale * 2;
        if (targetScale < mMaxScale) {
            return targetScale;
        }
        return mMaxScale;
    }

    /**
     * 鍒嗗彂寮€濮嬬缉鏀?     *
     * @param detector 缂╂斁鎵嬪娍妫€娴嬪櫒
     * @return 鏄惁缁х画澶勭悊缂╂斁
     */
    protected boolean dispatchScaleBegin(ScaleGestureDetector detector) {
        onScaleBegin(mScale, detector.getFocusX(), detector.getFocusY());
        return true;
    }

    /**
     * 寮€濮嬬缉鏀?     *
     * @param scale  璧峰缂╂斁姣?     * @param focusX 鐒︾偣X
     * @param focusY 鐒︾偣Y
     */
    protected void onScaleBegin(float scale, float focusX, float focusY) {
        dispatchOnScrollStateChanged(SCROLL_STATE_SCALING);
    }

    /**
     * 鍒嗗彂缂╂斁
     *
     * @param detector 缂╂斁鎵嬪娍妫€娴嬪櫒
     * @return 鏄惁澶勭悊浜嗕簨浠?     */
    protected boolean dispatchScale(ScaleGestureDetector detector) {
        onScale(mScale * detector.getScaleFactor(),
                detector.getFocusX(), detector.getFocusY());
        return true;
    }

    /**
     * 澶勭悊缂╂斁
     *
     * @param scale  鐩爣缂╂斁姣?     * @param focusX 鐒︾偣X
     * @param focusY 鐒︾偣Y
     */
    protected void onScale(float scale, float focusX, float focusY) {
        scaleTo(scale, focusX, focusY);
    }

    /**
     * 缂╂斁
     * TODO 浣嶇疆鏍¤皟鍙紭鍖?     *
     * @param scale  鐩爣缂╂斁姣?     * @param focusX 鐒︾偣X
     * @param focusY 鐒︾偣Y
     */
    public void scaleTo(float scale, float focusX, float focusY) {
        scale = scale > mMaxScale ? mMaxScale : scale;
        scale = scale < mMinScale ? mMinScale : scale;
        if (scale == mScale)
            return;
        View child = getChildAt(0);
        // 璁＄畻focus瀵瑰簲View鐨?x1, y1)
        final float beforeViewX = focusX - child.getLeft();
        final float beforeViewY = focusY - child.getTop();
        // 璁＄畻View鐨?x1, y1)瀵瑰簲缂╂斁鍚庣殑(x2, y2)
        final float afterViewX = beforeViewX / mScale * scale;
        final float afterViewY = beforeViewY / mScale * scale;
        mScale = scale;
        invalidateLayoutManagerScale();
        requestLayout();
        // 搴旂敤鍋忕Щ
        final int dx = Math.round(afterViewX - beforeViewX);
        final int dy = Math.round(afterViewY - beforeViewY);
        switch (mAdjustType) {
            default:
            case TYPE_ADJUST_AUTO:
                final LayoutManager layoutManager = getLayoutManager();
                if (layoutManager instanceof LinearLayoutManager) {
                    if (((LinearLayoutManager) layoutManager).getOrientation() ==
                            LinearLayoutManager.HORIZONTAL) {
                        scrollBy(dx, 0);
                    } else {
                        scrollBy(0, dy);
                    }
                } else {
                    scrollBy(dx, dy);
                }
                break;
            case TYPE_ADJUST_HORIZONTAL:
                scrollBy(dx, 0);
                break;
            case TYPE_ADJUST_VERTICAL:
                scrollBy(0, dy);
                break;
            case TYPE_ADJUST_ALL:
                scrollBy(dx, dy);
                break;
        }
    }

    /**
     * 鍒嗗彂缁撴潫缂╂斁
     *
     * @param detector 缂╂斁鎵嬪娍妫€娴嬪櫒
     */
    protected void dispatchScaleEnd(ScaleGestureDetector detector) {
        onScaleEnd(mScale, detector.getFocusX(), detector.getFocusY());
    }

    /**
     * 鍒嗗彂鏈€鍚庣殑缂╂斁缁撴灉
     *
     * @param scale  缂╂斁姣?     * @param focusX 鐒︾偣X
     * @param focusY 鐒︾偣Y
     */
    protected void onScaleEnd(float scale, float focusX, float focusY) {
        if (mScaleBegin)
            return;
        dispatchOnScrollStateChanged(SCROLL_STATE_IDLE);
    }

    protected void onDoubleTapScaleBegin(float scale, float focusX, float focusY) {
        dispatchOnScrollStateChanged(SCROLL_STATE_SCALING);
    }

    protected void onDoubleTapScale(float scale, float focusX, float focusY) {
        scaleTo(scale, focusX, focusY);
    }

    protected void onDoubleTapScaleEnd(float scale, float focusX, float focusY) {
        if (mScaleBegin)
            return;
        dispatchOnScrollStateChanged(SCROLL_STATE_IDLE);
    }

    /**
     * 鍒ゆ柇缂╂斁鏄惁寮€鍚?     *
     * @return 鏄惁寮€鍚?     */
    public boolean isScaleEnable() {
        return mScaleEnable;
    }

    /**
     * 璁剧疆缂╂斁鏄惁寮€鍚?     *
     * @param enable 鏄惁寮€鍚?     */
    public void setScaleEnable(boolean enable) {
        mScaleEnable = enable;
    }

    /**
     * 璁剧疆鍗曞弻鍑荤洃鍚?     *
     * @param listener 鐐瑰嚮鐩戝惉
     */
    public void setOnTabListener(OnTabListener listener) {
        mListener = listener;
    }

    /**
     * 璁剧疆缂╂斁鍖洪棿
     *
     * @param min 鏈€灏忕缉鏀?     * @param max 鏈€澶х缉鏀?     */
    public void setScaleRange(float min, float max) {
        mMinScale = min;
        mMaxScale = max;
    }

    /**
     * 鑾峰彇缂╂斁姣?     *
     * @return 缂╂斁姣?     */
    public float getScale() {
        return mScale;
    }

    /**
     * 璁剧疆瀛愰」缂╂斁
     *
     * @param child 灞炰簬ScaleRecyclerView鐨勫瓙椤?     */
    public static void setScale(View child) {
        final ViewParent parent = child.getParent();
        if (parent instanceof ScaleRecyclerView) {
            final ScaleRecyclerView view = (ScaleRecyclerView) parent;
            RecyclerView.ViewHolder holder = view.findContainingViewHolder(child);
            if (holder instanceof ScaleRecyclerView.ViewHolder) {
                ((ScaleRecyclerView.ViewHolder) holder).setScale(view.getScale());
            }
        }
    }

    /**
     * 璁剧疆缂╂斁姣?     *
     * @param scale 缂╂斁姣?     */
    public void setScale(float scale) {
        if (scale < mMinScale || scale > mMaxScale || scale == mScale)
            return;
        mScale = scale;
        invalidateLayoutManagerScale();
        requestLayout();
    }

    /**
     * 璁剧疆缂╂斁鍚庣殑浣嶇疆鏍¤皟鏂瑰紡
     *
     * @param type 鏍¤皟鏂瑰紡
     */
    public void setAdjustType(int type) {
        mAdjustType = type;
    }

    public interface OnTabListener {
        /**
         * 鍗曞嚮浜嬩欢
         *
         * @param view ScaleRecyclerView
         * @return 鏄惁娑堣€椾簨浠?         */
        boolean onSingleTap(ScaleRecyclerView view);

        /**
         * 鍙屽嚮浜嬩欢
         *
         * @param view ScaleRecyclerView
         * @return 鏄惁娑堣€椾簨浠?         */
        boolean onDoubleTap(ScaleRecyclerView view);
    }

    /**
     * 鍐呭鎻愪緵鑰?     *
     * @param <VH> 瑙嗗浘鎸佹湁鑰?     */
    public static abstract class Adapter<VH extends ViewHolder> extends RecyclerView.Adapter<VH> {

        private ScaleRecyclerView mView;

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            if (recyclerView instanceof ScaleRecyclerView) {
                mView = (ScaleRecyclerView) recyclerView;
            }
            super.onAttachedToRecyclerView(recyclerView);
        }

        @Override
        public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
            mView = null;
            super.onDetachedFromRecyclerView(recyclerView);
        }

        public boolean isAttachedToRecyclerView() {
            return mView != null;
        }

        public float getScale() {
            return mView == null ? 1 : mView.getScale();
        }
    }

    /**
     * 瑙嗗浘鎸佹湁鑰?     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }

        public void setScale(float scale) {

        }
    }

    private class DoubleTapListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return dispatchSingleTap();
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            return dispatchDoubleTapEvent(e);
        }
    }

    private class ScaleListener implements ScaleGestureDetector.OnScaleGestureListener {


        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return dispatchScaleBegin(detector);
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            return dispatchScale(detector);
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            dispatchScaleEnd(detector);
        }
    }
}

