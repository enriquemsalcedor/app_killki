package com.killki.app_killki.dialogos;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.killki.app_killki.AdminSQLiteOpenHelper;
import com.killki.app_killki.OrganizacionActivity;
import com.killki.app_killki.R;

public class DialogoOrganizacion extends DialogFragment {

    private Button btn_guardar;
    private EditText txt_nombre;
    private EditText txt_email;
    private EditText txt_direccion;
    private EditText txt_telefonos;
    private ImageView img_logo;

    public DialogoOrganizacion() {
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
        View v = inflater.inflate(R.layout.fragment_organizacion, null);
        builder.setView(v);

        btn_guardar = v.findViewById(R.id.btn_guardar);
        txt_nombre = v.findViewById(R.id.txt_nombre);
        txt_email = v.findViewById(R.id.txt_email);
        txt_direccion = v.findViewById(R.id.txt_direccion);
        txt_telefonos = v.findViewById(R.id.txt_telefonos);
        img_logo = v.findViewById(R.id.img_logo);

        btn_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardar();

            }
        });

        img_logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirGaleria();

            }
        });

        return builder.create();
    }

    private void guardar() {

        String nombre = txt_nombre.getText().toString();
        String email = txt_email.getText().toString();
        String direccion = txt_direccion.getText().toString();
        String telefonos = txt_telefonos.getText().toString();

        if (nombre.equals("") || email.equals("") ||
                direccion.equals("") || telefonos.equals("")){
            Toast.makeText(this.getContext(), "Debes llenar los campos para poder registrar.", Toast.LENGTH_SHORT)
                    .show();
        }else {

            img_logo.buildDrawingCache();
            Bitmap bmap = img_logo.getDrawingCache();

            ContentValues registro = new ContentValues();

            registro.put("nombre", nombre);
            registro.put("email", email);
            registro.put("direccion", direccion);
            registro.put("telefono", telefonos);
            registro.put("img", telefonos);

            AdminSQLiteOpenHelper con = new AdminSQLiteOpenHelper(this.getContext(), "killki", null, 1);
            SQLiteDatabase bd = con.getWritableDatabase();
            bd.insert("organizacion", null, registro);
            bd.close();

            Toast.makeText(this.getContext(), "ONG registrada con éxito", Toast.LENGTH_SHORT)
                    .show();
            Intent i = new Intent(this.getContext(), OrganizacionActivity.class );
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            startActivity(i);

        }

    }

    public void abrirGaleria(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/");
        startActivityForResult(intent.createChooser(intent, "Seleccione la aplicación"), 10);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK){
            Uri path = data.getData();
            img_logo.setImageURI(path);
        }

    }
}