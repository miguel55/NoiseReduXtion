package speech.frame;

/**
 *
 * @author Miguel
 */
public class SigmoidFB extends PowerLevelFB{
    private final double a, c;
    
    public SigmoidFB(boolean noise){
        super(noise);
        a=2;
        c=1.9;
    }
    
    public SigmoidFB(boolean noise, int Freq, int Lframe, int Ltransf) 
            throws EnhproFB.InvalidFrequencyException, EnhproFB.InvalidParametersException{
        super(noise, Freq, Lframe, Ltransf);
        a=2;
        c=1.9;
    }
    
    public short[] processing(short[] x1, short[] x2, boolean end){
        try{
            initProcessing(x1,x2);
            for (int i=0; i<freq_time.getM()/2+1; i++){
                if (freq_time.Xf1[0].length!=1){
                    for (int j=1; j<3; j++){
                        PSD1[i][j]=PSD1[i][j-1]*a1+(1-a1)*freq_time.Xf1[i][j-1].module()*freq_time.Xf1[i][j-1].module();
                        PSD2[i][j]=PSD2[i][j-1]*a1+(1-a1)*freq_time.Xf2[i][j-1].module()*freq_time.Xf2[i][j-1].module();
                        G[i][j-1]=1.0/(1+Math.exp(-a*(PSD1[i][j]/PSD2[i][j]-c)));
                        if (j==2){
                            PSD1[i][0]=PSD1[i][j];
                            PSD2[i][0]=PSD2[i][j];
                        }
                    }
                } else{
                    PSD1[i][0]=freq_time.Xf1[i][0].module()*freq_time.Xf1[i][0].module();
                    PSD2[i][0]=freq_time.Xf2[i][0].module()*freq_time.Xf2[i][0].module();
                    G[i][0]=1.0/(1+Math.exp(-a*(PSD1[i][0]/PSD2[i][0]-c)));
                } 
            }
            return obtainXrec(end);
        } catch (java.lang.NegativeArraySizeException e){
            System.out.println(e.toString());
            return null;
        }
    }
}