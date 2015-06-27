/*  Copyright 2015 Miguel Molina

    NoiseReductionFB is part of the package speech.frame of library 
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
package speech.frame;

import speech.common.Complex;

/**
 *
 * @author Miguel
 */
public abstract class NoiseReductionFB {
    EnhproFB freq_time;
    Complex[][] Xf;
    
    NoiseReductionFB(){
        freq_time=new EnhproFB();
    }
    
    NoiseReductionFB(int Freq, int Lframe, int Ltransf) throws 
            EnhproFB.InvalidFrequencyException, EnhproFB.InvalidParametersException{
        freq_time=new EnhproFB(Freq, Lframe, Ltransf);
    }
    
    void initProcessing(short[] x1, short x2[]){
        freq_time.preProcessing(x1,x2);
        Xf = new Complex[freq_time.getM()][freq_time.Xf1[0].length];
    }
}
