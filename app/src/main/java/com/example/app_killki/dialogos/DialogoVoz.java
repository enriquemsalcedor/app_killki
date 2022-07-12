package com.example.app_killki.dialogos;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.app_killki.R;

import java.io.File;
import java.io.IOException;


public class DialogoVoz extends DialogFragment {

    MediaRecorder grabacion;
    String archivo;

    ImageButton ibtn_grabar;
    ImageButton ibtn_reproducir;
    ImageButton ibtn_ayuda;
    TextView titulo;

    Button btn_guardar;
    Button btn_cerrar;
    View cuerpo;
    View cuerpo_ayuda;


    public DialogoVoz() {
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
        View v = inflater.inflate(R.layout.fragment_dialogo_voz, null);
        builder.setView(v);

        ibtn_grabar = v.findViewById(R.id.ibtn_grabar);
        ibtn_reproducir = v.findViewById(R.id.ibtn_reproducir);
        cuerpo = v.findViewById(R.id.cuerpo);
        cuerpo_ayuda = v.findViewById(R.id.cuerpo_ayuda);

        btn_guardar = v.findViewById(R.id.btn_guardar);
        btn_cerrar = v.findViewById(R.id.btn_cerrar);
        titulo = v.findViewById(R.id.titulo);

        ibtn_ayuda = v.findViewById(R.id.ibtn_ayuda);

        cuerpo_ayuda.setVisibility(View.GONE);

        btn_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardar();
                dismiss();
            }
        });

        //GRABAR
        ibtn_grabar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                grabar(view);
            }
        });

        //REPRODUCIR
        ibtn_reproducir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                reproducir(view);

            }
        });

        ibtn_ayuda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cuerpo_ayuda.setVisibility(View.VISIBLE);
                cuerpo.setVisibility(View.GONE);
                titulo.setText("AYUDA");

            }
        });

        btn_cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cuerpo_ayuda.setVisibility(View.GONE);
                cuerpo.setVisibility(View.VISIBLE);
                titulo.setText("CONFIGURACIÓN DE VOZ");

            }
        });


        return builder.create();
    }

    private void guardar() {
        Toast.makeText(this.getContext(), "Configuración éxitosa",
                Toast.LENGTH_SHORT).show();
    }

    public void grabar(View view) {

        if (grabacion == null){
            File directory;
            //archivo = Environment.getExternalStorageDirectory().getAbsolutePath() + "/grabacion.mp3";
            if (Environment.getExternalStorageState() == null) {
                directory = new File(Environment.getDataDirectory() + "/killki");

                if (!directory.exists()) {
                    directory.mkdir();
                }
                archivo = directory.toString() + "/grabacion.mp3";

            }else{
                archivo = Environment.getExternalStorageDirectory().getAbsolutePath() + "/grabacion.mp3";
            }

            archivo = Environment.getExternalStorageDirectory().getAbsolutePath() + "/grabacion.mp3";
            grabacion = new MediaRecorder();
            grabacion.setAudioSource(MediaRecorder.AudioSource.MIC);
            grabacion.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            grabacion.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            grabacion.setOutputFile(archivo);

            try {
                grabacion.prepare();
                grabacion.start();

            }catch (IOException e){

            }
            ibtn_grabar.setImageResource(R.drawable.ic_action_mic_off);
            Toast.makeText(this.getContext(), "Grabando...",
                    Toast.LENGTH_SHORT).show();

        }else if (grabacion != null){

            grabacion.stop();
            grabacion.reset();
            grabacion.release();
            grabacion = null;

            ibtn_grabar.setImageResource(R.drawable.ic_action_mic);
            Toast.makeText(this.getContext(), "Grabación finalizada...",
                    Toast.LENGTH_SHORT).show();

        }
    }

    public void reproducir(View view) {
        MediaPlayer mediaPlayer = new MediaPlayer();
        if (Environment.getExternalStorageState() == null) {

            archivo = Environment.getDataDirectory() + "/killki" + "/grabacion.mp3";

        }else{
            archivo = Environment.getExternalStorageDirectory().getAbsolutePath() + "/grabacion.mp3";
        }
        //archivo = Environment.getDataDirectory().getAbsolutePath() + "/grabacion.mp3";
        try{
            mediaPlayer.setDataSource(archivo);
            mediaPlayer.prepare();
        }catch (IOException e){

        }
        mediaPlayer.start();
        Toast.makeText(this.getContext(), "Reproduciendo grabación...",
                Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dialogo_voz, container, false);
    }

}