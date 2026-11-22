package am.project.x.notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

/**
 * 閫氱煡鏋勯€犲櫒
 * Created by Alex on 2017/11/2.
 */

public class NotificationMaker {
    private static final int ID_SCAN = 1;
    private static final int ID_DEFAULT = 2;

    /**
     * 閫氱煡鎵弿
     *
     * @param context Context
     * @param title   鏍囬
     * @param text    鍐呭
     */
    public static void notifyScan(Context context, String title, String text) {
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.setBigContentTitle(title).bigText(text);
        NotificationCompat.Builder builder =
                NotificationBuilderHelper.getNotificationBuilderMin(context);
        builder.setCategory(NotificationCompat.CATEGORY_PROGRESS)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setStyle(bigTextStyle)
                .setContentTitle(title)
                .setOngoing(true)
                .setContentText(text);
        NotificationManagerCompat.from(context).notify(ID_SCAN, builder.build());
    }

    /**
     * 鑾峰彇涓嬭浇涓€氱煡
     *
     * @param context       Context
     * @param title         鏍囬
     * @param text          鍐呭
     * @param progress      杩涘害鎸?     * @param indeterminate 鏄惁涓轰笉纭畾杩涘害
     * @return 涓嬭浇涓€氱煡
     */
    public static Notification getNotificationDownloading(Context context,
                                                          String title, String text,
                                                          int progress, boolean indeterminate) {
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.setBigContentTitle(title).bigText(text);
        NotificationCompat.Builder builder =
                NotificationBuilderHelper.getNotificationBuilderLow(context);
        builder.setCategory(NotificationCompat.CATEGORY_PROGRESS)
                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                .setAutoCancel(true)
                .setStyle(bigTextStyle)
                .setContentTitle(title)
                .setContentText(text)
                .setProgress(100, progress, indeterminate);
        return builder.build();
    }


    /**
     * 鍙戦€佹帹閫侀粯璁ら€氱煡
     *
     * @param context   Context
     * @param title     娑堟伅鏍囬
     * @param text      娑堟伅鏂囨湰
     * @param tag       鏍囩
     * @param broadcast 骞挎挱
     */
    public static void notifyPushDefault(Context context, String title, String text, String tag,
                                         @Nullable Intent broadcast) {
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.setBigContentTitle(title).bigText(text);
        NotificationCompat.Builder builder =
                NotificationBuilderHelper.getNotificationBuilderDefault(context);
        builder.setCategory(NotificationCompat.CATEGORY_PROMO)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setAutoCancel(true)
                .setStyle(bigTextStyle)
                .setContentTitle(title)
                .setContentText(text);
        if (broadcast != null) {
            builder.setContentIntent(PendingIntent.getBroadcast(context, ID_DEFAULT, broadcast,
                    PendingIntent.FLAG_UPDATE_CURRENT));
        }
        NotificationManagerCompat.from(context).notify(tag, ID_DEFAULT, builder.build());
    }
}

