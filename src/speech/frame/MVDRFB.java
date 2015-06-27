/*  Copyright 2015 Miguel Molina

    MVDRFB is part of the package speech.frame of library NoiseReduXtion.

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

import speech.common.Complex;
import java.util.Arrays;

/**
 *
 * @author Miguel
 */
public class MVDRFB extends FilteringFB{
    private int count;
    private double [][] N1, N2;
    private double[][] R=new double[2][2];

    public MVDRFB(){
        super();
        count=0;
        N1=new double[freq_time.getM()/2+1][1];
        N2=new double[freq_time.getM()/2+1][1];
    }
    
    public MVDRFB(int Freq, int Lframe, int Ltransf) 
            throws EnhproFB.InvalidFrequencyException, EnhproFB.InvalidParametersException{
        super(Freq, Lframe, Ltransf);
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
                    
                double aux1=(R[1][1]*at[i][0]-R[0][1]*at[i][1])/(R[0][0]*R[1][1]-R[0][1]*R[1][0]+1e-15);
                double aux2=(-R[1][0]*at[i][0]+R[0][0]*at[i][1])/(R[0][0]*R[1][1]-R[0][1]*R[1][0]+1e-15);
                w[i][0]=new Complex(aux1/(aux1*at[i][0]+aux2*at[i][1]),0);
                w[i][1]=new Complex(aux2/(aux1*at[i][0]+aux2*at[i][1]),0);
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
