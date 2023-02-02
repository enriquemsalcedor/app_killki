package com.killki.app_killki.dialogos;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.os.Environment;
import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.killki.app_killki.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class DialogoVoz extends DialogFragment {

    MediaRecorder grabacion;
    String archivo = null;
    MediaPlayer player;

    TextView ibtn_grabar;
    TextView ibtn_detener;
    ImageButton ibtn_ayuda;
    TextView titulo;

    Button btn_reproducir;
    Button btn_cerrar;
    View cuerpo;
    View cuerpo_ayuda;

    private static final int RECOGNIZE_SPEECH_ACTIVITY = 1;
    private int RESULT_OK;

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

        btn_reproducir = v.findViewById(R.id.btn_reproducir);
        cuerpo = v.findViewById(R.id.cuerpo);
        cuerpo_ayuda = v.findViewById(R.id.cuerpo_ayuda);

        ibtn_detener = v.findViewById(R.id.ibtn_detener);

        btn_cerrar = v.findViewById(R.id.btn_cerrar);
        titulo = v.findViewById(R.id.titulo);

        ibtn_ayuda = v.findViewById(R.id.ibtn_ayuda);
        ibtn_ayuda.setVisibility(View.GONE);

        cuerpo_ayuda.setVisibility(View.GONE);

        ibtn_detener.setEnabled(false);

        //REPRODUCIR
        btn_reproducir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reproducir();
                //dismiss();
            }
        });

        //GRABAR
        ibtn_grabar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                grabar();
                hablar();
            }
        });

        //DETENER
        ibtn_detener.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detener();

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


    public void grabar() {

        if (grabacion == null){
            File directory;
            //archivo = Environment.getExternalStorageDirectory().getAbsolutePath() + "/grabacion.mp3";
            if (Environment.getExternalStorageState() == null) {
                directory = new File(Environment.getDataDirectory() + "/killki");

                if (!directory.exists()) {
                    directory.mkdir();
                }
                archivo = directory.toString() + "/grabacion.3gp";

            }else{
                archivo = Environment.getExternalStorageDirectory().getAbsolutePath() + "/grabacion.3gp";
            }

            archivo = Environment.getExternalStorageDirectory().getAbsolutePath() + "/grabacion.3gp";
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
            //ibtn_grabar.setImageResource(R.drawable.ic_action_mic_off);
            Toast.makeText(this.getContext(), "Grabando...",
                    Toast.LENGTH_SHORT).show();

        }
        /*
        grabacion = new MediaRecorder();
        grabacion.setAudioSource(MediaRecorder.AudioSource.MIC);
        grabacion.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        grabacion.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        File path = new File(Environment.getExternalStorageDirectory()
                .getPath());
        try {
            archivo = File.createTempFile("temporal", ".3gp", path);
        } catch (IOException e) {
        }
        grabacion.setOutputFile(archivo.getAbsolutePath());
        try {
            grabacion.prepare();
        } catch (IOException e) {
        }
        grabacion.start();
        */
        //ibtn_grabar.setEnabled(false);
        //ibtn_detener.setEnabled(true);
        ibtn_detener.setEnabled(true);

    }

    public void reproducir() {
        /*if (grabacion != null){

            grabacion.stop();
            grabacion.reset();
            grabacion.release();
            grabacion = null;

            //ibtn_grabar.setImageResource(R.drawable.ic_action_mic);
            Toast.makeText(this.getContext(), "Grabación finalizada...",
                    Toast.LENGTH_SHORT).show();

        }else{*/
            MediaPlayer mediaPlayer = new MediaPlayer();
            if (Environment.getExternalStorageState() == null) {

                archivo = Environment.getDataDirectory() + "/killki" + "/grabacion.3gp";

            }else{
                archivo = Environment.getExternalStorageDirectory().getAbsolutePath() + "/grabacion.3gp";
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
        //}

    }

    public void detener() {

        grabacion.stop();
        grabacion.reset();
        grabacion.release();
        Toast.makeText(getContext(), "Grabación finalizada.",
                Toast.LENGTH_SHORT).show();
        /*
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                try {
                    player.setDataSource(archivo.getAbsolutePath());
                } catch (IOException e) {
                    Toast.makeText(getContext(), "Ha ocurrido un error",
                            Toast.LENGTH_SHORT).show();
                }
                try {
                    player.prepare();
                } catch (IOException e) {
                    Toast.makeText(getContext(), "Ha ocurrido un error",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });*/

        /*
        ibtn_grabar.setEnabled(true);
        ibtn_detener.setEnabled(false);
        btn_reproducir.setEnabled(true);

         */
    }

    public void onCompletion(MediaPlayer mp) {
        Toast.makeText(getContext(), "Listo", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RECOGNIZE_SPEECH_ACTIVITY:
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> speech = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String strSpeech2Text = speech.get(0);
                    //grabar.setText(strSpeech2Text);
                    Toast.makeText(this.getContext(), "strSpeech2Text-> " + strSpeech2Text, Toast.LENGTH_LONG).show();

                    detener();
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
            Toast.makeText(getContext(),
                    "Tú dispositivo no soporta el reconocimiento por voz",
                    Toast.LENGTH_SHORT).show();
        }
    }

}