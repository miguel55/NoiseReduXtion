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
