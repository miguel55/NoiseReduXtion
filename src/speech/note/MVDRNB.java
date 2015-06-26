package speech.note;

import speech.common.Complex;

/**
 *
 * @author Miguel
 */
public class MVDRNB extends FilteringNB{
    private double [][] N1=null, N2=null;
    private double[][] R=new double[2][2];

    public MVDRNB(short[] x1, short[] x2){
        super(x1,x2);
        N1=new double[freq_time.getM()/2+1][40];
        N2=new double[freq_time.getM()/2+1][40];
    }
    
    public MVDRNB(short[] x1, short[] x2, int Freq, int Lframe, int Ltransf) 
            throws EnhproNB.InvalidFrequencyException, EnhproNB.InvalidParametersException{
        super(x1,x2,Freq,Lframe,Ltransf);
        N1=new double[freq_time.getM()/2+1][40];
        N2=new double[freq_time.getM()/2+1][40];
    }
    
    public short[] processing(){
        try{
            initProcessing();
            for (int i=0; i<freq_time.getM()/2+1; i++){
                if (freq_time.Xf1[0].length>40){
                    for (int j=0; j<20; j++){
                        N1[i][j]=freq_time.Xf1[i][j].module();
                        N2[i][j]=freq_time.Xf2[i][j].module();
                        N1[i][j+20]=freq_time.Xf1[i][freq_time.Xf1[0].length-20+j].module();
                        N2[i][j+20]=freq_time.Xf2[i][freq_time.Xf1[0].length-20+j].module();
                    }
                } else{
                    for (int j=0; j<freq_time.Xf1[0].length; j++){
                        N1[i][j]=freq_time.Xf1[i][j].module();
                        N2[i][j]=freq_time.Xf2[i][j].module();
                    }
                }
                R[0][0]=meanCorr(N1[i],N1[i]);
                R[0][1]=meanCorr(N1[i],N2[i]);
                R[1][0]=R[0][1];
                R[1][1]=meanCorr(N2[i],N2[i]);
                
                double aux1=(R[1][1]*at[i][0]-R[0][1]*at[i][1])/(R[0][0]*R[1][1]-R[0][1]*R[1][0]+1e-15);
                double aux2=(-R[1][0]*at[i][0]+R[0][0]*at[i][1])/(R[0][0]*R[1][1]-R[0][1]*R[1][0]+1e-15);
                w[i][0]=new Complex(aux1/(aux1*at[i][0]+aux2*at[i][1]),0);
                w[i][1]=new Complex(aux2/(aux1*at[i][0]+aux2*at[i][1]),0);
                for (int k=0; k<freq_time.Xf1[0].length; k++){
                    Xf[i][k]=w[i][0].conjugated().product(freq_time.Xf1[i][k].module()).addition(w[i][1].conjugated().product(freq_time.Xf2[i][k].module()));
                }
            }

            return obtainXrec();
        } catch (java.lang.NegativeArraySizeException e){
            System.out.println(e.toString());
            return null;
        }
    }     
}
