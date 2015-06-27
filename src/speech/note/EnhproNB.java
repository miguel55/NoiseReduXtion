/*  Copyright 2015 Miguel Molina

    EnhproNB is part of the package speech.note of library NoiseReduXtion.

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
package speech.note;

import speech.common.Complex;
import speech.common.FFT;
import java.util.Arrays;


/**
 *
 * @author Miguel
 */
public class EnhproNB {
    private int L, Os, Fs, M;
    private double a0, a1;
    private short[] x1, x2;
    Complex[][] Xf1,Xf2;
    Complex[] Xcomplex;
    short[] Xrec;
    
    // Constructor por defecto
    public EnhproNB(short[] signal1, short[] signal2){
        a0 = 0.5;
        a1 = 0.5;
        Fs=8000;
        L = 160;
        M=256;
        Os=L/2;
        x1=signal1;
        x2=signal2;
        Xf1=new Complex[M][(x1.length-L)/Os+1];
        Xf2=new Complex[M][(x1.length-L)/Os+1];
        Xcomplex=new Complex[Xf1[0].length*Os+M];
        Xrec=new short[x1.length];
    }
    // Constructor con parámetros
    public EnhproNB(short[] signal1, short[] signal2, int Freq, int Lframe, int Ltransf) 
        throws InvalidFrequencyException, InvalidParametersException{
        if ((Freq == 8000) || (Freq == 16000) || (Freq==44100)){
            if (Lframe<Ltransf){
                a0 = 0.5;
                a1 = 0.5;
                Fs=Freq;
                L = Lframe;
                M=Ltransf;
                Os=L/2;
                x1=signal1;
                x2=signal2;
                Xf1=new Complex[M][(x1.length-L)/Os+1];
                Xf2=new Complex[M][(x1.length-L)/Os+1];
                Xcomplex=new Complex[Xf1[0].length*Os+M];
                Xrec=new short[x1.length];
            } else{
                throw new InvalidParametersException(Lframe, Ltransf);
            }
        } else {
            throw new InvalidFrequencyException(Freq);
        }
    }
    
    public void preProcessing(){
        double[] hann=new double[L];
        Complex[] frame1=new Complex[L], frame2=new Complex[L];
        for (int i=0; i<L; i++){
            hann[i]=a0-a1*Math.cos((2*Math.PI*i)/(L-1));
        }
        int it=0;
        for (int t=0; t<=(x1.length-L); t=t+Os){
            for (int i=0; i<L; i++){
                frame1[i]=new Complex((double)x1[t+i]*hann[i],0);
                frame2[i]=new Complex((double)x2[t+i]*hann[i],0);
            }
            Complex[] aux1=FFT.fft(frame1, M);
            Complex[] aux2=FFT.fft(frame2, M);
            for (int i=0; i<M; i++){
                Xf1[i][it]=aux1[i];
                Xf2[i][it]=aux2[i];
            }
            it=it+1;
        }
    }
    
    public void postProcessing(Complex[][] Xf){
        Arrays.fill(Xcomplex, new Complex(0,0));
        for (int i=0; i<Xf[0].length; i++){
            Complex[] aux=FFT.ifft(obtCol(Xf,i),M);
            for (int j=0; j<M; j++){
                Xcomplex[Os*i+j]=Xcomplex[Os*i+j].addition(aux[j].getReal());
            }
        }
        for (int i=0; i<x1.length; i++){
            Xrec[i]=(short) Math.round(Xcomplex[i].getReal());
        }
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
