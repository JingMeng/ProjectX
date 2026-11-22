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

package am.widget.gradienttabstrip;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.viewpager.widget.ViewPager;
import android.text.TextPaint;
import android.util.AttributeSet;

import am.widget.basetabstrip.BaseTabStrip;

/**
 * 婊戝姩娓愬彉TabStrip锛屽瓙椤瑰缓璁笉瓒呰繃5涓? *
 * @author Alex
 */
@ViewPager.DecorView
public class GradientTabStrip extends BaseTabStrip {

    public static final int TAG_MIN_SIZE_MODE_HAS_TEXT = 0;// 褰撳浘鐗囨渶灏忓楂樻洿灏忔椂锛屾寜鍥剧墖璁＄畻
    public static final int TAG_MIN_SIZE_MODE_ALWAYS = 1;// 鎸夌収璁剧疆鐨勬渶灏忓楂?    private static final int DEFAULT_TEXT_SIZE = 14;// 榛樿瀛椾綋澶у皬dp
    private static final int DEFAULT_TEXT_COLOR = 0xff000000;// 榛樿瀛椾綋棰滆壊
    private static final int DEFAULT_TAG_TEXT_SIZE = 11;// 榛樿Tag瀛椾綋澶у皬sp
    private static final int DEFAULT_TAG_TEXT_COLOR = 0xffffffff;// 榛樿Tag鏂囧瓧棰滆壊
    private static final int DEFAULT_TAG_MIN_SIZE = 15;// 榛樿Tag鏈€灏忓ぇ灏廳p
    private static final int[] ATTRS = new int[]{android.R.attr.textSize,
            android.R.attr.textColor, android.R.attr.drawablePadding};
    private final TextPaint mTextPaint;// 鏂囧瓧鐢荤瑪
    private float mTextSize;// 鏂囧瓧澶у皬
    private ColorStateList mTextColor;// 鏂囧瓧棰滆壊
    private int mDrawablePadding;// 鍥炬枃闂磋窛
    private Drawable mInterval;// 瀛愰」闂撮殧
    private int mMinItemWidth;// 鏈€灏忓瓙椤瑰搴?    private GradientTabAdapter mAdapter;// Tag
    private float mTagTextSize;// Tag鏂囧瓧澶у皬
    private int mTagTextColor;// Tag鏂囧瓧棰滆壊
    private Drawable mTagBackground;// Tag鏂囧瓧鑳屾櫙
    private int mTagMinSizeMode;// Tag鏂囧瓧澶у皬妯″紡
    private int mTagMinWidth;// Tag鏂囧瓧鏈€灏忓搴?    private int mTagMinHeight;// Tag鏂囧瓧鏈€灏忛珮搴?    private TagLocation mTagLocation;// Tag甯冨眬
    private Rect mTextMeasureBounds = new Rect();// 鏂囧瓧娴嬮噺
    private int mItemHeight;// 楂樺害
    private float mItemWidth;// 瀹藉害
    private int mMaxDrawableWidth;// 鏈€澶у浘鐗囧
    private int mMaxDrawableHeight;// 鏈€澶у浘鐗囬珮
    private int mCenterGap;// 涓棿闂撮殧
    private int mCurrentPager = 0;
    private int mNextPager = 0;
    private float mOffset = 1;

    public GradientTabStrip(Context context) {
        this(context, null);
    }

