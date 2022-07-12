package com.example.app_killki;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class AdminSQLiteOpenHelper  extends SQLiteOpenHelper {
    public AdminSQLiteOpenHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table contactos(nombre text, numero text)");
        db.execSQL("create table configuracion(mensaje text, intervalo integer, sensor integer, num_veces integer, mensaje_voz integer)");
        db.execSQL("create table organizacion(nombre text, email text, direccion text, telefono text, logo text, img text)");

        db.execSQL("INSERT INTO configuracion (mensaje, intervalo, sensor, num_veces, mensaje_voz) VALUES ('¡NECESITO AYUDA!', 0, 0, 0, 0)");

        db.execSQL("INSERT INTO organizacion (nombre, email, direccion, telefono, logo) VALUES ('DEMUS', 'demus@demus.org.pe', 'Jirón Caracas 2624 Jesús María, Lima - Perú', '4638515 / 4631226 / 4600879', 'ong_demus')");
        db.execSQL("INSERT INTO organizacion (nombre, email, direccion, telefono, logo) VALUES ('CENTRO DE LA MUJER PERUANA FLORA TRISTAN', 'postmast@flora.org.pe', 'Parque Hernán Velarde No 42, Lima - Perú', '433 1457', 'ong_flora')");
        db.execSQL("INSERT INTO organizacion (nombre, email, direccion, telefono, logo) VALUES ('MOVIMIENTO MANUELA RAMOS', '', 'Pueblo Libre, Lima - Perú', '4238840', 'ong_manuela')");
        db.execSQL("INSERT INTO organizacion (nombre, email, direccion, telefono, logo) VALUES ('PROMSEX', 'www.promsex.org', '', '', 'ong_promsex')");

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

