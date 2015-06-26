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
