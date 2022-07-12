package com.example.app_killki.utilidades;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.telephony.SmsManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app_killki.AdminSQLiteOpenHelper;

public class EnvioMensajeTexto extends AppCompatActivity {

    public void mensajeSMS(String numero, String mensaje){
        try {
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(numero,null,mensaje,null,null);
            Toast.makeText(this, "Mensaje Enviado.", Toast.LENGTH_LONG).show();
        }
        catch (Exception e) {
            Toast.makeText(this, "Mensaje no enviado, ha ocurrido un error.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }

}
