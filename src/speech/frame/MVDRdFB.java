package speech.frame;

import speech.common.Complex;
import java.util.Arrays;

/**
 *
 * @author Miguel
 */
public class MVDRdFB extends FilteringFB{
    private int count;
    private double [][] N1, N2;
    private double[][] R=new double[2][2];
    
    public MVDRdFB(){
        super();
        count=0;
        N1=new double[freq_time.getM()/2+1][1];
        N2=new double[freq_time.getM()/2+1][1];
    }
    
    public short[] processing(short[] x1, short[] x2, boolean end) throws ArithmeticException{
        try{
            int z;
            initProcessing(x1,x2);
            if (count==0){
                z=3;
            } else{
                z=4;
            }
            for (int i=0; i<freq_time.getM()/2+1; i++){
                if (count<30){
                    for (int j=0; j<freq_time.Xf1[0].length; j++){
                        N1[i][count+j]=freq_time.Xf1[i][j].module();
                        N2[i][count+j]=freq_time.Xf2[i][j].module();
                    }
                    N1[i]=Arrays.copyOf(N1[i],count+z);
                    N2[i]=Arrays.copyOf(N2[i],count+z);
                }        
                    R[0][0]=meanCorr(N1[i],N1[i],count+z-2);
                    R[0][1]=meanCorr(N1[i],N2[i],count+z-2);
                    R[1][0]=R[0][1];
                    R[1][1]=meanCorr(N2[i],N2[i],count+z-2);
                Complex aux1=(d[i][0].product(R[1][1]).subtraction(d[i][1].product(R[0][1]))).division(R[0][0]*R[1][1]-R[0][1]*R[1][0]+1e-15);
                Complex aux2=(d[i][1].product(R[0][0]).subtraction(d[i][0].product(R[1][0]))).division(R[0][0]*R[1][1]-R[0][1]*R[1][0]+1e-15);
                w[i][0]=aux1.division(aux1.product(d[i][0]).addition(aux2.product(d[i][1].conjugated())));
                w[i][1]=aux2.division(aux1.product(d[i][0]).addition(aux2.product(d[i][1].conjugated())));
                for (int k=0; k<freq_time.Xf1[0].length; k++){
                    Xf[i][k]=w[i][0].conjugated().product(freq_time.Xf1[i][k].module()).addition(w[i][1].conjugated().product(freq_time.Xf2[i][k].module()));
                }
            }
            if (count<30)
                if (count!=0)
                    count+=2;
                else  
                    count+=1;
            return obtainXrec(end);
        } catch (ArithmeticException e){
            System.out.println(e.toString());
            return null;
        }
    }
           
}
