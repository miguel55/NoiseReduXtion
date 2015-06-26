package speech.common;

import java.util.Arrays;

/**
 *
 * @author Miguel
 */
public class MusFilter {
    private final double Eth;
    private final int PHI, f0, Fs;
    
    public MusFilter(){
        Eth = 0.4;
        PHI=10;
        f0=1000;
        Fs=8000;
    }
    
    public MusFilter(double Eu_def, int PHIu_def, int f0u_def, int Fsu_def){
        Eth = Eu_def;
        PHI=PHIu_def;
        f0=f0u_def;
        Fs=Fsu_def;
    }
    
    public double[][] mus_filter(Complex[][] Xf1, double[][] G0){
        Complex[][] Xf_aux;
        double[][] G_aux, GPF, H;
        double[] E, N;
        
        Xf_aux=new Complex[G0.length/2+1-Math.round((2*f0*(G0.length/2+1))/Fs)][G0[0].length];
        G_aux=new double[G0.length/2+1-Math.round((2*f0*(G0.length/2+1))/Fs)][G0[0].length];
        for (int i=Math.round((2*f0*(G0.length/2+1))/Fs); i<G0.length/2+1; i++){
            System.arraycopy(G0[i],0,G_aux[i-Math.round((2*f0*(G0.length/2+1))/Fs)],0,G0[0].length);
            System.arraycopy(Xf1[i],0,Xf_aux[i-Math.round((2*f0*(G0.length/2+1))/Fs)],0,Xf1[0].length);
        }
        E=new double[G_aux[0].length];
        N=new double[G_aux[0].length];
        Arrays.fill(E, 0);
        for (int i=0; i<G_aux[0].length; i++){
            for (int j=0; j<G_aux.length; j++){
                E[i]=E[i]+((Xf_aux[j][i].product(G_aux[j][i])).product(Xf_aux[j][i].product(G_aux[j][i])).module())/(Xf_aux[j][i].product(Xf_aux[j][i]).module());
            }
            if (E[i]>=Eth){
                N[i]=1;
            } else{
                N[i]=2*Math.round((1-E[i]/Eth)*PHI)+1;
            }
        }
        H=new double[Xf_aux.length][Xf_aux[0].length];
        for (int i=0; i<Xf_aux.length; i++){
            for (int j=0; j<Xf_aux[0].length; j++){
                if (i<N[j]){
                    H[i][j]=1/N[j];
                } else{
                    H[i][j]=0;
                }
            }
        }
        GPF=new double[Xf_aux[0].length][Xf_aux.length];
        for (int j=0; j<Xf_aux[0].length; j++){
            GPF[j]=FFT.convolution(obtCol(G_aux,j), obtCol(H,j));
        }  
        
        for (int i=(Math.round((2*f0*(G0.length/2+1))/Fs)); i<G0.length/2+1; i++){
            for (int j=0; j<G0[i].length; j++){
                G0[i][j]=GPF[j][i-(Math.round((2*f0*(G0.length/2+1))/Fs))];
            }
        }
        return G0;
    }
    
    private static double[] obtCol (double[][] mat, int index){
        double[] aux=new double [mat.length];
        for (int i=0; i<mat.length; i++){
            aux[i]=mat[i][index];
        }
        return aux;
    }
}