package com.killki.app_killki;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
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
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.killki.app_killki.dialogos.DialogoOrganizacion;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import modelos.Organizacion;

public class OrganizacionActivity extends AppCompatActivity {

    public ImageView btn_sos;
    public ImageView btn_voz_alerta;
    public ImageView btn_whatsapp;

    TextView txtv_direccion;
    ImageView btn_add;
    Intent intent;
    Context context;

    SensorManager sensorManager;
    Sensor sensor;
    SensorEventListener sensorEventListener;
    int movimiento = 0;
    int intervalo;
    int numero = 0;
    String sms = "";
    String mensaje_voz = "";

    private static final int RECOGNIZE_SPEECH_ACTIVITY = 1;

    private ArrayList<Organizacion> listOrganizacion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizacion);

        AdminSQLiteOpenHelper con = new AdminSQLiteOpenHelper(getApplicationContext(), "killki", null, 1);
        SQLiteDatabase bd = con.getWritableDatabase();
        Cursor consulta = bd.rawQuery(
                "select num_veces from configuracion", null);
        if (consulta.moveToFirst()) {
            numero = consulta.getInt(0);

        }

        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        if (sensor == null) {
            Toast.makeText(getApplicationContext(), "El dispositivo no cuenta con sensor de movimiento", Toast.LENGTH_LONG).show();
        }
        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                if (numero > 0){
                    float x = sensorEvent.values[0];
                    if(x<-5){
                        movimiento++;
                        //Toast.makeText(getApplicationContext(), "uno..." + movimiento, Toast.LENGTH_LONG).show();
                    }

                    if(movimiento == numero){
                        movimiento = 0;
                        enviarAlerta(sms);
                    }
                }

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
        //se comento para quitar el envio de alerta de movimiento
        //start();

        txtv_direccion =  (TextView) findViewById(R.id.txtv_direccion);
        btn_add = (ImageView) findViewById(R.id.btn_add);

        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Localizacion local = new Localizacion();
        local.setOrganizacionActivity(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
            return;
        }

        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (LocationListener) local);
        txtv_direccion.setText("");
        txtv_direccion.setText("");

        listar();
        AdaptadorOrganizacion adaptador = new AdaptadorOrganizacion(this);
        ListView lv_orgazacion = findViewById(R.id.lv_orgazacion);
        lv_orgazacion.setAdapter(adaptador);


        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DialogoOrganizacion dialogoOrganizacion = new DialogoOrganizacion();
                dialogoOrganizacion.show(getSupportFragmentManager(), "Organizacion");
            }
        });

        btn_sos = findViewById(R.id.btn_sos);

        btn_voz_alerta = findViewById(R.id.btn_voz_alerta);

        btn_whatsapp = findViewById(R.id.btn_whatsapp);

        btn_voz_alerta.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Escuchando!!!", Toast.LENGTH_LONG).show();
                hablar();
            }
        });

        btn_sos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                enviarAlerta(sms);
                /*
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
                        enviarAlerta(sms);
                    }
                }
                */
            }
        });

        btn_whatsapp .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enviarAlertaWhatsapp(sms);
            }
        });
    }

    private void start(){
        sensorManager.registerListener(sensorEventListener,sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void stop(){
        sensorManager.unregisterListener(sensorEventListener);
    }

    public void setLocation(Location location){
        if (location.getLatitude() != 0.0 && location.getLongitude() != 0.0) {
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(
                        location.getLatitude(), location.getLongitude(), 1);
                if(!list.isEmpty()){
                    Address direccion = list.get(0);
                    txtv_direccion.setText(direccion.getAddressLine(0));
                }

            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public class Localizacion implements LocationListener{
        OrganizacionActivity organizacionActivity;

        public OrganizacionActivity getOrganizacionActivity() {return organizacionActivity;}
        public void setOrganizacionActivity(OrganizacionActivity organizacionActivity){
            this.organizacionActivity = organizacionActivity;

        }
        @Override
        public void onLocationChanged(@NonNull Location location) {
            location.getAltitude();
            location.getLatitude();
            String mensaje = "coordenadas" + location.getLatitude() + " - " + location.getLongitude();

            this.organizacionActivity.setLocation(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es desactivado
            AlertDialog.Builder builder = new AlertDialog.Builder(OrganizacionActivity.this);
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
            txtv_direccion.setText("Buscando ubicación...");
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

    public void irNoticias(View view) {
        Intent i = new Intent(this, NoticiasActivity.class );
        startActivity(i);
    }
    public void irContacto(View view) {
        Intent i = new Intent(this, ContactoActivity.class );
        startActivity(i);
    }
    public void irMapa(View view) {
        Intent i = new Intent(this, MapaActivity.class );
        startActivity(i);
    }
    public void irConfiguracion(View view) {
        Intent i = new Intent(this, ConfiguracionActivity.class );
        startActivity(i);
    }
    public void irInicio(View view) {
        Intent i = new Intent(this, SosActivity.class );
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

    public void listar(){

        AdminSQLiteOpenHelper con = new AdminSQLiteOpenHelper(this, "killki", null, 1);
        SQLiteDatabase bd = con.getWritableDatabase();

        Organizacion organizacion = null;
        listOrganizacion = new ArrayList<Organizacion>();
        Cursor list = bd.rawQuery("select nombre, email, direccion, telefono, logo from organizacion", null);

        while (list.moveToNext()){
            organizacion = new Organizacion();
            organizacion.setNombre(list.getString(0));
            organizacion.setEmail(list.getString(1));
            organizacion.setDireccion(list.getString(2));
            organizacion.setTelefonos(list.getString(3));
            organizacion.setLogo(list.getString(4));
            listOrganizacion.add(organizacion);

        }

    }



    class AdaptadorOrganizacion extends ArrayAdapter<Organizacion> {

        AppCompatActivity appCompatActivity;

        AdaptadorOrganizacion(AppCompatActivity context) {
            super(context, R.layout.layout_organizacion, listOrganizacion);
            appCompatActivity = context;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = appCompatActivity.getLayoutInflater();
            View item = inflater.inflate(R.layout.layout_organizacion, null);

            TextView nombre = item.findViewById(R.id.nombre);
            nombre.setText(listOrganizacion.get(position).getNombre());

            TextView email = item.findViewById(R.id.email);
            email.setText(listOrganizacion.get(position).getEmail());

            TextView telefono = item.findViewById(R.id.telefono);
            telefono.setText(listOrganizacion.get(position).getTelefonos());

            TextView direccion = item.findViewById(R.id.direccion);
            direccion.setText(listOrganizacion.get(position).getDireccion());

            ImageView logo = item.findViewById(R.id.logoOng);
            String name = listOrganizacion.get(position).getLogo();
            if (name != null) {
                int id = getResources().getIdentifier(name, "drawable", getPackageName());
                Drawable drawable = getResources().getDrawable(id);
                logo.setBackground(drawable);
            }else{
                String nameaux = "logo";
                int id = getResources().getIdentifier(nameaux, "drawable", getPackageName());
                Drawable drawable = getResources().getDrawable(id);
                logo.setBackground(drawable);
            }

            //logo.setImageResource(R.drawable.ong_demus);

            return(item);
        }
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

    public void mensajeSMS(String numero, String mensaje){
        try {
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(numero,null,mensaje,null,null);
            Toast.makeText(getApplicationContext(), "Mensaje Enviado.", Toast.LENGTH_LONG).show();
        }
        catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Mensaje no enviado, datos incorrectos.", Toast.LENGTH_LONG).show();
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

    public void enviarAlerta(String mensaje){
        AdminSQLiteOpenHelper con = new AdminSQLiteOpenHelper(this, "killki", null, 1);
        SQLiteDatabase bd = con.getWritableDatabase();

        Cursor count = bd.rawQuery("select count (*) as count from contactos", null);
        count.moveToFirst();
        int contador= count.getInt(0);
        count.close();

        if (contador >= 1) {

            Cursor list = bd.rawQuery("select nombre, numero from contactos", null);
            Cursor intervalo = bd.rawQuery("select intervalo from configuracion", null);

            while (list.moveToNext()) {
                mensajeSMS(list.getString(1), mensaje);

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
        String uri = "whatsapp://send?phone=" + numero.toString() + "&text=" + message.toString();
        intent.setData(Uri.parse(uri));

        startActivity(intent);
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

                    alertaVoz(strSpeech2Text);


                }
                break;
            default:
                break;
        }
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


    private void alertaVoz(String mensaje) {
        AdminSQLiteOpenHelper con = new AdminSQLiteOpenHelper(getApplicationContext(), "killki", null, 1);
        SQLiteDatabase bd = con.getWritableDatabase();
        Cursor mensajeVoz = bd.rawQuery("select mensaje, mensaje_voz from configuracion", null);
        if (mensajeVoz.moveToFirst()) {
            mensaje_voz = mensajeVoz.getString(1).toUpperCase(Locale.ROOT);
        }

        if (!mensaje_voz.isEmpty() && !mensaje.isEmpty()){
            if(mensaje.toUpperCase(Locale.ROOT).equals(mensaje_voz)){
                enviarAlerta(sms);
            }else{
                Toast.makeText(getApplicationContext(), "No hay coincidencia", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(getApplicationContext(), "Debes configurar tu mensaje de alerta de voz.", Toast.LENGTH_SHORT).show();
        }
    }
}