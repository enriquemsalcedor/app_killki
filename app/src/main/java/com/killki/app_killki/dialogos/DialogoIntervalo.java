package com.killki.app_killki.dialogos;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
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


public class DialogoIntervalo extends DialogFragment {

    private Button btn_guardar;
    private RadioButton rb_no_select;
    private RadioButton rb_treinta_seg;
    private RadioButton rb_un_min;
    private RadioButton rb_dos_min;
    private RadioButton rb_cinco_min;
    private RadioButton rb_diez_min;

    public DialogoIntervalo() {
        // Required empty public constructor
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        return crearDialogoConfInvervalo();
    }

    private AlertDialog crearDialogoConfInvervalo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_dialogo_intervalo, null);
        builder.setView(v);

        rb_no_select = v.findViewById(R.id.rb_no_select);
        rb_treinta_seg = v.findViewById(R.id.rb_treinta_seg);
        rb_un_min = v.findViewById(R.id.rb_un_min);
        rb_dos_min = v.findViewById(R.id.rb_dos_min);
        rb_cinco_min = v.findViewById(R.id.rb_cinco_min);
        rb_diez_min = v.findViewById(R.id.rb_diez_min);

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
        int intervalo;
        if (rb_no_select.isChecked()==true) {
            intervalo = 0;
        } else if (rb_treinta_seg.isChecked()==true) {
            intervalo = 30;
        } else if (rb_un_min.isChecked()==true) {
            intervalo = 60;
        }else if (rb_dos_min.isChecked()==true) {
            intervalo = 120;
        }else if (rb_cinco_min.isChecked()==true) {
            intervalo = 300;
        }else if (rb_diez_min.isChecked()==true) {
            intervalo = 600;
        }else{
            intervalo = 0;
        }

        ContentValues registro = new ContentValues();
        registro.put("intervalo", intervalo);
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
                "select intervalo from configuracion", null);
        if (consulta.moveToFirst()) {
            int intervalo = consulta.getInt(0);
            if (intervalo == 0) {
                rb_no_select.setChecked(true);
            } else if (intervalo == 30) {
                rb_treinta_seg.setChecked(true);
            } else if (intervalo == 60) {
                rb_un_min.setChecked(true);
            }else if (intervalo == 120) {
                rb_dos_min.setChecked(true);
            }else if (intervalo == 300) {
                rb_cinco_min.setChecked(true);
            }else if (intervalo == 600) {
                rb_diez_min.setChecked(true);
            }
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dialogo_intervalo, container, false);
    }
}