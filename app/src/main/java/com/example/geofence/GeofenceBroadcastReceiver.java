package com.example.geofence;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import java.util.List;

/** Clase GEOFENCEBROADCASTRECEIVER
 *  Este BroadCastReceiver sirve para detectar los eventos del geofence
 *  DWEL: Transitar dentro de la geovalla
 *  ENTER: Cuando la geovalla se activa, o cuando el usuario entra a esta geovalla
 *  EXIT: Cuando la geovalla se desactiva o cuando el usuario sale de la geovalla
 */
public class GeofenceBroadcastReceiver extends BroadcastReceiver {


    /** METODO ONRECIEVE
     *  Este metodo Es es el metodo que maneja los eventos de la geofence
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        /**Esta variable se utiliza como el manejador de las notificaciones */
        NotificationHelper notificationHelper = new NotificationHelper(context);
        /**Esta variable es la que permite leer los eventos de las geofences */
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        /**Condicional para saber si el evento tuvo errores o no. */
        if ( geofencingEvent.hasError()){
            Log.d("Recieve", "onReceive: Error Receiving geofence event...");
            return;
        }
        /**Esta es la variable que almanecerá todas las feovallas que hayan activado los eventos */
        List<Geofence> geofenceList = geofencingEvent.getTriggeringGeofences();
        for (Geofence geofence: geofenceList) {
            Log.d("Recieve", "onReceive: "+   geofence.getRequestId());
        }
        /** Obtener Que tipo de evento sucedio. */
        int transitionType = geofencingEvent.getGeofenceTransition();
        /**Switch que manejará que tipo de transicion o evento se llevo a cabo */
        switch (transitionType){
            /** Caso Enter
             *  Si el usuario entra en la geovalla se le notifica que esta dentro de la geovalla
             *  Y le permitirá acceso a la app
             *  Caso DWELL
             *  No tiene porque haber alguna accion en este evento pero por el momento se le notificará
             *  Caso Exit
             *  Se le notificará al usuario que salio de la geovalla, le cerrará la sesion
             *  Y finalizará la actividad.
             * */
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                Toast.makeText(context, "GEOFENCE_TRANSITION_ENTER", Toast.LENGTH_SHORT).show();
                notificationHelper.sendHighPriorityNotification("GEOFENCE_TRANSITION_ENTER","", MapsActivity.class);
                break;
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                Toast.makeText(context, "GEOFENCE_TRANSITION_DWELL", Toast.LENGTH_SHORT).show();
                notificationHelper.sendHighPriorityNotification("GEOFENCE_TRANSITION_DWELL","", MapsActivity.class);
                break;
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                Toast.makeText(context, "GEOFENCE_TRANSITION_EXIT", Toast.LENGTH_SHORT).show();
                notificationHelper.sendHighPriorityNotification("GEOFENCE_TRANSITION_EXIT","", MapsActivity.class);
                Intent i = new Intent(context, MapsActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                i.putExtra("Close", true); // an extra which says to finish the activity.
                context.startActivity(i);
                break;
        }
    }
}