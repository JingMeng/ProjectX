package am.project.x.activities.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import am.project.x.R;
import am.project.x.activities.main.adapters.MainPagerAdapter;
import am.widget.gradienttabstrip.GradientTabStrip;

/**
 * 涓婚〉
 */
public class MainActivity extends AppCompatActivity {

    /**
     * 鍚姩涓婚〉
     *
     * @param context Context
     */
    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, MainActivity.class));
    }

    @Override
    @SuppressWarnings("all")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ViewPager vpFragments = (ViewPager) findViewById(R.id.main_vp_fragments);
        GradientTabStrip gtsTabs = (GradientTabStrip) findViewById(R.id.main_gts_tabs);
        MainPagerAdapter adapter = new MainPagerAdapter(getSupportFragmentManager(), this);
        vpFragments.setAdapter(adapter);
        gtsTabs.setAdapter(adapter);
        gtsTabs.bindViewPager(vpFragments);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        ViewPager vpFragments = (ViewPager) findViewById(R.id.main_vp_fragments);
        GradientTabStrip gtsTabs = (GradientTabStrip) findViewById(R.id.main_gts_tabs);
        gtsTabs.bindViewPager(vpFragments);
    }
}

