package am.project.x.activities;

import android.os.Bundle;
import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

/**
 * 鍩虹绫? * Created by Alex on 2016/5/23.
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int layout = getContentViewLayoutResources();
        if (layout != 0) {
            setContentView(layout);
        }
        initResource(savedInstanceState);
    }

    /**
     * 鑾峰彇鍐呭甯冨眬
     *
     * @return 甯冨眬璧勬簮ID
     */
    protected abstract int getContentViewLayoutResources();

    /**
     * 鍒濆鍖?     */
    protected abstract void initResource(Bundle savedInstanceState);


    /**
     * 璁剧疆Toolbar 涓篈ctionBar
     *
     * @param toolbarId Toolbar璧勬簮ID
     * @param showTitle 鏄惁鏄剧ず鏍囬
     */
    public void setSupportActionBar(@IdRes int toolbarId, boolean showTitle) {
        Toolbar mToolbar = (Toolbar) findViewById(toolbarId);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            mToolbar.setNavigationOnClickListener(new NavigationOnClickListener());
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayShowTitleEnabled(showTitle);
            }
        }
    }

    /**
     * 璁剧疆Toolbar 涓篈ctionBar
     *
     * @param toolbarId Toolbar璧勬簮ID
     */
    public void setSupportActionBar(@IdRes int toolbarId) {
        setSupportActionBar(toolbarId, false);
    }

    /**
     * Toolbar 杩斿洖鎸夐挳鐐瑰嚮
     */
    protected void onNavigationClick() {
        onBackPressed();
    }

    private class NavigationOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            onNavigationClick();
        }
    }
}

