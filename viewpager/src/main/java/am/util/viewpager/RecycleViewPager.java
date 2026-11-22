package am.util.viewpager;

import android.content.Context;
import android.content.res.Configuration;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;

import am.util.viewpager.adapter.RecyclePagerAdapter;
import am.util.viewpager.adapter.ViewsPagerAdapter;

/**
 * 涓嶳ecyclePagerAdapter鎴朧iewsPagerAdapter閰嶅悎浣跨敤鐨刅iewPager
 * Created by Alex on 2017/11/14.
 */
@SuppressWarnings("unused")
public class RecycleViewPager extends ViewPager {
    public RecycleViewPager(Context context) {
        super(context);
    }

    public RecycleViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        final PagerAdapter adapter = getAdapter();
        if (adapter instanceof RecyclePagerAdapter) {
            ((RecyclePagerAdapter) adapter).onConfigurationChanged(newConfig);
        }
        if (adapter instanceof ViewsPagerAdapter) {
            ((ViewsPagerAdapter) adapter).onConfigurationChanged(newConfig);
        }
    }
}

