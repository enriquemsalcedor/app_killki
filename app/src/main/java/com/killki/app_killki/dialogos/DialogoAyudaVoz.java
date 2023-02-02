package com.killki.app_killki.dialogos;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.killki.app_killki.AdminSQLiteOpenHelper;
import com.killki.app_killki.R;


public class DialogoAyudaVoz extends DialogFragment {

    private static final int RECOGNIZE_SPEECH_ACTIVITY = 1;

    private TextView mensaje;
    private Button guardar;

    public DialogoAyudaVoz() {
        // Required empty public constructor
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return crearDialogoConfVoz();
    }

    private AlertDialog crearDialogoConfVoz() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_dialogo_ayuda_voz, null);
        builder.setView(v);
        mensaje = (TextView) v.findViewById(R.id.mensaje);
        guardar = (Button) v.findViewById(R.id.guardar);

        SharedPreferences prefe = getContext().getSharedPreferences("killki", Context.MODE_PRIVATE);
        mensaje.setText(prefe.getString("mensaje",""));


        guardar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
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
        return inflater.inflate(R.layout.fragment_dialogo_ayuda_voz, container, false);
    }

    public void modificar(){
        String mensaje_voz = mensaje.getText().toString();
        ContentValues registro = new ContentValues();
        registro.put("mensaje_voz", mensaje_voz);
        AdminSQLiteOpenHelper con = new AdminSQLiteOpenHelper(this.getContext(), "killki", null, 1);
        SQLiteDatabase bd = con.getWritableDatabase();
        int cant = bd.update("configuracion", registro, null, null);
        bd.close();
        if (cant == 1)
            Toast.makeText(this.getContext(), "Mensaje guardado con éxito", Toast.LENGTH_SHORT)
                    .show();
    }


}