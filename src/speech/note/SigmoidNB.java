/*  Copyright 2015 Miguel Molina

    SigmoidNB is part of the package speech.note of library NoiseReduXtion.

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

/**
 *
 * @author Miguel
 */
public class SigmoidNB extends PowerLevelNB{
    private final double a,c;
    
    public SigmoidNB(short[] x1, short[] x2, boolean noise){
        super(x1,x2,noise);
        a=2;
        c=1.9;
    }
    
    public SigmoidNB(short[] x1, short[] x2, boolean noise, int Freq, int Lframe, int Ltransf) 
            throws EnhproNB.InvalidFrequencyException, EnhproNB.InvalidParametersException{
        super(x1,x2,noise,Freq,Lframe,Ltransf);
        a=2;
        c=1.9;
    }
    
    public short[] processing(){
        initProcessing();
        for (int i=0; i<freq_time.getM()/2+1; i++){
            PSD1[i][0]=freq_time.Xf1[i][0].module()*freq_time.Xf1[i][0].module();
            PSD2[i][0]=freq_time.Xf2[i][0].module()*freq_time.Xf2[i][0].module();
            G[i][0]=1.0/(1+Math.exp(-a*(PSD1[i][0]/PSD2[i][0]-c)));
            for (int j=1; j<freq_time.Xf1[1].length; j++){
                PSD1[i][j]=PSD1[i][j-1]*a1+(1-a1)*freq_time.Xf1[i][j].module()*freq_time.Xf1[i][j].module();
                PSD2[i][j]=PSD2[i][j-1]*a1+(1-a1)*freq_time.Xf2[i][j].module()*freq_time.Xf2[i][j].module();
                G[i][j]=1.0/(1+Math.exp(-a*(PSD1[i][j]/PSD2[i][j]-c)));
            }
        }
        return obtainXrec();
    }
    
}


