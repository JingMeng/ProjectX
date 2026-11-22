package am.project.x.activities.welcome;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import am.project.x.R;
import am.project.x.activities.main.MainActivity;

/**
 * 娆㈣繋鐣岄潰
 */
public class WelcomeActivity extends AppCompatActivity implements Runnable{

    private static final int TIME_DELAYED = 1500;
    private WelcomeActivity me = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        getWindow().getDecorView().postDelayed(me, TIME_DELAYED);
    }

    @Override
    public void run() {
        if (isFinishing()) {
            return;
        }
        MainActivity.startActivity(me);
        finish();
    }
}

