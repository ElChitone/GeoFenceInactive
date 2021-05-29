package com.example.geofence;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

public class CloseSession extends AppCompatActivity {
    //Variable que detectará el inicio de la interacción del usuario
    long startTime;
    //Variable que detectará la ultima interaccion del usuario
    long lastInteractionTime;
    //Variable que almacenará si la pantalla está apagada o encendida
    boolean isScreenOff;
    //Variable que detectará si la aplicación está en segundo plano
    boolean isForeGround;
    //Variable para interactuar con las API'S de GeoFencing
    GeofencingClient geofencingClient;
    //Variable de la clase GeoFenceHelper que nos ayudará a crear todas las GeoFence (GeoVallas)
    GeofenceHelper geofenceHelper;
    //Variable que almacenará la latitud y longitud de las sucursales
    LatLng ubi;
    //Variable constante que contiene el código de peticion de permisos para el permiso
    // FINE_LOCATION_ACCESS el cual nos permitirá obtener la ubicacion del usuario
    private final int FINE_LOCATION_ACCESS_REQUEST_CODE = 10001;
    //Variable que almacenará el radio requerido de la geo valla

    NotificationHelper notificationHelper;
    //Constructor Vacio de la clase
    public CloseSession(){
        //Inicia la variable que detecta la interraccion del usuario apenas inice la actividad
        startTime = System.currentTimeMillis();
    }
    /*  MÉTODO START
    *   Este metodo inicia el hilo, y el screen reciever, aparte inicializa al manejador de las
    *   geovallas y a al cliente de estas, ejecuta el metodo para activar la ubicacion del usuario
    *   tambien inicializa el manejador de notificaciones
    */
    protected void start(){
        new ScreenReceiver();
        runThread();
        geofencingClient = LocationServices.getGeofencingClient(this);
        geofenceHelper = new GeofenceHelper(this);
        enableUserLocation();
        notificationHelper = new NotificationHelper(CloseSession.this);
    }

    /* METODO SENDHIGHPRIORITYNOTIFICATION
    *  Metodo que manda a pantalla una notificación por medio del manejador de notificaciones
    * */
    public void sendHighPriorityNotification(String title, String body) {
        notificationHelper.sendHighPriorityNotification(title,body,CloseSession.class);
    }

    /* METODO ONUSERINTERACTION
    *  Método que detecta la interaccion del usuario y almacena el timepo de
    *  Su ultima interacción en la variable definida para esto
    * */
        @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        startTime = System.currentTimeMillis();
    }

    /* METODO ONPAUSE
    *  Este metodo sobre escribe el metodo normal de onPause(), primero manda a llamar a el metodo
    *  onPause() del padre, y cambia el valor de la variable que usamos como bandera a true
    *  avisando que la aplicacion está en segundo plano
    * */
    @Override
    protected void onPause() {
        super.onPause();
        isForeGround = true;
    }

    /* METODO RUNTHREAD
    *  Explicacion general: Metodo que permite ver si el usuario está inactivo o no
    * */
    private void runThread() {
        //Se crea un nuevo hilo
        new Thread() {
            //Se escribe su método run, necesario para que el hilo funcione
            public void run() {
                while(true){
                    try {
                        //El Hilo tendrá que correr en el hilo de la interfaz de usario para poder
                        //Mandar notificaciones
                        runOnUiThread(() -> {
                            //Se manda a llamar al metodo setLastInteractiontime
                            setLastInteractionTime();
                            //Se comprueba si la pantalla está apagada o si el tiempo sin
                            //interaccion del usuario es mayor a 15 segundos (15*1000) o si la
                            //Aplicacion esta en segundo plano
                            if(isScreenOff || getLastInteractionTime() > 15000 || isForeGround){
                                //...... means USER has been INACTIVE over a period of
                                // and you do your stuff like log the user out

                                finish();
                            }
                        });
                        Thread.sleep(20000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    public long getLastInteractionTime() {
        return lastInteractionTime;
    }
    public void setLastInteractionTime() {
        lastInteractionTime = System.currentTimeMillis() - startTime;
    }

    private void enableUserLocation(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED ){
            return;
        }else{

            //Ask for permission
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                //We need to show user a dialog for displaying what the permission is needed and then
                //Ask for the permission..
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                                    Manifest.permission.INTERNET,
                                    Manifest.permission.WAKE_LOCK,Manifest.permission.ACCESS_COARSE_LOCATION},
                            FINE_LOCATION_ACCESS_REQUEST_CODE );
                }else{
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            FINE_LOCATION_ACCESS_REQUEST_CODE );
                }
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        FINE_LOCATION_ACCESS_REQUEST_CODE );
            }
        }
    }

  protected void addGeofence(LatLng latLng, float radius,String id){
      ubi = new LatLng(25.5608, -103.4328);
      String GEOFENCE_ID = id;
        Geofence geofence = geofenceHelper.getGeofence(GEOFENCE_ID,latLng,radius,
                Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT);
        GeofencingRequest geofencingRequest = geofenceHelper.getGeofencingRequest(geofence);
        PendingIntent pendingIntent = geofenceHelper.getPendingIntent();
        geofencingClient.addGeofences(geofencingRequest,pendingIntent)
                .addOnSuccessListener(unused -> {
                    System.out.println("onSuccess: GEOFENCE Added...");
                    Toast.makeText(CloseSession.this, "onSuccess: GEOFENCE Added...", Toast.LENGTH_SHORT).show();

                })
                .addOnFailureListener(e -> {
                    String errorMessage = geofenceHelper.getErrorString(e);
                    Toast.makeText(CloseSession.this, "onFailure: " +errorMessage, Toast.LENGTH_SHORT).show();

                });

    }

    private class ScreenReceiver extends BroadcastReceiver {
        protected ScreenReceiver() { // register receiver that handles screen on and screen off logic
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SCREEN_ON);
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            registerReceiver(this, filter);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                isScreenOff = true;
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                isScreenOff = false;
            }
        }
    }


}
