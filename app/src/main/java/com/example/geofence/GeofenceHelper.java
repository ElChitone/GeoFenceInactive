package com.example.geofence;

import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.maps.model.LatLng;


/** CLASE GEOFENCEHELPER
 *  Esta clase sirve como un auxiliar para otorgar la funcionalidad de las geovallas
 */
public class GeofenceHelper extends ContextWrapper {
    //Variable de clase que almacena los pending intent de las geovallas
    PendingIntent pendingIntent;

    //Constructor de la clase por defecto
    public GeofenceHelper(Context base) {
        super(base);
    }

    /** METODO GETGEOFENCINGREQUEST
     *  Este metodo sirve para obtener el geofencingrequest, esta es una lista de geovallas
     *  que serán monitoriadas y como las notificaciones serán reportados,
     *  el initial trigger es para saber que ese será la que active la geovalla, en este caso será
     *  cuando entran a las geovallas.
     */
    public GeofencingRequest getGeofencingRequest(Geofence geofence){
        return new GeofencingRequest.Builder()
                .addGeofence(geofence)
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .build();
    }

    /** METODO GETGEOFENCE
     *  Este metodo crea una geovalla con un id, la latidud y longitud, su radio y los
     *  tipos de transision.
     */
    public Geofence getGeofence(String ID, LatLng latLng, float radius, int transitiontrypes){
        return new Geofence.Builder().setCircularRegion(latLng.latitude,latLng.longitude,radius)
                .setRequestId(ID)
                .setTransitionTypes(transitiontrypes)
                .setLoiteringDelay(5000)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build();
    }

    /** METODO GETPENDING INTEND
     *  Este método nos otorgará el pending intent, si es que existe, si no existe creará uno y regresará ese
     */

    public PendingIntent getPendingIntent(){
        if(pendingIntent != null){
            return pendingIntent;
        }
        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 2607, intent,
                                                    PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

    /** METODO GETERRORSTRING
     *  Este metodo recibe una excepcion y regresa el error que causó la excepcion en formato de cadena
     * */
    public String getErrorString(Exception e){
        if (e instanceof ApiException){
            ApiException apiException = (ApiException) e;

            switch (apiException.getStatusCode()){
                case GeofenceStatusCodes
                     .GEOFENCE_NOT_AVAILABLE:

                    return "GEOFENCE_NOT_AVAILABLE";
                case GeofenceStatusCodes
                        .GEOFENCE_TOO_MANY_GEOFENCES:
                    return "GEOFENCE_TOO_MANY_GEOFENCES";
                case GeofenceStatusCodes
                        .GEOFENCE_TOO_MANY_PENDING_INTENTS:
                    return "GEOFENCE_TOO_MANY_PENDING_INTENTS";
            }
        }

        return e.getLocalizedMessage();
    }

}
