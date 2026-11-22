package am.project.x.widgets.display;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowInsets;

import am.widget.multifunctionalrecyclerview.MultifunctionalRecyclerView;
import am.widget.scalerecyclerview.ScaleRecyclerView;

/**
 * 鏄剧ずRecyclerView
 * Created by Xiang Zhicheng on 2017/11/3.
 */

public class DisplayRecyclerView extends MultifunctionalRecyclerView implements
        MultifunctionalRecyclerView.OnTabListener {

    private int mPaddingTop;
    private int mPaddingLeft;
    private int mPaddingBottom;
    private int mPaddingRight;
    private DisplayItemDecoration mItemDecoration;
    private DisplayLayoutManager mLayoutManager;
    private OnDisplayListener mListener;
    private int mOffset = 0;
    private int mCurrentPosition = NO_POSITION;

    public DisplayRecyclerView(Context context) {
        super(context);
        initView(context);
    }

    public DisplayRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public DisplayRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    private void initView(Context context) {
        setScaleEnable(true);
        mItemDecoration = new DisplayItemDecoration(context);
        mLayoutManager = new DisplayLayoutManager(context);
        mItemDecoration.updateDecorationMaxWidthOfChildWithMaxSize(mLayoutManager);
        addItemDecoration(mItemDecoration);
        setLayoutManager(mLayoutManager);
        setOnTabListener(this);
        setScaleRange(1, 6);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mPaddingTop = getPaddingTop();
        mPaddingLeft = getPaddingLeft();
        mPaddingBottom = getPaddingBottom();
        mPaddingRight = getPaddingRight();
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        mLayoutManager.setChildMaxSize(getDisplayWidth(), getDisplayHeight());
    }

    @Override
    @SuppressWarnings("all")
    protected boolean fitSystemWindows(Rect insets) {
        if (Build.VERSION.SDK_INT >= 20) {
            return super.fitSystemWindows(insets);
        }
        setPadding(mPaddingLeft + insets.left, mPaddingTop,
                mPaddingRight + insets.right, mPaddingBottom);
        mItemDecoration.fitSystemWindows(insets.left, insets.top, insets.right, insets.bottom);
        return super.fitSystemWindows(insets);
    }

    @Override
    @RequiresApi(20)
    public WindowInsets dispatchApplyWindowInsets(WindowInsets insets) {
        final int left = insets.getSystemWindowInsetLeft();
        final int top = insets.getSystemWindowInsetTop();
        final int right = insets.getSystemWindowInsetRight();
        final int bottom = insets.getSystemWindowInsetBottom();
        setPadding(mPaddingLeft + left, mPaddingTop,
                mPaddingRight + right, mPaddingBottom);
        mItemDecoration.fitSystemWindows(left, top, right, bottom);
        return super.dispatchApplyWindowInsets(insets);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        final View target = findChildViewNear(
                (getWidth() - getPaddingLeft() - getPaddingRight()) * 0.5f,
                (getHeight() - getPaddingTop() - getPaddingBottom()) * 0.5f);
        if (target == null)
            return;
        final int position = getChildAdapterPosition(target);
        if (mCurrentPosition != position) {
            mCurrentPosition = position;
            if (mListener != null)
                mListener.onCurrentItemChanged(this, position);
        }
    }

    @Nullable
    @Override
    public View findChildViewNear(float x, float y) {
        return mLayoutManager.findChildViewNear(x, y);
    }

    @Override
    public void onScrolled(int dx, int dy) {
        super.onScrolled(dx, dy);
        if (isHorizontal())
            return;
        final int offset = computeVerticalScrollOffset();
        if (offset <= 0) {
            if (mListener != null)
                mListener.onScrollToTopEdge(this);
        }
        mOffset = offset;
    }

    @Override
    @SuppressWarnings("all")
    public boolean onTouchEvent(MotionEvent e) {
        if (isHorizontal())
            return super.onTouchEvent(e);
        final float lastOffset = mOffset;
        final boolean result = super.onTouchEvent(e);
        if (lastOffset < mOffset) {
            if (mListener != null)
                mListener.onScrollDown(this);
        }
        return result;
    }

    protected int getDisplayWidth() {
        return mItemDecoration.getDisplayWidth(this);
    }

    protected int getDisplayHeight() {
        return mItemDecoration.getDisplayHeight(this);
    }

    protected boolean isHorizontal() {
        return mLayoutManager.getOrientation() == LinearLayoutManager.HORIZONTAL;
    }

    // Listener
    @Override
    public boolean onSingleTap(ScaleRecyclerView view) {
        if (mListener != null) {
            mListener.onClick(this);
            return true;
        }
        return false;
    }

    @Override
    public boolean onDoubleTap(ScaleRecyclerView view) {
        return false;
    }

    /**
     * 璁剧疆鐩戝惉鍣?     *
     * @param listener 鐩戝惉鍣?     */
    public void setOnDisplayListener(OnDisplayListener listener) {
        mListener = listener;
    }

    /**
     * 璁剧疆鏄剧ず鏂瑰紡
     *
     * @param orientation 婊氬姩鏂瑰悜
     * @param paging      鏄惁鍒嗛〉
     * @param scale       缂╂斁姣斾緥
     */
    public void setDisplaySetting(int orientation, boolean paging, float scale) {
        mLayoutManager.setOrientation(orientation);
        mLayoutManager.setPagingEnable(paging);
        setScale(scale);
    }

    /**
     * 鑾峰彇婊氬姩鏂瑰悜
     *
     * @return 婊氬姩鏂瑰悜
     */
    public int getOrientation() {
        return mLayoutManager.getOrientation();
    }

    /**
     * 鏄惁寮€鍚垎椤?     *
     * @return 鍒嗛〉
     */
    public boolean isPagingEnable() {
        return mLayoutManager.isPagingEnable();
    }

    /**
     * 璁剧疆闃呰浣嶇疆
     *
     * @param position   绗嚑椤?     * @param offset     椤靛亸绉?     * @param percentage 鍙︿竴鏂瑰悜鐧惧垎姣?     */
    public void setDisplayLocation(int position, int offset, float percentage) {
        mLayoutManager.setDisplayLocation(position, offset, percentage);
    }

    /**
     * 璁剧疆闃呰浣嶇疆
     *
     * @param position       绗嚑椤?     * @param x              楂樺鏃犲叧鐨勫乏涓婅鍦ㄩ〉闈㈢殑x鍧愭爣
     * @param y              楂樺鏃犲叧鐨勫乏涓婅鍦ㄩ〉闈㈢殑y鍧愭爣
     * @param last           鏄惁涓烘渶鍚庝竴涓瓙椤?     * @param maxChildWidth  鏈€澶у瓙椤瑰
     * @param maxChildHeight 鏈€澶у瓙椤归珮
     * @param width          椤甸潰瀹?     * @param height         椤甸潰楂?     */
    public void setDisplayLocation(int position, float x, float y, boolean last,
                                   float maxChildWidth, float maxChildHeight,
                                   float width, float height) {
        mLayoutManager.setDisplayLocation(position, x, y, last, maxChildWidth, maxChildHeight,
                width, height);
    }

    void getHorizontalDecoration(Rect decoration, int type) {
        mItemDecoration.getHorizontalDecoration(decoration, type);
    }

    void getVerticalDecoration(Rect decoration, int type) {
        mItemDecoration.getVerticalDecoration(decoration, type);
    }

    public interface OnDisplayListener {
        void onClick(DisplayRecyclerView display);

        void onScrollToTopEdge(DisplayRecyclerView display);

        void onScrollDown(DisplayRecyclerView display);

        void onCurrentItemChanged(DisplayRecyclerView display, int position);
    }

    /**
     * 鍐呭鎻愪緵鑰?     *
     * @param <VH> 瑙嗗浘鎸佹湁鑰?     */
    public static abstract class Adapter<VH extends ViewHolder> extends
            MultifunctionalRecyclerView.Adapter<VH> {

        private DisplayRecyclerView mView;

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            if (recyclerView instanceof DisplayRecyclerView) {
                mView = (DisplayRecyclerView) recyclerView;
            }
            super.onAttachedToRecyclerView(recyclerView);
        }

        @Override
        public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
            super.onDetachedFromRecyclerView(recyclerView);
            mView = null;
        }

        protected int getDisplayWidth() {
            return mView == null ? 0 : mView.getDisplayWidth();
        }

        protected int getDisplayHeight() {
            return mView == null ? 0 : mView.getDisplayHeight();
        }

        protected float getChildMaxWidth() {
            return 0;
        }

        protected float getChildMaxHeight() {
            return 0;
        }

        protected boolean isHorizontal() {
            return mView != null && mView.isHorizontal();
        }
    }

    /**
     * 瑙嗗浘鎸佹湁鑰?     */
    public static class ViewHolder extends MultifunctionalRecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}

