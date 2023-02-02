package com.killki.app_killki.dialogos;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import com.killki.app_killki.AdminSQLiteOpenHelper;
import com.killki.app_killki.R;

public class DialogoSensorActivar extends DialogFragment {

    private RadioButton activo;
    private RadioButton no_activo;

    private Button btn_guardar;

    public DialogoSensorActivar() {

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        return crearDialogoConfSensor();
    }

    private AlertDialog crearDialogoConfSensor() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_dialogo_sensor_activar, null);
        builder.setView(v);

        activo = v.findViewById(R.id.activo);
        no_activo = v.findViewById(R.id.no_activo);

        btn_guardar = v.findViewById(R.id.btn_guardar);
        check();
        btn_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardar();
                dismiss();
            }
        });

        return builder.create();
    }

    public void guardar(){
        String valor = "";
        if (activo.isChecked()==true) {
            valor = "0";
        } else if (no_activo.isChecked()==true) {
            valor = "ENVIADA";
        }

        ContentValues registro = new ContentValues();
        registro.put("estatus", valor);
        AdminSQLiteOpenHelper con = new AdminSQLiteOpenHelper(this.getContext(), "killki", null, 1);
        SQLiteDatabase bd = con.getWritableDatabase();
        int cant = bd.update("configuracion", registro, null, null);
        bd.close();
        if (cant == 1)
            Toast.makeText(this.getContext(), "Configuración éxitosa", Toast.LENGTH_SHORT)
                    .show();

    }

    public void check(){
        AdminSQLiteOpenHelper con = new AdminSQLiteOpenHelper(this.getContext(), "killki", null, 1);
        SQLiteDatabase bd = con.getWritableDatabase();
        Cursor consulta = bd.rawQuery(
                "select estatus from configuracion", null);
        if (consulta.moveToFirst()) {
            String valor = consulta.getString(0);
            if (valor.equals("0")) {
                activo.setChecked(true);
            }else{
                no_activo.setChecked(true);
            }
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dialogo_sensor_activar, container, false);
    }
}