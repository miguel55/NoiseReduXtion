/*  Copyright 2015 Miguel Molina

    PowerLevelNB is part of the package speech.note of library NoiseReduXtion.

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
import speech.common.MusFilter;

/**
 *
 * @author Miguel
 */
public abstract class PowerLevelNB extends NoiseReductionNB{
    final double a1;
    final boolean WN;
    double[][] PSD1, PSD2, G;
    
    PowerLevelNB(short[] x1, short[] x2, boolean noise){
        super(x1,x2);
        a1 = 0.9;
        WN=noise;
        PSD1 = new double[freq_time.getM()/2+1][freq_time.Xf1[0].length];
        PSD2 = new double[freq_time.getM()/2+1][freq_time.Xf1[0].length];
    }
    
    PowerLevelNB(short[] x1, short[] x2, boolean noise, int Freq, int Lframe, int Ltransf) 
            throws EnhproNB.InvalidFrequencyException, EnhproNB.InvalidParametersException{
        super(x1,x2,Freq,Lframe,Ltransf);
        a1 = 0.9;
        WN=noise;
        PSD1 = new double[freq_time.getM()/2+1][freq_time.Xf1[0].length];
        PSD2 = new double[freq_time.getM()/2+1][freq_time.Xf1[0].length];
    }
    
    @Override
    void initProcessing(){
        super.initProcessing();
        G = new double[freq_time.getM()][freq_time.Xf1[0].length];
    }
    
    short[] obtainXrec(){
        if (WN==false){
              rec(G, freq_time.Xf1);
        } else{
            MusFilter r=new MusFilter();
            double[][] G_new=r.mus_filter(freq_time.Xf1,G);
            rec(G_new, freq_time.Xf1);
        }
        freq_time.postProcessing(Xf);
        return freq_time.Xrec;
    }
    
    private void rec(double[][] Gan, Complex[][] Sfreq){
        for (int i=1; i<Gan.length/2; i++){
            System.arraycopy(Gan[Gan.length/2-i], 0, Gan[Gan.length/2+i], 0, Gan[0].length);
        }
        for (int i=0; i<Sfreq.length; i++){
            for (int j=0; j<Sfreq[0].length; j++){
                Xf[i][j]=Sfreq[i][j].product(Gan[i][j]);
            }
        }
    }
}