package com.example.geofence;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import java.util.Random;

/** CLASE NOTIFICATION HELPER
 *  Esta es una clase auxiliar la cual nos permite crear y mandar notificaciones
 */
public class NotificationHelper extends ContextWrapper {

    /** Metodo Constructor
     * Este constructor es el constructor por default, este verifica la version instalada en el
     * Dispositivo, y dependiendo de la version creará o no los canales
     */
    public NotificationHelper(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannels();
        }
    }

    /** Crea el nombre y el id del canal */
    private String CHANNEL_NAME = "High priority channel";
    private String CHANNEL_ID = "com.example.notifications" + CHANNEL_NAME;

    /** METODO CRATECHANELS
     *  Metodo que ayuda a crear una serie de configuracione para las notificaciones, desde su
     *  Importancia hasta el color de luz, si vibrará o no etc.
     *  Este metodo solo se ejecutará si la version del api es superior a la 26 (Android 8)
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannels() {
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.enableLights(true);
        notificationChannel.enableVibration(true);
        notificationChannel.setDescription("this is the description of the channel.");
        notificationChannel.setLightColor(Color.RED);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(notificationChannel);
    }

    /** METODO SENDHIGHPRIORITYNOTIFICATION
     *  Este metodo funciona para mandar una notiicacion al usuario con un nivel de prioridad alta.
     */
    public void sendHighPriorityNotification(String title, String body, Class activityName) {

        Intent intent = new Intent(this, activityName);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 267, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
//                .setContentTitle(title)
//                .setContentText(body)
                .setSmallIcon(R.drawable.ic_notifications_active_black_24dp)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setStyle(new NotificationCompat.BigTextStyle().setSummaryText("summary").setBigContentTitle(title).bigText(body))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();

        NotificationManagerCompat.from(this).notify(new Random().nextInt(), notification);
    }


}
