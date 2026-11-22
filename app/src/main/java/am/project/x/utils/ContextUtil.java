package am.project.x.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import androidx.core.content.FileProvider;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.ViewConfiguration;

import java.io.File;

import am.project.x.BuildConfig;


/**
 * Activity宸ュ叿绫? * Created by Xiang Zhicheng on 2017/5/9.
 */

public class ContextUtil {

    /**
     * 鑾峰彇鐘舵€佹爮楂樺害
     *
     * @param context Context
     * @return 鐘舵€佹爮楂樺害
     */
    public static int getStateBarHeight(Context context) {
        int statusBarHeight = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen",
                "android");
        if (resourceId > 0) {
            statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }

    /**
     * 鑾峰彇瀵艰埅鏍忥紙铏氭嫙閿洏鏍忥紝骞堕潪鎵€鏈夎澶囬兘鏈夛級楂樺害
     *
     * @param context Context
     * @return 瀵艰埅鏍忛珮搴?     */
    public static int getNavBarHeight(Context context) {
        int result = 0;
        boolean hasMenuKey = ViewConfiguration.get(context).hasPermanentMenuKey();
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);

        if (!hasMenuKey && !hasBackKey) {
            //The device has a navigation bar
            Resources resources = context.getResources();

            int orientation = resources.getConfiguration().orientation;
            int resourceId;
            if (isTablet(context)) {
                resourceId = resources.getIdentifier(orientation ==
                        Configuration.ORIENTATION_PORTRAIT ? "navigation_bar_height" :
                        "navigation_bar_height_landscape", "dimen", "android");
            } else {
                resourceId = resources.getIdentifier(orientation ==
                        Configuration.ORIENTATION_PORTRAIT ? "navigation_bar_height" :
                        "navigation_bar_width", "dimen", "android");
            }

            if (resourceId > 0) {
                return resources.getDimensionPixelSize(resourceId);
            }
        }
        return result;
    }


    private static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    /**
     * 鎵撳紑娴忚鍣ㄨ闂綉鍧€
     *
     * @param context Context
     * @param url     缃戝潃
     */
    public static boolean openBrowser(Context context, String url) {
        Uri webPage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webPage);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
            return true;
        }
        return false;
    }

    /**
     * 鎵撳紑鐢靛瓙閭欢鍙戦€侀偖浠?     *
     * @param context   Context
     * @param addresses 閭鍦板潃
     */
    public static void openEmail(Context context, String... addresses) {
        openEmail(null, context, addresses);
    }

    /**
     * 鎵撳紑鐢靛瓙閭欢鍙戦€侀偖浠?     *
     * @param subject   涓婚
     * @param context   Context
     * @param addresses 閭鍦板潃
     */
    public static void openEmail(String subject, Context context, String... addresses) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        if (subject != null)
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }

    /**
     * 鍙戦€丳DF鏂囦欢
     *
     * @param context  Context
     * @param filePath 鏂囦欢璺緞
     * @param title    鏍囬
     */
    public static void sendPDFFile(Context context, String filePath, String title) {
        // TODO
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        Uri url = FileProvider.getUriForFile(context,
                context.getApplicationContext().getPackageName() + ".provider",
                new File(filePath));
        shareIntent.putExtra(Intent.EXTRA_STREAM, url);
        shareIntent.setType("application/pdf");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        if (shareIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(Intent.createChooser(shareIntent, title));
        }
    }

    public static boolean isMainProcess(Context context) {
        final int pid = android.os.Process.myPid();
        String processName = "";
        ActivityManager mActivityManager = (ActivityManager)
                context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess :
                mActivityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                processName = appProcess.processName;
                break;
            }
        }
        return BuildConfig.APPLICATION_ID.equals(processName);
    }
}