    public GradientTabStrip(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GradientTabStrip(Context context, AttributeSet attrs,
                            int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setItemClickable(true);
        final float density = getResources().getDisplayMetrics().density;
        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Align.CENTER);
        mTextPaint.density = getResources().getDisplayMetrics().density;
        mTagLocation = new TagLocation(TagLocation.LOCATION_CONTENT);
        final TypedArray a = context.obtainStyledAttributes(attrs, ATTRS, defStyleAttr, 0);
        int n = a.getIndexCount();
        int textSize = (int) (DEFAULT_TEXT_SIZE * density);
        ColorStateList textColors = null;
        int drawablePadding = 0;
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case 0:
                    textSize = a.getDimensionPixelSize(attr, textSize);
                    break;
                case 1:
                    textColors = a.getColorStateList(attr);
                    break;
                case 2:
                    drawablePadding = a.getDimensionPixelSize(attr, drawablePadding);
                    break;
            }
        }
        a.recycle();
        Drawable itemBackground = null;
        Drawable interval = null;
        int minItemWidth = 0;
        int tagTextSize = (int) (DEFAULT_TAG_TEXT_SIZE * density);
        int tagTextColor = DEFAULT_TAG_TEXT_COLOR;
        Drawable tagBackground = getDefaultTagBackground();
        int tagMinSizeMode = TAG_MIN_SIZE_MODE_HAS_TEXT;
        int tagMinWidth = (int) (DEFAULT_TAG_MIN_SIZE * density);
        int tagMinHeight = (int) (DEFAULT_TAG_MIN_SIZE * density);
        int tagPaddingLeft = 0;
        int tagPaddingTop = 0;
        int tagPaddingRight = 0;
        int tagPaddingBottom = 0;
        int tagMarginLeft = 0;
        int tagMarginTop = 0;
        int tagMarginRight = 0;
        int tagMarginBottom = 0;
        int centerGap = 0;
        TypedArray custom = context.obtainStyledAttributes(attrs, R.styleable.GradientTabStrip);
        textSize = custom.getDimensionPixelSize(R.styleable.GradientTabStrip_gtsTextSize, textSize);
        if (custom.hasValue(R.styleable.GradientTabStrip_gtsTextColor))
            textColors = custom.getColorStateList(R.styleable.GradientTabStrip_gtsTextColor);
        drawablePadding = custom.getDimensionPixelSize(
                R.styleable.GradientTabStrip_gtsDrawablePadding, drawablePadding);
        if (custom.hasValue(R.styleable.GradientTabStrip_gtsBackground))
            itemBackground = custom.getDrawable(R.styleable.GradientTabStrip_gtsBackground);
        if (custom.hasValue(R.styleable.GradientTabStrip_gtsInterval))
            interval = custom.getDrawable(R.styleable.GradientTabStrip_gtsInterval);
        minItemWidth = custom.getDimensionPixelOffset(R.styleable.GradientTabStrip_gtsMinItemWidth,
                minItemWidth);
        tagTextSize = custom.getDimensionPixelSize(R.styleable.GradientTabStrip_gtsTagTextSize,
                tagTextSize);
        tagTextColor = custom.getColor(R.styleable.GradientTabStrip_gtsTagTextColor,
                tagTextColor);
        if (custom.hasValue(R.styleable.GradientTabStrip_gtsTagBackground))
            tagBackground = custom.getDrawable(R.styleable.GradientTabStrip_gtsTagBackground);
        tagMinSizeMode = custom.getInt(R.styleable.GradientTabStrip_gtsTagMinSizeMode,
                tagMinSizeMode);
        tagMinWidth = custom.getDimensionPixelOffset(R.styleable.GradientTabStrip_gtsTagMinWidth,
                tagMinWidth);
        tagMinHeight = custom.getDimensionPixelOffset(R.styleable.GradientTabStrip_gtsTagMinHeight,
                tagMinHeight);
        if (custom.hasValue(R.styleable.GradientTabStrip_gtsTagPadding)) {
            final int padding = custom.getDimensionPixelOffset(
                    R.styleable.GradientTabStrip_gtsTagPadding, 0);
            tagPaddingLeft = padding;
            tagPaddingTop = padding;
            tagPaddingRight = padding;
            tagPaddingBottom = padding;
        }
        tagPaddingLeft = custom.getDimensionPixelOffset(
                R.styleable.GradientTabStrip_gtsTagPaddingLeft, tagPaddingLeft);
        tagPaddingTop = custom.getDimensionPixelOffset(
                R.styleable.GradientTabStrip_gtsTagPaddingTop, tagPaddingTop);
        tagPaddingRight = custom.getDimensionPixelOffset(
                R.styleable.GradientTabStrip_gtsTagPaddingRight, tagPaddingRight);
        tagPaddingBottom = custom.getDimensionPixelOffset(
                R.styleable.GradientTabStrip_gtsTagPaddingBottom, tagPaddingBottom);
        if (custom.hasValue(R.styleable.GradientTabStrip_gtsTagMargin)) {
            final int margin = custom.getDimensionPixelOffset(
                    R.styleable.GradientTabStrip_gtsTagMargin, 0);
            tagMarginLeft = margin;
            tagMarginTop = margin;
            tagMarginRight = margin;
            tagMarginBottom = margin;
        }
        tagMarginLeft = custom.getDimensionPixelOffset(
                R.styleable.GradientTabStrip_gtsTagMarginLeft, tagMarginLeft);
        tagMarginTop = custom.getDimensionPixelOffset(
                R.styleable.GradientTabStrip_gtsTagMarginTop, tagMarginTop);
        tagMarginRight = custom.getDimensionPixelOffset(
                R.styleable.GradientTabStrip_gtsTagMarginRight, tagMarginRight);
        tagMarginBottom = custom.getDimensionPixelOffset(
                R.styleable.GradientTabStrip_gtsTagMarginBottom, tagMarginBottom);
        centerGap = custom.getDimensionPixelOffset(
                R.styleable.GradientTabStrip_gtsCenterGap, centerGap);
        custom.recycle();
        setTextSize(textSize);
        if (textColors != null) {
            setTextColor(textColors);
        } else {
            setTextColor(DEFAULT_TEXT_COLOR);
        }
        setDrawablePadding(drawablePadding);
        setItemBackground(itemBackground);
        setInterval(interval);
        setMinItemWidth(minItemWidth);
        if (isInEditMode()) {
            setAdapter(new TestAdapter(context));
        }
        setTagTextSize(tagTextSize);
        setTagTextColor(tagTextColor);
        setTagBackground(tagBackground);
        setTagMinSizeMode(tagMinSizeMode);
        setTagMinWidth(tagMinWidth);
        setTagMinHeight(tagMinHeight);
        setTagPadding(tagPaddingLeft, tagPaddingTop, tagPaddingRight, tagPaddingBottom);
        setTagMargin(tagMarginLeft, tagMarginTop, tagMarginRight, tagMarginBottom);
        setCenterGap(centerGap);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mTextPaint.setTextSize(mTextSize);
        int maxTextWidth = 0;
        for (int i = 0; i < getItemCount(); i++) {
            if (getItemText(i) != null) {
                String text = getItemText(i).toString();
                mTextPaint.getTextBounds(text, 0, text.length(), mTextMeasureBounds);
                maxTextWidth = Math.max(maxTextWidth, mTextMeasureBounds.width());
            }
        }
        mMaxDrawableWidth = getMaxDrawableWidth();
        mMaxDrawableHeight = getMaxDrawableHeight();
        final int itemBackgroundWith = getMinItemBackgroundWidth();
        final int itemWidth = Math.max(Math.max(maxTextWidth, mMaxDrawableWidth),
                Math.max(mMinItemWidth, itemBackgroundWith));
        final int intervalWidth = getIntervalWidth();
        final int totalWidth = itemWidth * getItemCount() +
                intervalWidth * (getItemCount() - 1) +
                ViewCompat.getPaddingStart(this) + ViewCompat.getPaddingEnd(this) + mCenterGap;
        final int width = Math.max(totalWidth, getSuggestedMinimumWidth());

        mItemHeight = mMaxDrawableHeight + mDrawablePadding + mTextMeasureBounds.height();
        final int itemBackgroundHeight = getMinItemBackgroundHeight();
        final int intervalHeight = mInterval == null ? 0 : mInterval.getIntrinsicHeight();
        final int itemHeight = Math.max(Math.max(mItemHeight, intervalHeight),
                itemBackgroundHeight);
        final int height = Math.max(itemHeight + getPaddingTop() + getPaddingBottom(),
                getSuggestedMinimumHeight());
        setMeasuredDimension(resolveSize(width, widthMeasureSpec),
                resolveSize(height, heightMeasureSpec));
        mItemWidth = (float) (getMeasuredWidth() - ViewCompat.getPaddingStart(this)
                - ViewCompat.getPaddingEnd(this) - (getItemCount() % 2 == 0 ? mCenterGap : 0)
                - getIntervalWidth() * (getItemCount() - 1))
                / getItemCount();
    }

    /**
     * 鑾峰彇鏈€澶у浘鐗囧搴?     *
     * @return 鏈€澶у浘鐗囧搴?     */
    protected int getMaxDrawableWidth() {
        if (mAdapter == null)
            return 0;
        int maxWidth = 0;
        for (int position = 0; position < getItemCount(); position++) {
            Drawable normal = mAdapter.getNormalDrawable(position, getContext());
            Drawable selected = mAdapter.getSelectedDrawable(position, getContext());
            if (normal != null) {
                maxWidth = Math.max(maxWidth, normal.getIntrinsicWidth());
            }
            if (selected != null) {
                maxWidth = Math.max(maxWidth, selected.getIntrinsicWidth());
            }
        }
        return maxWidth;
    }

    /**
     * 鑾峰彇鏈€澶у浘鐗囬珮搴?     *
     * @return 鏈€澶у浘鐗囬珮搴?     */
    protected int getMaxDrawableHeight() {
        if (mAdapter == null)
            return 0;
        int maxHeight = 0;
        for (int position = 0; position < getItemCount(); position++) {
            Drawable normal = mAdapter.getNormalDrawable(position, getContext());
            Drawable selected = mAdapter.getSelectedDrawable(position, getContext());
            if (normal != null) {
                maxHeight = Math.max(maxHeight, normal.getIntrinsicHeight());
            }
            if (selected != null) {
                maxHeight = Math.max(maxHeight, selected.getIntrinsicHeight());
            }
        }
        return maxHeight;
    }

    /**
     * 鑾峰彇闂撮殧瀹藉害
     *
     * @return 闂撮殧瀹藉害
     */
    protected int getIntervalWidth() {
        return mInterval == null ? 0 : mInterval.getIntrinsicWidth();
    }

    @Override
    protected void jumpTo(int current) {
        mCurrentPager = -1;
        mNextPager = current;
        mOffset = 1;
        invalidate();
    }

    @Override
    protected void gotoLeft(int current, int next, float offset) {
        mCurrentPager = current;
        mNextPager = next;
        mOffset = 1 - offset;
        invalidate();
    }

    @Override
    protected void gotoRight(int current, int next, float offset) {
        mCurrentPager = current;
        mNextPager = next;
        mOffset = offset;
        invalidate();
    }

    @Override
    protected int pointToPosition(float x, float y) {
        final int paddingTop = getPaddingTop();
        final int paddingBottom = getPaddingBottom();
        if (y < paddingTop || y > getWidth() - paddingBottom) {
            return -1;
        }
        int position = -1;
        final int intervalWidth = getIntervalWidth();
        for (int i = 0; i < getItemCount(); i++) {
            float l = ViewCompat.getPaddingStart(this) + intervalWidth * i + mItemWidth * i +
                    ((i * 2 >= getItemCount() - 1 && getItemCount() % 2 == 0) ? mCenterGap : 0);
            float r = l + mItemWidth;
            if (x >= l && x <= r) {
                position = i;
                break;
            }
        }
        return position;
    }

    @Override
    protected float getHotspotX(Drawable background, int position, float motionX, float motionY) {
        return motionX - getPaddingLeft() - getIntervalWidth() * position - mItemWidth * position
                - ((position * 2 >= getItemCount() - 1 && getItemCount() % 2 == 0) ? mCenterGap : 0);
    }

    @Override
    protected float getHotspotY(Drawable background, int position, float motionX, float motionY) {
        return motionY;
    }

    @Override
    protected int getItemCount() {
        if (isInEditMode()) {
            return 4;
        }
        return super.getItemCount();
    }

    @Override
    protected CharSequence getItemText(int position) {
        if (isInEditMode()) {
            return "Tab" + position;
        }
        return super.getItemText(position);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int position = 0; position < getItemCount(); position++) {
            drawItem(canvas, position);
        }
    }

    /**
     * 缁樺埗瀛愰」
     *
     * @param canvas 鐢诲竷
     */
    protected void drawItem(Canvas canvas, int position) {
        drawItemBackground(canvas, position);
        drawInterval(canvas, position);
        drawDrawable(canvas, position);
        drawText(canvas, position);
        drawTag(canvas, position);
    }

    /**
     * 缁樺埗鑳屾櫙
     *
     * @param canvas   鐢诲竷
     * @param position 瀛愰」鍧愭爣
     */
    protected void drawItemBackground(Canvas canvas, int position) {
        if (!hasItemBackgrounds())
            return;
        Drawable tag = getItemBackground(position);
        if (position == getItemCount() - 1) {
            int restWidth = getWidth() - ViewCompat.getPaddingStart(this)
                    - ViewCompat.getPaddingEnd(this) - getIntervalWidth() * (getItemCount() - 1)
                    - (int) mItemWidth * (getItemCount() - 1);
            tag.setBounds(0, 0, restWidth, getHeight() - getPaddingTop() - getPaddingBottom());
        } else {
            tag.setBounds(0, 0, (int) mItemWidth,
                    getHeight() - getPaddingTop() - getPaddingBottom());
        }
        final float moveX = ViewCompat.getPaddingStart(this) +
                (mItemWidth + getIntervalWidth()) * position
                + ((position * 2 >= getItemCount() - 1 && getItemCount() % 2 == 0) ? mCenterGap : 0);
        final float moveY = getPaddingTop();
        canvas.save();
        canvas.translate(moveX, moveY);
        tag.draw(canvas);
        canvas.restore();
    }

    /**
     * 缁樺埗闂撮殧
     *
     * @param canvas   鐢诲竷
     * @param position 瀛愰」鍧愭爣
     */
    protected void drawInterval(Canvas canvas, int position) {
        if (mInterval == null || mInterval.getIntrinsicWidth() <= 0
                || position == getItemCount() - 1)
            return;
        if (getItemCount() % 2 == 0 && position + 1 == getItemCount() / 2)
            return;
        final int intervalHeight = mInterval.getIntrinsicHeight() <= 0 ? getHeight()
                - getPaddingTop() - getPaddingBottom() : mInterval.getIntrinsicHeight();
        mInterval.setBounds(0, 0, getIntervalWidth(), intervalHeight);
        final float moveX = ViewCompat.getPaddingStart(this) + mItemWidth * position
                + ((position * 2 >= getItemCount() - 1 && getItemCount() % 2 == 0) ? mCenterGap : 0);
        final float moveY = getPaddingTop() +
                (getHeight() - getPaddingTop() - getPaddingBottom()) * 0.5f - intervalHeight * 0.5f;
        canvas.save();
        canvas.translate(moveX, moveY);
        mInterval.draw(canvas);
        canvas.restore();
    }

    /**
     * 缁樺埗鍥剧墖
     *
     * @param canvas   鐢诲竷
     * @param position 瀛愰」鍧愭爣
     */
    protected void drawDrawable(Canvas canvas, int position) {
        if (mAdapter == null)
            return;
        Drawable normal = mAdapter.getNormalDrawable(position, getContext());
        Drawable selected = mAdapter.getSelectedDrawable(position, getContext());
        if (normal == null && selected == null)
            return;
        float alphaNormal;
        float alphaSelected;
        if (position == mNextPager) {
            alphaNormal = 1 - mOffset;
            alphaSelected = mOffset;
        } else if (position == mCurrentPager) {
            alphaNormal = mOffset;
            alphaSelected = 1 - mOffset;
        } else {
            alphaNormal = 1;
            alphaSelected = 0;
        }
        final float drawableCenterX = ViewCompat.getPaddingStart(this) +
                (mItemWidth + getIntervalWidth()) * position + mItemWidth * 0.5f +
                ((position * 2 >= getItemCount() - 1 && getItemCount() % 2 == 0) ? mCenterGap : 0);
        final float drawableCenterY = getPaddingTop() + (getHeight() - getPaddingTop()
                - getPaddingBottom()) * 0.5f - mItemHeight * 0.5f + mMaxDrawableHeight * 0.5f;
        canvas.save();
        canvas.translate(drawableCenterX, drawableCenterY);
        if (normal != null) {
            final int normalWidth = normal.getIntrinsicWidth();
            final int normalHeight = normal.getIntrinsicHeight();
            normal.setAlpha((int) Math.ceil(0xFF * alphaNormal));
            normal.setBounds(0, 0, normalWidth, normalHeight);
            canvas.save();
            canvas.translate(-normalWidth * 0.5f, -normalHeight * 0.5f);
            normal.draw(canvas);
            canvas.restore();
        }
        if (selected != null) {
            final int selectedWidth = selected.getIntrinsicWidth();
            final int selectedHeight = selected.getIntrinsicHeight();
            selected.setAlpha((int) Math.ceil(0xFF * alphaSelected));
            selected.setBounds(0, 0, selectedWidth, selectedHeight);
            canvas.save();
            canvas.translate(-selectedWidth * 0.5f, -selectedHeight * 0.5f);
            selected.draw(canvas);
            canvas.restore();
        }
        canvas.restore();
    }

    /**
     * 缁樺埗鏂囧瓧
     *
     * @param canvas   鐢诲竷
     * @param position 瀛愰」鍧愭爣
     */
    protected void drawText(Canvas canvas, int position) {
        if (getItemText(position) == null)
            return;
        String text = getItemText(position).toString();
        if (text.length() <= 0)
            return;
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.getTextBounds(text, 0, text.length(), mTextMeasureBounds);
        final int textHeight = mTextMeasureBounds.height();
        if (mTextColor == null) {
            mTextPaint.setColor(DEFAULT_TEXT_COLOR);
        } else {
            final int normalColor = mTextColor.getDefaultColor();
            final int selectedColor = mTextColor.getColorForState(SELECTED_STATE_SET, normalColor);
            if (position == mNextPager) {
                mTextPaint.setColor(getColor(normalColor, selectedColor, mOffset));
            } else if (position == mCurrentPager) {
                mTextPaint.setColor(getColor(normalColor, selectedColor, 1 - mOffset));
            } else {
                mTextPaint.setColor(normalColor);
            }
        }
        final int contentHeight = mMaxDrawableHeight + mDrawablePadding + textHeight;
        final float centerX = ViewCompat.getPaddingStart(this) +
                (mItemWidth + getIntervalWidth()) * (position + 0.5f) +
                ((position * 2 >= getItemCount() - 1 && getItemCount() % 2 == 0) ? mCenterGap : 0);
        final float centerY = getPaddingTop()
                + (getHeight() - getPaddingTop() - getPaddingBottom()) * 0.5f;
        canvas.save();
        canvas.translate(centerX, centerY + contentHeight * 0.5f - textHeight);
        canvas.drawText(text, 0, -mTextMeasureBounds.top, mTextPaint);
        canvas.restore();
    }

    /**
     * 缁樺埗Tag
     *
     * @param canvas   鐢诲竷
     * @param position 瀛愰」鍧愭爣
     */
    protected void drawTag(Canvas canvas, int position) {
        if (mAdapter == null || !mAdapter.isTagEnable(position))
            return;
        String text = mAdapter.getTag(position) == null ? "" : mAdapter.getTag(position);
        mTextPaint.setTextSize(mTagTextSize);
        mTextPaint.setColor(mTagTextColor);
        mTextPaint.getTextBounds(text, 0, text.length(), mTextMeasureBounds);
        final int textWidth = mTextMeasureBounds.width();
        final int textHeight = mTextMeasureBounds.height();
        final int tagBackgroundWidth = mTagBackground == null ?
                0 : mTagBackground.getIntrinsicWidth();
        final int tagBackgroundHeight = mTagBackground == null ?
                0 : mTagBackground.getIntrinsicHeight();
        int tagWidth;
        int tagHeight;
        switch (mTagMinSizeMode) {
            default:
            case TAG_MIN_SIZE_MODE_HAS_TEXT:
                if (text.length() == 0) {
                    tagWidth = Math.min(mTagMinWidth, tagBackgroundWidth);
                    tagHeight = Math.min(mTagMinHeight, tagBackgroundHeight);
                    break;
                }
            case TAG_MIN_SIZE_MODE_ALWAYS:
                tagWidth = Math.max(
                        textWidth + mTagLocation.getPaddingLeft() + mTagLocation.getPaddingRight(),
                        Math.max(mTagMinWidth, tagBackgroundWidth));
                tagHeight = Math.max(
                        textHeight + mTagLocation.getPaddingTop() + mTagLocation.getPaddingBottom(),
                        Math.max(mTagMinHeight, tagBackgroundHeight));
                break;
        }
        final float centerX = ViewCompat.getPaddingStart(this) +
                (mItemWidth + getIntervalWidth()) * ((float) position + 0.5f) +
                ((position * 2 >= getItemCount() - 1 && getItemCount() % 2 == 0) ? mCenterGap : 0);
        final float centerY = getPaddingTop()
                + (getHeight() - getPaddingTop() - getPaddingBottom()) * 0.5f;
        final float tagCenterX = centerX + mMaxDrawableWidth * 0.5f;
        final float tagCenterY = centerY - mItemHeight * 0.5f + tagHeight * 0.5f;
        final float tagLeft = tagCenterX - tagWidth * 0.5f + mTagLocation.getMarginLeft();
        final float tagTop = tagCenterY - tagHeight * 0.5f - mTagLocation.getMarginBottom();
        canvas.save();
        canvas.translate(tagLeft, tagTop);
        if (mTagBackground != null) {
            mTagBackground.setBounds(0, 0, tagWidth, tagHeight);
            mTagBackground.draw(canvas);
        }
        if (text.length() > 0) {
            canvas.translate(mTagLocation.getPaddingLeft() +
                    (tagWidth - mTagLocation.getPaddingLeft() - mTagLocation.getPaddingRight())
                            * 0.5f, mTagLocation.getPaddingTop());
            if (tagHeight > textHeight) {
                canvas.translate(0, (tagHeight - textHeight) * 0.5f);
            }
            canvas.drawText(text, 0, -mTextMeasureBounds.top, mTextPaint);
        }
        canvas.restore();
    }

    /**
     * 鑾峰彇鏂囧瓧澶у皬
     *
     * @return 鏂囧瓧澶у皬
     */
    @SuppressWarnings("unused")
    public float getTextSize() {
        return mTextSize;
    }

    /**
     * 璁剧疆鏂囧瓧澶у皬
     *
     * @param textSize 鏂囧瓧澶у皬
     */
    public void setTextSize(int textSize) {
        if (mTextSize != textSize) {
            mTextSize = textSize;
            requestLayout();
            invalidate();
        }
    }

    /**
     * 鑾峰彇鏂囧瓧棰滆壊
     *
     * @return 鏂囧瓧棰滆壊
     */
    @SuppressWarnings("unused")
    public ColorStateList getTextColor() {
        return mTextColor;
    }

    /**
     * 璁剧疆鏂囧瓧棰滆壊
     *
     * @param color 鏂囧瓧棰滆壊
     */
    public void setTextColor(ColorStateList color) {
        if (color != null && color != mTextColor) {
            mTextColor = color;
            invalidate();
        }
    }

    /**
     * 璁剧疆鏂囧瓧棰滆壊
     *
     * @param color 鏂囧瓧棰滆壊
     */
    public void setTextColor(@ColorInt int color) {
        setTextColor(ColorStateList.valueOf(color));
    }

    /**
     * 鑾峰彇鍥炬枃闂磋窛
     *
     * @return 鍥炬枃闂磋窛
     */
    @SuppressWarnings("unused")
    public int getDrawablePadding() {
        return mDrawablePadding;
    }

    /**
     * 璁剧疆鍥炬枃闂磋窛
     *
     * @param padding 鍥炬枃闂磋窛
     */
    public void setDrawablePadding(int padding) {
        if (padding >= 0 && padding != mDrawablePadding) {
            mDrawablePadding = padding;
            requestLayout();
            invalidate();
        }
    }

    /**
     * 鑾峰彇瀛愰」闂撮殧
     *
     * @return 瀛愰」闂撮殧
     */
    @SuppressWarnings("unused")
    public Drawable getInterval() {
        return mInterval;
    }

    /**
     * 璁剧疆瀛愰」闂撮殧
     *
     * @param interval 瀛愰」闂撮殧
     */
    public void setInterval(Drawable interval) {
        if (mInterval != interval) {
            mInterval = interval;
            invalidate();
        }
    }

    /**
     * 璁剧疆瀛愰」闂撮殧
     *
     * @param interval 瀛愰」闂撮殧
     */
    @SuppressWarnings("unused")
    public void setInterval(@DrawableRes int interval) {
        setInterval(ContextCompat.getDrawable(getContext(), interval));
    }

    /**
     * 鑾峰彇瀛愰」鏈€灏忓搴?     *
     * @return 瀛愰」鏈€灏忓搴?     */
    @SuppressWarnings("unused")
    public int getMinItemWidth() {
        return mMinItemWidth;
    }

    /**
     * 璁剧疆瀛愰」鏈€灏忓搴?     *
     * @param width 瀛愰」鏈€灏忓搴?     */
    public void setMinItemWidth(int width) {
        if (width >= 0 && width != mMinItemWidth) {
            mMinItemWidth = width;
            requestLayout();
            invalidate();
        }
    }

    /**
     * 鑾峰彇Adapter
     *
     * @return Adapter
     */
    public GradientTabAdapter getAdapter() {
        return mAdapter;
    }

    /**
     * 璁剧疆Adapter
     *
     * @param adapter Adapter
     */
    public void setAdapter(GradientTabAdapter adapter) {
        if (mAdapter != adapter) {
            mAdapter = adapter;
            requestLayout();
            invalidate();
        }
    }

    /**
     * 鑾峰彇Tag鏂囧瓧澶у皬
     *
     * @return Tag鏂囧瓧澶у皬
     */
    @SuppressWarnings("unused")
    public float getTagTextSize() {
        return mTagTextSize;
    }

    /**
     * 璁剧疆Tag鏂囧瓧澶у皬
     *
     * @param size Tag鏂囧瓧澶у皬
     */
    public void setTagTextSize(float size) {
        if (mTagTextSize != size) {
            mTagTextSize = size;
            invalidate();
        }
    }

    /**
     * 鑾峰彇Tag鏂囧瓧棰滆壊
     *
     * @return Tag鏂囧瓧棰滆壊
     */
    @SuppressWarnings("unused")
    public int getTagTextColor() {
        return mTagTextColor;
    }

    /**
     * 璁剧疆Tag鏂囧瓧棰滆壊
     *
     * @param color Tag鏂囧瓧棰滆壊
     */
    public void setTagTextColor(@ColorInt int color) {
        if (mTagTextColor != color) {
            mTagTextColor = color;
            invalidate();
        }
    }

    /**
     * 鑾峰彇Tag鑳屾櫙
     *
     * @return Tag鑳屾櫙
     */
    @SuppressWarnings("unused")
    public Drawable getTagBackground() {
        return mTagBackground;
    }

    /**
     * 璁剧疆Tag鑳屾櫙
     *
     * @param background Tag鑳屾櫙
     */
    public void setTagBackground(Drawable background) {
        if (mTagBackground != background) {
            mTagBackground = background;
            invalidate();
        }
    }

    /**
     * 璁剧疆Tag鑳屾櫙
     *
     * @param background Tag鑳屾櫙
     */
    @SuppressWarnings("unused")
    public void setTagBackground(@DrawableRes int background) {
        setTagBackground(ContextCompat.getDrawable(getContext(), background));
    }

    /**
     * 鑾峰彇Tag鏈€灏忛珮瀹借绠楁ā寮?     *
     * @return Tag鏈€灏忛珮瀹借绠楁ā寮?     */
    @SuppressWarnings("unused")
    public int getTagMinSizeMode() {
        return mTagMinSizeMode;
    }

    /**
     * 璁剧疆Tag鏈€灏忛珮瀹借绠楁ā寮?     *
     * @param mode Tag鏈€灏忛珮瀹借绠楁ā寮?     */
    public void setTagMinSizeMode(int mode) {
        if (mode != TAG_MIN_SIZE_MODE_HAS_TEXT && mode != TAG_MIN_SIZE_MODE_ALWAYS)
            return;
        if (mTagMinSizeMode != mode) {
            mTagMinSizeMode = mode;
            invalidate();
        }
    }

    /**
     * 鑾峰彇Tag鏈€灏忓搴?     *
     * @return Tag鏈€灏忓搴?     */
    @SuppressWarnings("unused")
    public int getTagMinWidth() {
        return mTagMinWidth;
    }

    /**
     * 璁剧疆Tag鏈€灏忓搴?     *
     * @param width Tag鏈€灏忓搴?     */
    public void setTagMinWidth(int width) {
        if (width >= 0 && mTagMinWidth != width) {
            mTagMinWidth = width;
            invalidate();
        }
    }

    /**
     * 鑾峰彇Tag鏈€灏忛珮搴?     *
     * @return Tag鏈€灏忛珮搴?     */
    @SuppressWarnings("unused")
    public int getTagMinHeight() {
        return mTagMinHeight;
    }

    /**
     * 璁剧疆Tag鏈€灏忛珮搴?     *
     * @param height Tag鏈€灏忛珮搴?     */
    public void setTagMinHeight(int height) {
        if (height >= 0 && mTagMinHeight != height) {
            mTagMinHeight = height;
            invalidate();
        }
    }

    /**
     * 璁剧疆Tag Padding
     *
     * @param left   宸?     * @param top    涓?     * @param right  鍙?     * @param bottom 涓?     */
    public void setTagPadding(int left, int top, int right, int bottom) {
        if (mTagLocation.setPadding(left, top, right, bottom)) {
            invalidate();
        }
    }

    /**
     * 璁剧疆Tag Margin
     *
     * @param left   宸?     * @param top    涓?     * @param right  鍙?     * @param bottom 涓?     */
    public void setTagMargin(int left, int top, int right, int bottom) {
        if (mTagLocation.setMargin(left, top, right, bottom)) {
            invalidate();
        }
    }

    /**
     * 璁剧疆涓棿闂撮殧锛屼粎鍙屾暟Item鏈夋晥锛屽崟鏁版棤鏁?     *
     * @param gap 闂撮殧
     */
    public void setCenterGap(int gap) {
        if (gap > 0 && gap != mCenterGap) {
            mCenterGap = gap;
            requestLayout();
            invalidate();
        }
    }

    /**
     * 鏁版嵁瀹瑰櫒Adapter
     *
     * @author Alex
     */
    public interface GradientTabAdapter extends ItemTabAdapter {

        /**
         * 鑾峰彇鏅€氱姸鎬佷笅鐨?Drawable
         *
         * @param position 浣嶇疆
         * @param context  Context
         * @return 蹇呴』闈炵┖
         */
        Drawable getNormalDrawable(int position, Context context);

        /**
         * 鑾峰彇閫変腑鐘舵€佷笅鐨?Drawable
         *
         * @param position 浣嶇疆
         * @param context  Context
         * @return 蹇呴』闈炵┖
         */
        Drawable getSelectedDrawable(int position, Context context);
    }

    /**
     * 绠€鍗曠殑鏁版嵁瀹瑰櫒Adapter
     *
     * @author Alex
     */
    public static abstract class SimpleGradientTabAdapter implements GradientTabAdapter {

        /**
         * 鑾峰彇鏅€氱姸鎬佷笅鐨?Drawable
         *
         * @param position 浣嶇疆
         * @param context  Context
         * @return 蹇呴』闈炵┖
         */
        public abstract Drawable getNormalDrawable(int position, Context context);

        /**
         * 鑾峰彇閫変腑鐘舵€佷笅鐨?Drawable
         *
         * @param position 浣嶇疆
         * @param context  Context
         * @return 蹇呴』闈炵┖
         */
        public abstract Drawable getSelectedDrawable(int position, Context context);

        public boolean isTagEnable(int position) {
            return false;
        }

        public String getTag(int position) {
            return null;
        }
    }

    private class TestAdapter extends SimpleGradientTabAdapter {

        private GradientDrawable selected;
        private GradientDrawable normal;

        TestAdapter(Context context) {
            final int size = (int) (context.getResources().getDisplayMetrics().density * 10);
            selected = new GradientDrawable();
            selected.setShape(GradientDrawable.OVAL);
            selected.setColor(0xff808080);
            selected.setSize(size, size);
            normal = new GradientDrawable();
            normal.setShape(GradientDrawable.OVAL);
            normal.setColor(0x80808080);
            normal.setSize(size, size);
        }

        @Override
        public Drawable getNormalDrawable(int position, Context context) {
            return normal;
        }

        @Override
        public Drawable getSelectedDrawable(int position, Context context) {
            return selected;
        }
    }
}
