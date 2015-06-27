/*  Copyright 2015 Miguel Molina

    NoiseReductionNB is part of the package speech.note of library 
    NoiseReduXtion.

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

/**
 *
 * @author Miguel
 */
public abstract class NoiseReductionNB {
    EnhproNB freq_time;
    Complex[][] Xf;
    
    NoiseReductionNB(short[] x1, short[] x2){
        freq_time=new EnhproNB(x1,x2);
    }
    
    NoiseReductionNB(short[] x1, short[] x2, int Freq, int Lframe, int Ltransf) 
            throws EnhproNB.InvalidFrequencyException, EnhproNB.InvalidParametersException{
        freq_time=new EnhproNB(x1,x2, Freq, Lframe, Ltransf);
    }
    
    void initProcessing(){
        freq_time.preProcessing();
        Xf=new Complex[freq_time.getM()][freq_time.Xf1[0].length];
    }
}
