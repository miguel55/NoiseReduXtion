/*  Copyright 2015 Miguel Molina

    Graba is part of native code NativeReduXtion app.

    NativeReduXtion is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License version 3 as published
    by the Free Software Foundation.

    NativeReduXtion is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with NativeReduXtion.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.example.speechenh;

import android.app.Activity;
import android.app.ProgressDialog;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Graba extends Activity implements View.OnClickListener{
    private TextView statusText;
    private Button[] btns2=new Button[6];
    private short[] aux, sFront, sBack, clean;
    private RecordAudio recordTask;
    private PlayAudio playTask;
    private File sfFile, sbFile, algFile;
    boolean recording, playing;
    final int freq=8000;
    final int audioSource= MediaRecorder.AudioSource.CAMCORDER;
    final int channelConfiguration = AudioFormat.CHANNEL_IN_STEREO;
    final int channelOut= AudioFormat.CHANNEL_OUT_MONO;
    final int audioEncoding= AudioFormat.ENCODING_PCM_16BIT;
    private String mode, alg;
    private ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graba);
        mode = getIntent().getStringExtra("mode");
        alg = getIntent().getStringExtra("alg");

        statusText = (TextView) this.findViewById(R.id.statustxt);

        btns2[0] = (Button)findViewById(R.id.button1);
        btns2[1] = (Button)findViewById(R.id.button2);
        btns2[2] = (Button)findViewById(R.id.button3);
        btns2[3] = (Button)findViewById(R.id.button4);
        btns2[4] = (Button)findViewById(R.id.button5);
        btns2[5] = (Button)findViewById(R.id.button6);

        for (int i=0; i<6; i++){
            btns2[i].setOnClickListener(this);
            if (i!=0)
                btns2[i].setEnabled(false);
        }
        // Create file
        File path=new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/");
        path.mkdir();
        try {
            sfFile = File.createTempFile("noisy", ".pcm", path);
            sbFile = File.createTempFile("back", ".pcm", path);
            algFile = File.createTempFile("clean", ".pcm", path);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't create file on SD card", e);
        }

    }

    public void onClick(View v){
        if (v==btns2[0]){
            record();
        } else if (v==btns2[1]){
            dialog=new ProgressDialog(Graba.this);
            dialog.setTitle("Por favor espere");
            dialog.setMessage("Procesando...");
            dialog.show();
            stopRecording();
        } else if (v==btns2[2]){
            playN();
        } else if (v==btns2[3]){
            stopPlaying();
        } else if (v==btns2[4]){
            playC();
        } else if (v==btns2[5]){
            stopPlaying();
        }
    }

    // FUNCIONALIDADES DE LOS BOTONES: GRABACIÓN, DETENCIÓN Y REPRODUCCIÓN

    public void record() {
        recording=true;
        btns2[0].setEnabled(false);
        btns2[1].setEnabled(true);
        recordTask= new RecordAudio();
        recordTask.execute();
    }

    public void stopRecording () {
        recording=false;
        btns2[0].setEnabled(true);
        btns2[1].setEnabled(false);
        btns2[2].setEnabled(true);
        btns2[4].setEnabled(true);
    }

    public void playN () {
        playing=true;
        btns2[2].setEnabled(false);
        btns2[4].setEnabled(false);
        btns2[3].setEnabled(true);

        playTask=new PlayAudio();
        playTask.execute(sfFile);
    }

    public void playC () {
        playing=true;
        btns2[2].setEnabled(false);
        btns2[4].setEnabled(false);
        btns2[5].setEnabled(true);

        playTask=new PlayAudio();
        playTask.execute(algFile);
    }

    public void stopPlaying () {
        playing=false;
        btns2[2].setEnabled(true);
        btns2[4].setEnabled(true);
        btns2[3].setEnabled(false);
        btns2[5].setEnabled(false);
    }

    // ALGORITMOS DEL MODO NOTA DE AUDIO

    private void processNB(){
        switch (alg) {
            case "MVDR-MR":
                clean= MVDRdNBNat.MVDRdNB(sFront, sBack);
                break;
            case "MVDR-M":
                clean= MVDRNBNat.MVDRNB(sFront, sBack);
                break;
            case "PLD":
                clean= PLDNBNat.PLDNB(sFront, sBack, false);
                break;
            case "PLD-SR":
                clean= PLDNBNat.PLDNB(sFront, sBack, true);
                break;
            case "PLR-SIG":
                clean= SigmoidNBNat.SigmoidNB(sFront, sBack, false);
                break;
            case "PLR-SIG-SR":
                clean= SigmoidNBNat.SigmoidNB(sFront, sBack, true);
                break;
            default:
                clean=new short[1];
                launchToast();
        }
        saveClean(clean);
    }

    static {
        System.loadLibrary("noiseReduXtion");
    }

    // ALGORITMOS DEL MODO BASADO EN TRAMAS

    private void mvdrFB() {
        DataInputStream iSt = null, bSt = null;
        try {
            iSt= new DataInputStream(new BufferedInputStream(new FileInputStream(sfFile)));
            bSt= new DataInputStream(new BufferedInputStream(new FileInputStream(sbFile)));
        } catch (FileNotFoundException e) {
            Toast.makeText(this,"File Not Found",Toast.LENGTH_LONG).show();
        }
        try {
            sFront=new short[160];
            sBack=new short[160];
            clean=new short[iSt.available()/2];
            boolean continuar=true;
            int k=0;
            int ntrama=0;;
            MVDRFBNat.MVDRFB();
            while(iSt.available()>0 && continuar){
                sFront[k]=iSt.readShort();
                sBack[k]=bSt.readShort();
                k=k+1;
                if (k==160){
                    k=0;
                    if (iSt.available()<320) {
                        aux=MVDRFBNat.processing(sFront, sBack, 1);
                        continuar=false;
                    }  else{
                        if (ntrama==0) {
                            aux = MVDRFBNat.processing(sFront, sBack, 0);
                        }else{
                            aux=MVDRFBNat.processing(sFront, sBack, 2);
                        }
                    }
                    if (ntrama==0){
                        System.arraycopy(aux, 0, clean, ntrama * 160, aux.length);
                    } else{
                        System.arraycopy(aux, 0, clean, ntrama * 160 - 80, aux.length);
                    }
                    ntrama=ntrama+1;
                }
            }
            iSt.close();
            bSt.close();
        } catch (IOException e) {
            Toast.makeText(this,"I/O Exception",Toast.LENGTH_LONG).show();
        }
        saveClean(clean);
    }


    private void mvdr_retFB() {
        DataInputStream iSt = null, bSt = null;
        try {
            iSt= new DataInputStream(new BufferedInputStream(new FileInputStream(sfFile)));
            bSt= new DataInputStream(new BufferedInputStream(new FileInputStream(sbFile)));
        } catch (FileNotFoundException e) {
            Toast.makeText(this,"File Not Found",Toast.LENGTH_LONG).show();
        }
        try {
            sFront=new short[160];
            sBack=new short[160];
            clean=new short[iSt.available()/2];
            boolean continuar=true;
            int k=0;
            int ntrama=0;
            MVDRdFBNat.MVDRdFB();
            while(iSt.available()>0 && continuar){
                sFront[k]=iSt.readShort();
                sBack[k]=bSt.readShort();
                k=k+1;
                if (k==160){
                    k=0;
                    if (iSt.available()<320) {
                        aux=MVDRdFBNat.processing(sFront, sBack, 1);
                        continuar=false;
                    }  else{
                        if (ntrama==0) {
                            aux = MVDRdFBNat.processing(sFront, sBack, 0);
                        }else{
                            aux=MVDRdFBNat.processing(sFront, sBack, 2);
                        }
                    }
                    if (ntrama==0){
                        System.arraycopy(aux, 0, clean, ntrama * 160, aux.length);
                    } else{
                        System.arraycopy(aux, 0, clean, ntrama * 160 - 80, aux.length);
                    }
                    ntrama=ntrama+1;
                }
            }
            iSt.close();
            bSt.close();
        } catch (IOException e) {
            Toast.makeText(this,"I/O Exception",Toast.LENGTH_LONG).show();
        }
        saveClean(clean);
    }

    private void pldFB() {
        DataInputStream iSt = null, bSt = null;
        try {
            iSt= new DataInputStream(new BufferedInputStream(new FileInputStream(sfFile)));
            bSt= new DataInputStream(new BufferedInputStream(new FileInputStream(sbFile)));
        } catch (FileNotFoundException e) {
            Toast.makeText(this,"File Not Found",Toast.LENGTH_LONG).show();
        }
        try {
            sFront=new short[160];
            sBack=new short[160];
            clean=new short[iSt.available()/2];
            boolean continuar=true;
            int k=0;
            int ntrama=0;
            PLDFBNat.PLDFB(false);
            while(iSt.available()>0 && continuar){
                sFront[k]=iSt.readShort();
                sBack[k]=bSt.readShort();
                k=k+1;
                if (k==160){
                    k=0;
                    if (iSt.available()<320) {
                        aux=PLDFBNat.processing(sFront, sBack, 1);
                        continuar=false;
                    }  else{
                        if (ntrama==0) {
                            aux = PLDFBNat.processing(sFront, sBack, 0);
                        }else{
                            aux=PLDFBNat.processing(sFront, sBack, 2);
                        }
                    }
                    if (ntrama==0){
                        System.arraycopy(aux, 0, clean, ntrama * 160, aux.length);
                    } else{
                        System.arraycopy(aux, 0, clean, ntrama * 160 - 80, aux.length);
                    }
                    ntrama=ntrama+1;
                }
            }
            iSt.close();
            bSt.close();
        } catch (IOException e) {
            Toast.makeText(this,"I/O Exception",Toast.LENGTH_LONG).show();
        }
        saveClean(clean);
    }

    private void pldsrFB() {
        DataInputStream iSt = null, bSt = null;
        try {
            iSt= new DataInputStream(new BufferedInputStream(new FileInputStream(sfFile)));
            bSt= new DataInputStream(new BufferedInputStream(new FileInputStream(sbFile)));
        } catch (FileNotFoundException e) {
            Toast.makeText(this,"File Not Found",Toast.LENGTH_LONG).show();
        }
        try {
            sFront=new short[160];
            sBack=new short[160];
            clean=new short[iSt.available()/2];
            boolean continuar=true;
            int k=0;
            int ntrama=0;
            PLDFBNat.PLDFB(true);
            while(iSt.available()>0 && continuar){
                sFront[k]=iSt.readShort();
                sBack[k]=bSt.readShort();
                k=k+1;
                if (k==160){
                    k=0;
                    if (iSt.available()<320) {
                        aux=PLDFBNat.processing(sFront, sBack, 1);
                        continuar=false;
                    }  else{
                        if (ntrama==0) {
                            aux = PLDFBNat.processing(sFront, sBack, 0);
                        }else{
                            aux=PLDFBNat.processing(sFront, sBack, 2);
                        }
                    }
                    if (ntrama==0){
                        System.arraycopy(aux, 0, clean, ntrama * 160, aux.length);
                    } else{
                        System.arraycopy(aux, 0, clean, ntrama * 160 - 80, aux.length);
                    }
                    ntrama=ntrama+1;
                }
            }
            iSt.close();
            bSt.close();
        } catch (IOException e) {
            Toast.makeText(this,"I/O Exception",Toast.LENGTH_LONG).show();
        }
        saveClean(clean);
    }

    private void plrFB() {
        DataInputStream iSt = null, bSt = null;
        try {
            iSt= new DataInputStream(new BufferedInputStream(new FileInputStream(sfFile)));
            bSt= new DataInputStream(new BufferedInputStream(new FileInputStream(sbFile)));
        } catch (FileNotFoundException e) {
            Toast.makeText(this,"File Not Found",Toast.LENGTH_LONG).show();
        }
        try {
            sFront=new short[160];
            sBack=new short[160];
            clean=new short[iSt.available()/2];
            boolean continuar=true;
            int k=0;
            int ntrama=0;
            SigmoidFBNat.SigmoidFB(false);
            while(iSt.available()>0 && continuar){
                sFront[k]=iSt.readShort();
                sBack[k]=bSt.readShort();
                k=k+1;
                if (k==160){
                    k=0;
                    if (iSt.available()<320) {
                        aux=SigmoidFBNat.processing(sFront, sBack, 1);
                        continuar=false;
                    }  else{
                        if (ntrama==0) {
                            aux = SigmoidFBNat.processing(sFront, sBack, 0);
                        }else{
                            aux=SigmoidFBNat.processing(sFront, sBack, 2);
                        }
                    }
                    if (ntrama==0){
                        System.arraycopy(aux, 0, clean, ntrama * 160, aux.length);
                    } else{
                        System.arraycopy(aux, 0, clean, ntrama * 160 - 80, aux.length);
                    }
                    ntrama=ntrama+1;
                }
            }
            iSt.close();
            bSt.close();
        } catch (IOException e) {
            Toast.makeText(this,"I/O Exception",Toast.LENGTH_LONG).show();
        }
        saveClean(clean);
    }

    private void plrsrFB() {
        DataInputStream iSt = null, bSt = null;
        try {
            iSt= new DataInputStream(new BufferedInputStream(new FileInputStream(sfFile)));
            bSt= new DataInputStream(new BufferedInputStream(new FileInputStream(sbFile)));
        } catch (FileNotFoundException e) {
            Toast.makeText(this,"File Not Found",Toast.LENGTH_LONG).show();
        }
        try {
            sFront=new short[160];
            sBack=new short[160];
            clean=new short[iSt.available()/2];
            boolean continuar=true;
            int k=0;
            int ntrama=0;
            SigmoidFBNat.SigmoidFB(true);
            while(iSt.available()>0 && continuar){
                sFront[k]=iSt.readShort();
                sBack[k]=bSt.readShort();
                k=k+1;
                if (k==160){
                    k=0;
                    if (iSt.available()<320) {
                        aux=SigmoidFBNat.processing(sFront, sBack, 1);
                        continuar=false;
                    }  else{
                        if (ntrama==0) {
                            aux = SigmoidFBNat.processing(sFront, sBack, 0);
                        }else{
                            aux=SigmoidFBNat.processing(sFront, sBack, 2);
                        }
                    }
                    if (ntrama==0){
                        System.arraycopy(aux, 0, clean, ntrama * 160, aux.length);
                    } else{
                        System.arraycopy(aux, 0, clean, ntrama * 160 - 80, aux.length);
                    }
                    ntrama=ntrama+1;
                }
            }
            iSt.close();
            bSt.close();
        } catch (IOException e) {
            Toast.makeText(this,"I/O Exception",Toast.LENGTH_LONG).show();
        }
        saveClean(clean);
    }

    // TAREAS ASÍNCRONAS: GRABACIÓN, REPRODUCCIÓN; Y GUARDADO DE LA SEÑAL LIMPIA

    private class RecordAudio extends AsyncTask<Void,Integer,Void> {
        @Override
        protected void onPreExecute(){
        }
        @Override
        protected Void doInBackground(Void... params) {
            int bufferSize = AudioRecord.getMinBufferSize(freq, channelConfiguration, audioEncoding);
            short[] buffer=new short[bufferSize];
            try{
                DataOutputStream oStream= new DataOutputStream(new BufferedOutputStream(new FileOutputStream(sfFile)));
                DataOutputStream bStream= new DataOutputStream(new BufferedOutputStream(new FileOutputStream(sbFile)));
                AudioRecord audioRecord=new AudioRecord(audioSource,freq,channelConfiguration,audioEncoding,bufferSize);
                audioRecord.startRecording();
                int r=0;
                while (recording){
                    int bufferRead=audioRecord.read(buffer, 0, bufferSize);
                    for (int i = 0; i < bufferRead/2; i++) {
                        bStream.writeShort(buffer[2*i]);
                        oStream.writeShort(buffer[2 * i + 1]);
                    }
                    publishProgress(r);
                    r++;
                }
                audioRecord.stop();
                audioRecord.release();
                oStream.close();
                bStream.close();
            }
            catch (Throwable o) {
                launchToast();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progress){
            statusText.setText(progress[0].toString());
        }
        @Override
        protected void onPostExecute(Void result) {
            btns2[0].setEnabled(true);
            btns2[1].setEnabled(false);
            btns2[2].setEnabled(true);
            btns2[4].setEnabled(true);
            if (mode.equals("note")) {
                DataInputStream iSt = null, bSt = null;
                try {
                    iSt= new DataInputStream(new BufferedInputStream(new FileInputStream(sfFile)));
                    bSt= new DataInputStream(new BufferedInputStream(new FileInputStream(sbFile)));
                } catch (FileNotFoundException e) {
                    launchToast();
                }
                try {
                    sFront=new short[iSt.available()/2];
                    sBack=new short[bSt.available()/2];
                    int k=0;
                    while(iSt.available()>0){
                        sFront[k]=iSt.readShort();
                        sBack[k]=bSt.readShort();
                        k=k+1;
                    }
                    iSt.close();
                    bSt.close();
                } catch (IOException e) {
                    launchToast();
                }
                processNB();
            }

            if (mode.equals("frame")) {
                switch (alg) {
                    case "MVDR-MR":
                        mvdr_retFB();
                        break;
                    case "MVDR-M":
                        mvdrFB();
                        break;
                    case "PLD":
                        pldFB();
                        break;
                    case "PLD-SR":
                        pldsrFB();
                        break;
                    case "PLR-SIG":
                        plrFB();
                        break;
                    case "PLR-SIG-SR":
                        plrsrFB();
                        break;
                    default:
                        launchToast();
                }
            }
        }
    }

    private void saveClean(short[] clean) {
        DataOutputStream oStream = null;
        try {
            oStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(algFile)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            for (int i = 0; i < clean.length; i++) {
                oStream.writeShort(clean[i]);
            }
            oStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        dialog.dismiss();
    }

    private class PlayAudio extends AsyncTask<File,Integer,Void> {
        @Override
        protected void onPreExecute(){

        }
        @Override
        protected Void doInBackground(File... fileObj) {
            int bufferSize = AudioTrack.getMinBufferSize(freq, channelOut, audioEncoding);
            short[] audioFragment=new short[bufferSize];
            try{
                DataInputStream iStream= new DataInputStream(new BufferedInputStream(new FileInputStream(fileObj[0])));
                AudioTrack audioTrack=new AudioTrack(AudioManager.STREAM_MUSIC, freq,channelOut,
                        audioEncoding, bufferSize, AudioTrack.MODE_STREAM);
                audioTrack.play();
                while (playing && iStream.available() > 0) {
                    int i=0;
                    while (iStream.available() > 0 && i < audioFragment.length) {
                        audioFragment[i] = iStream.readShort();
                        i++;
                    }
                    audioTrack.write(audioFragment, 0, audioFragment.length);
                }
                audioTrack.stop();
                audioTrack.release();
                iStream.close();
            } catch (Throwable o){
                launchToast();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progress){
            statusText.setText(progress[0].toString());
        }
        @Override
        protected void onPostExecute(Void result){
            btns2[2].setEnabled(true);
            btns2[3].setEnabled(false);
            btns2[4].setEnabled(true);
            btns2[5].setEnabled(false);
        }
    }

    private void launchToast() {
        Toast toast= Toast.makeText(this, "No se ha podido aplicar el algoritmo", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }

}
