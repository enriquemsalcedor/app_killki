package com.example.app_killki.dialogos;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
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
import android.widget.EditText;
import android.os.Bundle;

import android.widget.Toast;

import com.example.app_killki.AdminSQLiteOpenHelper;
import com.example.app_killki.ConfiguracionActivity;
import com.example.app_killki.R;


public class DialogoMensaje extends DialogFragment {


    public EditText txt_mensaje;
    public Button btn_guardar;


    public DialogoMensaje() {
        // Required empty public constructor
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        return crearDialogoConfMensaje();
    }

    private AlertDialog crearDialogoConfMensaje() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_dialogo_mensaje, null);
        builder.setView(v);

        btn_guardar = (Button) v.findViewById(R.id.btn_guardar);
        txt_mensaje = (EditText) v.findViewById(R.id.txt_mensaje);

        AdminSQLiteOpenHelper con = new AdminSQLiteOpenHelper(this.getContext(), "killki", null, 1);
        SQLiteDatabase bd = con.getWritableDatabase();
        Cursor consulta = bd.rawQuery(
                "select mensaje from configuracion", null);
        if (consulta.moveToFirst()) {
            txt_mensaje.setText(consulta.getString(0));
        }

        btn_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modificar();
                dismiss();
            }
        });

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dialogo_mensaje, container, false);
    }

    public void modificar(){
        String mensajeNuevo = txt_mensaje.getText().toString();
        ContentValues registro = new ContentValues();
        registro.put("mensaje", mensajeNuevo);
        AdminSQLiteOpenHelper con = new AdminSQLiteOpenHelper(this.getContext(), "killki", null, 1);
        SQLiteDatabase bd = con.getWritableDatabase();
        int cant = bd.update("configuracion", registro, null, null);
        bd.close();
        if (cant == 1)
            Toast.makeText(this.getContext(), "Mensaje modificado con éxito", Toast.LENGTH_SHORT)
                    .show();
    }

}