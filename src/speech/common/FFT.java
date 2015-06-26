package speech.common;

import java.util.Arrays;

/**
 *
 * @author Miguel
 */
public abstract class FFT {
    
    public static Complex[] fft(Complex[] signal, int M){
        // We suppose M is bigger than L
        int L=signal.length;
        Complex[] signal1=Arrays.copyOf(signal,M);
        Arrays.fill(signal1,L,M,new Complex());
        if (M%2!=0){ 
            throw new RuntimeException("If M is not a power of 2, algorithm can not be applied"); 
        }
        
        if (M==2){
            return new Complex[] {signal1[0].addition(signal1[1]), signal1[0].subtraction(signal1[1])};
        }
        // Odd and even part division
        Complex[] even=new Complex[M/2];
        Complex[] odd=new Complex[M/2];
        for (int i=0; i<M/2; i++){
            even[i]=signal1[2*i];
            odd[i]=signal1[2*i+1];
        }
        
        // Fourier-Transform of M/2 sequences
        Complex[] ffteven=fft(even,M/2);
        Complex[] fftodd=fft(odd,M/2);
        
        Complex[] fftcomplete=new Complex[M];
        for (int i=0; i<M/2; i++){
            double exp=-2*Math.PI*i/M;
            Complex offset=new Complex(Math.cos(exp),Math.sin(exp));
            fftcomplete[i]=ffteven[i].addition(fftodd[i].product(offset));
            fftcomplete[i+M/2]=ffteven[i].subtraction(fftodd[i].product(offset));
        }
        
        return fftcomplete;
        
    }
    
    public static Complex[] ifft(Complex[] sfreq, int M){
        // We suppose M is bigger than L
        Complex[] stime=new Complex[M];

        for (int i=0; i<M; i++){
            stime[i]=sfreq[i].conjugated();
        }
        stime=FFT.fft(stime,M);     // Vector is re-used*/
        // Taking conjugated number again and dividing by M
        for (int i=0; i<M; i++){
            stime[i]=(stime[i].conjugated()).product(1.0/M);
        }
        return stime;
        
    }
    
    public static double[] convolution(double[] signal1, double[] signal2){
        double[] conv=new double[signal1.length+signal2.length];
        
        for (int i=0; i<signal1.length; i++){
            for (int j=0; j<signal2.length; j++){
                conv[i+j]=conv[i+j]+signal1[i]*signal2[j];
            }
        }
        return conv;
    }
}
