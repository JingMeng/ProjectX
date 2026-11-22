package am.project.x.notification;

import android.content.Context;
import androidx.core.app.NotificationCompat;

/**
 * 閫氱煡鏋勫缓鍣ㄨ緟鍔╃被
 * Created by Alex on 2017/11/2.
 */

@SuppressWarnings("unused")
class NotificationBuilderHelper {

    static NotificationCompat.Builder getNotificationBuilderMin(Context context) {
        return new NotificationCompat.Builder(context,
                NotificationChannelHelper.getChannelMin(context))
//                .setSmallIcon(R.drawable.ic_notification)
//                .setColor(ContextCompat.getColor(context, R.color.common_style_primary))
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setDefaults(NotificationCompat.DEFAULT_ALL);
    }

    static NotificationCompat.Builder getNotificationBuilderLow(Context context) {
        return new NotificationCompat.Builder(context,
                NotificationChannelHelper.getChannelLow(context))
//                .setSmallIcon(R.drawable.ic_notification)
//                .setColor(ContextCompat.getColor(context, R.color.common_style_primary))
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setDefaults(NotificationCompat.DEFAULT_ALL);
    }

    static NotificationCompat.Builder getNotificationBuilderDefault(Context context) {
        return new NotificationCompat.Builder(context,
                NotificationChannelHelper.getChannelDefault(context))
//                .setSmallIcon(R.drawable.ic_notification)
//                .setColor(ContextCompat.getColor(context, R.color.common_style_primary))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setDefaults(NotificationCompat.DEFAULT_ALL);
    }

    static NotificationCompat.Builder getNotificationBuilderHigh(Context context) {
        return new NotificationCompat.Builder(context,
                NotificationChannelHelper.getChannelHigh(context))
//                .setSmallIcon(R.drawable.ic_notification)
//                .setColor(ContextCompat.getColor(context, R.color.common_style_primary))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL);
    }
}

