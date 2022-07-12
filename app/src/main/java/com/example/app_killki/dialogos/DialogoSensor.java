package com.example.app_killki.dialogos;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.app_killki.AdminSQLiteOpenHelper;
import com.example.app_killki.R;


public class DialogoSensor extends DialogFragment {


    private Button btn_guardar;

    private RadioButton rb_no_select;
    private RadioButton rb_dos;
    private RadioButton rb_tres;
    private RadioButton rb_cinco;

    public DialogoSensor() {
        // Required empty public constructor
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        return crearDialogoConfSensor();
    }

    private AlertDialog crearDialogoConfSensor() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_dialogo_sensor, null);
        builder.setView(v);

        rb_no_select = v.findViewById(R.id.rb_no_select);
        rb_dos = v.findViewById(R.id.rb_dos);
        rb_tres = v.findViewById(R.id.rb_tres);
        rb_cinco = v.findViewById(R.id.rb_cinco);

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
        int numero;
        if (rb_dos.isChecked()==true) {
            numero = 2;
        } else if (rb_tres.isChecked()==true) {
            numero = 3;
        } else if (rb_cinco.isChecked()==true) {
            numero = 5;
        } else{
            numero = 0;
        }

        ContentValues registro = new ContentValues();
        registro.put("num_veces", numero);
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
                "select num_veces from configuracion", null);
        if (consulta.moveToFirst()) {
            int numero = consulta.getInt(0);
            if (numero == 2) {
                rb_dos.setChecked(true);
            } else if (numero == 3) {
                rb_tres.setChecked(true);
            } else if (numero == 5) {
                rb_cinco.setChecked(true);
            }else{
                rb_no_select.setChecked(true);
            }
        }
    }



        @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dialogo_sensor, container, false);
    }

}