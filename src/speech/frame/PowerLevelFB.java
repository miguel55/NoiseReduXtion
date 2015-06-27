/*  Copyright 2015 Miguel Molina

    PowerLevelFB is part of the package speech.frame of library NoiseReduXtion.

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

import speech.common.MusFilter;
import speech.common.Complex;

/**
 *
 * @author Miguel
 */
public abstract class PowerLevelFB extends NoiseReductionFB{
    final double a1;
    final boolean WN;
    double[][] PSD1, PSD2, G;
    
    PowerLevelFB(boolean noise){
        super();
        a1 = 0.9;
        WN=noise;
        PSD1 = new double[freq_time.getM()/2+1][3];
        PSD2 = new double[freq_time.getM()/2+1][3];
    }
    
    PowerLevelFB(boolean noise, int Freq, int Lframe, int Ltransf) 
            throws EnhproFB.InvalidFrequencyException, EnhproFB.InvalidParametersException{
        super(Freq, Lframe, Ltransf);
        a1 = 0.9;
        WN=noise;
        PSD1 = new double[freq_time.getM()/2+1][3];
        PSD2 = new double[freq_time.getM()/2+1][3];
    }
    
    @Override
    void initProcessing(short[] x1, short x2[]){
        super.initProcessing(x1, x2);
        G = new double[freq_time.getM()][freq_time.Xf1[0].length];
    }
    
    short[] obtainXrec(boolean end){
        if (WN==false){
            rec(G, freq_time.Xf1);
        } else{
            MusFilter r=new MusFilter();
            double[][] G_new=r.mus_filter(freq_time.Xf1,G);
            rec(G_new, freq_time.Xf1);
        }
        freq_time.postProcessing(Xf,end);
        return freq_time.Xrec;
    }
    
    private void rec(double[][] Gan, Complex[][] Sfreq){
        for (int i=1; i<Gan.length/2; i++){
            if (freq_time.Xf1[0].length==1){
                System.arraycopy(Gan[Gan.length/2-i], 0, Gan[Gan.length/2+i], 0, 1);
            } else{
                System.arraycopy(Gan[Gan.length/2-i], 0, Gan[Gan.length/2+i], 0, 2);
            }
        }
        for (int i=0; i<Sfreq.length; i++){
            for (int j=0; j<Sfreq[0].length; j++){
                Xf[i][j]=Sfreq[i][j].product(Gan[i][j]);
            }
        }
    }
}
