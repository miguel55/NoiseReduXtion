/*  Copyright 2015 Miguel Molina

    EnhproFB is part of the package speech.frame of library NoiseReduXtion.

    NoiseReduXtion is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License version 3 as published 
    by the Free Software Foundation.

    NoiseReduXtion is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with NoiseReduXtion.  If not, see <http://www.gnu.org/licenses/>.
*/
package speech.frame;


import speech.common.FFT;
import speech.common.Complex;
import java.util.Arrays;


/**
 *
 * @author Miguel
 */
public class EnhproFB {
    private boolean initPre, initPost;
    private int L, Os, Fs, M;
    private double a0, a1;
    private double[] hann;
    private short[] ov1, ov2;
    private Complex[] frame1, frame2, Xcomplex;
    Complex[][] Xf1,Xf2;
    short[] Xrec;
    
    // Constructor por defecto
    public EnhproFB(){
        Fs=8000;
        L = 160;
        M=256;
        Os=L/2;
        a0=0.5;
        a1=0.5;
        initPre=true;
        initPost=true;
        hann=new double[L];
        ov1=new short[Os];
        ov2=new short[Os];
        frame1=new Complex[L];
        frame2=new Complex[L];
        Xcomplex=new Complex[M+L];
        for (int i=0; i<L; i++){
            hann[i]=a0-a1*Math.cos((2*Math.PI*i)/(L-1));
        }
    }
    // Constructor con parámetros
    public EnhproFB(int Freq, int Lframe, int Ltransf) 
        throws InvalidFrequencyException, InvalidParametersException{
        if ((Freq == 8000) || (Freq == 16000) || (Freq==44100)){
            if (Lframe<Ltransf){
                Fs=Freq;
                L = Lframe;
                M=Ltransf;
                Os=L/2;
                a0=0.5;
                a1=0.5;
                initPre=true;
                initPost=true;
                hann=new double[L];
                ov1=new short[Os];
                ov2=new short[Os];
                frame1=new Complex[L];
                frame2=new Complex[L];
                Xcomplex=new Complex[M+L];
                for (int i=0; i<L; i++){
                    hann[i]=a0-a1*Math.cos((2*Math.PI*i)/(L-1));
                }
            } else{
                throw new InvalidParametersException(Lframe, Ltransf);
            }
        } else {
            throw new InvalidFrequencyException(Freq);
        }
    }
    
    public void preProcessing(short[] x1, short[] x2){
        if (initPre){
            Xf1=new Complex[M][1];
            Xf2=new Complex[M][1];
            for (int i=0; i<L; i++){
                frame1[i]=new Complex((double)x1[i]*hann[i],0);
                frame2[i]=new Complex((double)x2[i]*hann[i],0);
            }
            Complex[] aux1=FFT.fft(frame1, M);
            Complex[] aux2=FFT.fft(frame2, M);
            for (int i=0; i<M; i++){
                Xf1[i][0]=aux1[i];
                Xf2[i][0]=aux2[i];
            }
            initPre=false;
        } else{
            short[] signal1, signal2;
            Xf1=new Complex[M][2];
            Xf2=new Complex[M][2];
            signal1=new short[L+Os];
            signal2=new short[L+Os];
            System.arraycopy(ov1, 0, signal1, 0, Os);
            System.arraycopy(ov2, 0, signal2, 0, Os);
            System.arraycopy(x1, 0, signal1, Os, L); 
            System.arraycopy(x2, 0, signal2, Os, L);
            for (int t=0; t<2; t++){
                for (int i=0; i<L; i++){
                    frame1[i]=new Complex((double)signal1[Os*t+i]*hann[i],0);
                    frame2[i]=new Complex((double)signal2[Os*t+i]*hann[i],0);
                }
                Complex[] aux1=FFT.fft(frame1, M);
                Complex[] aux2=FFT.fft(frame2, M);
                for (int i=0; i<M; i++){
                    Xf1[i][t]=aux1[i];
                    Xf2[i][t]=aux2[i];
                }
            }
        }
        System.arraycopy(x1, Os, ov1, 0, Os);
        System.arraycopy(x2, Os, ov2, 0, Os);
    }
    
    public void postProcessing(Complex[][] Xf, boolean end){
        int v=0;
        if (end){
            v=Os;
        }
        Xrec=new short[Xf1[0].length*Os+v];
        if (initPost){
            Arrays.fill(Xcomplex,new Complex(0,0));
            initPost=false;
        }
        for (int i=0; i<Xf[0].length; i++){
            Complex[] aux=FFT.ifft(obtCol(Xf,i),M);
            for (int j=0; j<M; j++){
                Xcomplex[Os*i+j]=Xcomplex[Os*i+j].addition(aux[j].getReal());
            }
        }
        for (int i=0; i<Xf1[0].length*Os+v; i++){
            Xrec[i]=(short) Math.round(Xcomplex[i].getReal());          
        }
        System.arraycopy(Xcomplex,Xf1[0].length*Os,Xcomplex,0,Xcomplex.length-Xf1[0].length*Os);
        Arrays.fill(Xcomplex,Os,Xcomplex.length,new Complex(0,0));
    }
    private static Complex[] obtCol (Complex[][] mat, int index){
        Complex[] aux=new Complex [mat.length];
        for (int i=0; i<mat.length; i++){
            aux[i]=mat[i][index];
        }
        return aux;
    }
    
    public int getFs(){
        return Fs;
    }
    
    public int getM(){
        return M;
    }
    
    public class InvalidFrequencyException extends Exception {
        public InvalidFrequencyException (int freq) {
            super("Invalid frequency: " + freq + ". Frequency must be 8000, "
                    + "16000 or 44100 Hz.");
        }
    }
    
    public class InvalidParametersException extends Exception {
        public InvalidParametersException (int L, int M) {
            super("Invalid parameters: " + L + " (frame length) " + M + 
                    " (Fourier transform length). L can't be bigger than M.");
        }
    }
}
