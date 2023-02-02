package com.killki.app_killki;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.telephony.SmsManager;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.killki.app_killki.dialogos.DialogoAyudaVoz;
import com.killki.app_killki.dialogos.DialogoInstrucciones;
import com.killki.app_killki.dialogos.DialogoMensaje;
import com.killki.app_killki.dialogos.DialogoVoz;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import modelos.Inicio;

public class SosActivity extends AppCompatActivity {

    public ImageView btn_sos;
    public ImageView btn_voz_alerta;
    public ImageView btn_whatsapp;

    private ArrayList<Inicio> lista;
    LocationManager locationManager ;
    boolean GpsStatus;
    Intent intent;
    Context context;

    SensorManager sensorManager;
    Sensor sensor;
    SensorEventListener sensorEventListener;
    int movimiento = 0;
    int numero = 0;
    int intervalo;
    String sms = "";
    String mensaje_voz = "";
    String fechaFormat;
    String getFecha = "";

    int flagVoz = 0;
    boolean alertaMoviento = false;
    private static final int RECOGNIZE_SPEECH_ACTIVITY = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos);

        lista=new ArrayList<Inicio>();
        lista.add(new Inicio("Empieza creando tus <font color='#EE0000'>ALERTAS</font> con mensajes de texto <font color='#EE0000'>AQUI</font>."));
        lista.add(new Inicio("Puedes enviar tus alertas de emergencia hasta a 3 contactos. Selecciona tus Contactos <font color='#EE0000'>AQUI</font>."));
        //lista.add(new Inicio("Para activar tus alertas y éstas se envíen selecciona <font color='#EE0000'>ACTIVAR ALERTA CON MI VOZ</font>."));
        //lista.add(new Inicio("También puedes escoger <font color='#EE0000'>ACTIVAR ALERTA MOVIENDO MI CELULAR</font>."));
        lista.add(new Inicio("Por último, puedes ingresar a KILLKI y darle click al botón <font color='#EE0000'>SOS</font> de color rojo y se enviarán tus alertas a los contactos seleccionados. "));

        AdminSQLiteOpenHelper con = new AdminSQLiteOpenHelper(getApplicationContext(), "killki", null, 1);
        SQLiteDatabase bd = con.getWritableDatabase();
        Cursor consulta = bd.rawQuery(
                "select num_veces from configuracion", null);
        if (consulta.moveToFirst()) {
            numero = consulta.getInt(0);

        }

        numero = 3;
        Cursor mensaje = bd.rawQuery("select mensaje, mensaje_voz from configuracion", null);
        if (mensaje.moveToFirst()) {
            sms = mensaje.getString(0);
            mensaje_voz = mensaje.getString(1);
        }

        ContentValues registro = new ContentValues();
        Date date = new Date();
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        format.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
        fechaFormat = format.format(date);

        String sql = "select fecha from historial where fecha = " + fechaFormat + " and estatus = ACTIVO";

        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        if (sensor == null) {
            Toast.makeText(getApplicationContext(), "El dispositivo no cuenta con sensor de movimiento", Toast.LENGTH_LONG).show();
        }

        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {

                if (numero > 0) {
                    float x = sensorEvent.values[0];
                    if (x > 18 ) {
                        movimiento++;
                    }

                    if (movimiento == numero) {
                        movimiento = 0;

                        AdminSQLiteOpenHelper con = new AdminSQLiteOpenHelper(getApplicationContext(), "killki", null, 1);
                        SQLiteDatabase bd = con.getWritableDatabase();
                        Cursor historial = bd.rawQuery(
                                "select estatus from configuracion", null);
                        String estatus = "";
                        if (historial.moveToFirst()) {
                            estatus = historial.getString(0);

                        }

                        if (estatus.equals("0")){
                            alertaMoviento = true;
                            enviarAlerta(sms, alertaMoviento);

                            desbloquearSensor();


                        }else{
                            Toast.makeText(getApplicationContext(), "Alerta con movimiento ya ha sido enviada, espere un momento para volverla a enviar.", Toast.LENGTH_LONG ).show();
                            /*
                            AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                            builder.setMessage("¿Desea activar el envío de alerta con movimiento nuevamente?")
                                    .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            actualizarAlerta("0");
                                            enviarAlerta(sms);
                                        }
                                    })
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.dismiss();
                                        }
                                    });
                            builder.show();

                           */
                        }



                    }
                }

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
        //se comento para quitar el envio de alerta de movimiento
        start();

        AdaptadorInicio adaptador = new AdaptadorInicio(this);
        ListView lv1 = findViewById(R.id.lv_inicio);
        lv1.setAdapter(adaptador);

        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        SosActivity.Localizacion local = new SosActivity.Localizacion();
        local.setSosActivity(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED

        ) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.SEND_SMS}, 1000);

            return;
        }

        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (LocationListener) local);

        btn_sos = findViewById(R.id.btn_sos);
        btn_voz_alerta = findViewById(R.id.btn_voz_alerta);
        btn_whatsapp = findViewById(R.id.btn_whatsapp);


        btn_voz_alerta.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                flagVoz = 0;
                Toast.makeText(getApplicationContext(), "Escuchando!!!", Toast.LENGTH_LONG).show();
                hablar();
            }
        });

        btn_sos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertaMoviento = false;
                enviarAlerta(sms, alertaMoviento);
                Cursor consultaIntervalo = bd.rawQuery("select intervalo from configuracion", null);

                if (consultaIntervalo.moveToFirst()) {
                    intervalo = consultaIntervalo.getInt(0);
                }
                if (intervalo > 0){
                    intervalo = intervalo * 1000;
                    for (int i = 1; i<= 3; i++){
                        try{
                            Thread.sleep(intervalo);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        alertaMoviento = false;
                        enviarAlerta(sms, alertaMoviento);
                    }
                }
            }
        });


        btn_whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enviarAlertaWhatsapp(sms);
            }
        });


    }

    private void consultaEstatus() {

        DialogoInstrucciones dialogo = new DialogoInstrucciones();
        dialogo.show(getSupportFragmentManager(), "DialogoInstrucciones");
    }

    private void start(){
        sensorManager.registerListener(sensorEventListener,sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void stop(){
        sensorManager.unregisterListener(sensorEventListener);
    }

    public void mensajeSMS(String numero, String mensaje){
        try {
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(numero,null,mensaje,null,null);
            Toast.makeText(getApplicationContext(), "Mensaje Enviado.", Toast.LENGTH_LONG).show();
        }
        catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Mensaje no enviado, ha ocurrido un error.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    void reenviar(){
        AdminSQLiteOpenHelper con = new AdminSQLiteOpenHelper(this, "killki", null, 1);
        SQLiteDatabase bd = con.getWritableDatabase();
        Cursor intervalo = bd.rawQuery("select intervalo from configuracion", null);
        int interv = 0;
        if (intervalo.moveToFirst()) {
            interv = intervalo.getInt(0);
        }

        if (interv > 0){
            Cursor list = bd.rawQuery("select nombre, numero from contactos", null);
            while (list.moveToNext()) {
                mensajeSMS(list.getString(1), sms);
                mensajeWhatsapp(list.getString(1), sms);

            }
        }

    }

    public void enviarAlertaWhatsapp(String mensaje){
        AdminSQLiteOpenHelper con = new AdminSQLiteOpenHelper(this, "killki", null, 1);
        SQLiteDatabase bd = con.getWritableDatabase();

        Cursor count = bd.rawQuery("select count (*) as count from contactos", null);
        count.moveToFirst();
        int contador= count.getInt(0);
        count.close();

        if (contador >= 1) {

            Cursor elPrimero = bd.rawQuery("select nombre, numero from contactos limit 1", null);

            while (elPrimero.moveToNext()) {
                mensajeWhatsapp(elPrimero.getString(1), mensaje);

            }

        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("No has configurado tus contactos.\n ¿Deseas configurarlos ahora?")
                    .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent i = new Intent(getApplicationContext(), ContactoActivity.class );
                            startActivity(i);
                        }
                    })
                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
            builder.show();
        }
    }


    public void enviarAlerta(String mensaje, boolean moviento){
        AdminSQLiteOpenHelper con = new AdminSQLiteOpenHelper(this, "killki", null, 1);
        SQLiteDatabase bd = con.getWritableDatabase();

        Cursor count = bd.rawQuery("select count (*) as count from contactos", null);
        count.moveToFirst();
        int contador= count.getInt(0);
        count.close();

        if (contador >= 1) {

            Cursor list = bd.rawQuery("select nombre, numero from contactos", null);
            Cursor elPrimero = bd.rawQuery("select nombre, numero from contactos limit 1", null);
            Cursor intervalo = bd.rawQuery("select intervalo from configuracion", null);

            while (list.moveToNext()) {
                mensajeSMS(list.getString(1), mensaje);

            }

            if(alertaMoviento == true){
                while (elPrimero.moveToNext()) {
                    mensajeWhatsapp(elPrimero.getString(1), mensaje);

                }
                actualizarAlerta("ENVIADA");
            }


        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("No has configurado tus contactos.\n ¿Deseas configurarlos ahora?")
                    .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent i = new Intent(getApplicationContext(), ContactoActivity.class );
                            startActivity(i);
                        }
                    })
                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
            builder.show();
        }
    }

    private void mensajeWhatsapp(String numero, String message)
    {

        // Creating new intent
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        //intent.setPackage("com.whatsapp");
        System.out.println("---->" + numero);
        String uri = "whatsapp://send?phone=+" + numero.toString() + "&text=" + message.toString();
        intent.setData(Uri.parse(uri));

        startActivity(intent);

    }

    public void irNoticias(View view) {
        Intent i = new Intent(this, NoticiasActivity.class );
        startActivity(i);
    }

    public void irMapa(View view) {
        Intent i = new Intent(this, MapaActivity.class );
        startActivity(i);
    }

    public void irOng(View view) {
        Intent i = new Intent(this, OrganizacionActivity.class );
        startActivity(i);
    }

    public void irContacto(View view) {
        Intent i = new Intent(this, ContactoActivity.class );
        startActivity(i);
    }

    public void irConfiguracion(View view) {
        Intent i = new Intent(this, ConfiguracionActivity.class );
        startActivity(i);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("¿Desea salir de Killki?")
                    .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(Intent.ACTION_MAIN);
                            intent.addCategory(Intent.CATEGORY_HOME);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
            builder.show();
        }

        return super.onKeyDown(keyCode, event);
    }

    public void salir(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("¿Desea salir de Killki?")
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        builder.show();
    }

    public void guardarAlerta(){
        ContentValues registro = new ContentValues();
        Date date = new Date();
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        format.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
        fechaFormat = format.format(date);

        registro.put("fecha", fechaFormat);
        registro.put("hora", "");
        registro.put("estatus", "ENVIADA");

        AdminSQLiteOpenHelper con = new AdminSQLiteOpenHelper(this, "killki", null, 1);
        SQLiteDatabase bd = con.getWritableDatabase();
        bd.insert("organ", null, registro);

        bd.close();

    }

    public void actualizarAlerta(String estatus) {

        ContentValues registro = new ContentValues();
        registro.put("estatus", estatus);
        AdminSQLiteOpenHelper con = new AdminSQLiteOpenHelper(this, "killki", null, 1);
        SQLiteDatabase bd = con.getWritableDatabase();
        bd.update("configuracion", registro, null, null);
        bd.close();
    }


        class AdaptadorInicio extends ArrayAdapter<Inicio> {

        AppCompatActivity appCompatActivity;

        AdaptadorInicio(AppCompatActivity context) {
            super(context, R.layout.layout_inicio, lista);
            appCompatActivity = context;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = appCompatActivity.getLayoutInflater();
            View item = inflater.inflate(R.layout.layout_inicio, null);

            TextView textView1 = item.findViewById(R.id.item);
            //textView1.setText(lista.get(position).getItem());
            textView1.setText(Html.fromHtml(lista.get(position).getItem()));
            ImageButton icon= item.findViewById(R.id.imgbtn);

            if (position == 0){
                icon.setImageResource(R.drawable.mensaje_removebg);
            }
            else if (position == 1){
                icon.setImageResource(R.drawable.contacto_removebg);
            }
            /*else if (position == 2){
                icon.setImageResource(R.drawable.voz_removebg);
            }*/
            else{
                icon.setImageResource(R.drawable.ic_action_check);
            }

            textView1.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    //do something
                    //Toast.makeText(getApplicationContext(), "Enlace seleccionado numero: "+ (position + 1), Toast.LENGTH_LONG).show();

                    if (position == 0) {
                        DialogoMensaje dialogoMensaje = new DialogoMensaje();
                        dialogoMensaje.show(getSupportFragmentManager(), "DialogoMensaje");

                    }

                    if (position == 1) {
                        Intent i = new Intent(SosActivity.this, ContactoActivity.class );
                        startActivity(i);

                    }

                    if (position == 2) {
                        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                                && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                        ) {
                            ActivityCompat.requestPermissions(SosActivity.this, new String[]{
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.RECORD_AUDIO}, 1000);
                            return;
                        }else {
                            flagVoz = 1;
                            hablar();
                            /*
                            DialogoVoz dialogoVoz = new DialogoVoz();
                            dialogoVoz.show(getSupportFragmentManager(), "DialogoVoz");

                             */

                        }
                    }
                    /*
                    if (position == 2) {
                        DialogoSensor dialogoSensor = new DialogoSensor();
                        dialogoSensor.show(getSupportFragmentManager(), "DialogoSensor");

                    }*/
                }
            });


            icon.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    //do something

                    if (position == 0) {
                        DialogoMensaje dialogoMensaje = new DialogoMensaje();
                        dialogoMensaje.show(getSupportFragmentManager(), "DialogoMensaje");

                    }
                    if (position == 1) {
                        Intent i = new Intent(SosActivity.this, ContactoActivity.class );
                        startActivity(i);

                    }
                    if (position == 2) {
                        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                                && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                        ) {
                            ActivityCompat.requestPermissions(SosActivity.this, new String[]{
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.RECORD_AUDIO}, 1000);
                            return;
                        }else {
                            DialogoVoz dialogoVoz = new DialogoVoz();
                            dialogoVoz.show(getSupportFragmentManager(), "DialogoVoz");
                        }
                    }
                    /*
                    if (position == 3) {
                        DialogoSensor dialogoSensor = new DialogoSensor();
                        dialogoSensor.show(getSupportFragmentManager(), "DialogoSensor");

                    }
                     */

                }
            });

            return(item);
        }
    }




    public void setLocation(Location location){
        if (location.getLatitude() != 0.0 && location.getLongitude() != 0.0) {
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(
                        location.getLatitude(), location.getLongitude(), 1);
                if(!list.isEmpty()){
                    Address direccion = list.get(0);
                    //txtv_direccion.setText(direccion.getAddressLine(0));
                }

            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RECOGNIZE_SPEECH_ACTIVITY:
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> speech = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String strSpeech2Text = speech.get(0);
                    //grabar.setText(strSpeech2Text);
                    if (flagVoz == 0){
                        //enviando alerta de voz
                        alertaVoz(strSpeech2Text);
                    }else{
                        //gonfiguracion de voz
                        mostrarDialogVoz(strSpeech2Text);
                    }

                }
                break;
            default:
                break;
        }
    }

    private void alertaVoz(String mensaje) {
        AdminSQLiteOpenHelper con = new AdminSQLiteOpenHelper(getApplicationContext(), "killki", null, 1);
        SQLiteDatabase bd = con.getWritableDatabase();
        Cursor mensajeVoz = bd.rawQuery("select mensaje, mensaje_voz from configuracion", null);
        if (mensajeVoz.moveToFirst()) {
            mensaje_voz = mensajeVoz.getString(1).toUpperCase(Locale.ROOT);
        }

        if (!mensaje_voz.isEmpty() && !mensaje.isEmpty()){
            if(mensaje.toUpperCase(Locale.ROOT).equals(mensaje_voz)){
                alertaMoviento = false;
                enviarAlerta(sms, alertaMoviento);
            }else{
                Toast.makeText(getApplicationContext(), "No hay coincidencia", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(getApplicationContext(), "Debes configurar tu mensaje de alerta de voz.", Toast.LENGTH_SHORT).show();
        }
    }

    private void mostrarDialogVoz(String mensaje) {
        SharedPreferences preferencias=getSharedPreferences("killki",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putString("mensaje", mensaje);
        editor.commit();
        DialogoAyudaVoz dialogoVoz = new DialogoAyudaVoz();
        dialogoVoz.show(getSupportFragmentManager(), "DialogoAyudaVoz");

    }

    public void hablar(){
        Intent intentActionRecognizeSpeech = new Intent(
                RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        // Configura el Lenguaje (Español-México)
        intentActionRecognizeSpeech.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL, "es-PE");
        try {
            startActivityForResult(intentActionRecognizeSpeech,
                    RECOGNIZE_SPEECH_ACTIVITY);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "Tú dispositivo no soporta el reconocimiento por voz",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public class Localizacion implements LocationListener {
        SosActivity sosActivity;

        public SosActivity getSosActivity() {return sosActivity;}
        public void setSosActivity(SosActivity sosActivity){
            this.sosActivity = sosActivity;

        }
        @Override
        public void onLocationChanged(@NonNull Location location) {
            location.getAltitude();
            location.getLatitude();
            String mensaje = "coordenadas" + location.getLatitude() + " - " + location.getLongitude();

            //txtv_direccion.setText(mensaje);
            this.sosActivity.setLocation(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es desactivado

            AlertDialog.Builder builder = new AlertDialog.Builder(SosActivity.this);
            builder.setMessage("El GPS del dispositivo esta desactivado, actívalo para seguir usando la aplicación.")
                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
            builder.show();

        }

        @Override
        public void onProviderEnabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es activado
            //txtv_direccion.setText("GPS Activado");
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.AVAILABLE:
                    Log.d("debug", "LocationProvider.AVAILABLE");
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    Log.d("debug", "LocationProvider.OUT_OF_SERVICE");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE");
                    break;
            }
        }
    }

    public boolean verificarMovimiento(){
        boolean valor = false;
        return valor;
    }

    public void desbloquearSensor(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3600000);
                    //Thread.sleep(6000);
                    actualizarAlerta("0");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void reiniciarMovimiento(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                    movimiento = 0;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}