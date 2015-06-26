package speech.frame;

/**
 *
 * @author Miguel
 */
public class PLDFB extends PowerLevelFB{
    private final double a2, a3, minth, maxth, dmic, c, gamma;
    private double[] Coh, f;
    private double[][] PSD12, PSDr, PLDNE, H12;            
    
    public PLDFB(boolean noise){
        super(noise);
        c = 340;
        dmic = 0.1;
        a2 = 0.9;
        a3 = 0.8;
        minth = 0.2;
        maxth = 0.8;
        gamma=4;
        Coh = new double[freq_time.getM() / 2 + 1];
        f = new double[freq_time.getM() / 2 + 1];
        PSD12 = new double[freq_time.getM() / 2 + 1][3];
        PSDr = new double[freq_time.getM() / 2 + 1][3];
    }
    
    public PLDFB(boolean noise, int Freq, int Lframe, int Ltransf, double d) 
            throws EnhproFB.InvalidFrequencyException, EnhproFB.InvalidParametersException{
        super(noise, Freq, Lframe, Ltransf);
        c = 340;
        dmic = d;
        a2 = 0.9;
        a3 = 0.8;
        minth = 0.2;
        maxth = 0.8;
        gamma=4;
        Coh = new double[freq_time.getM() / 2 + 1];
        f = new double[freq_time.getM() / 2 + 1];
        PSD12 = new double[freq_time.getM() / 2 + 1][3];
        PSDr = new double[freq_time.getM() / 2 + 1][3];
    }
    
    public short[] processing(short[] x1, short[] x2, boolean end){
        try{
            initProcessing(x1,x2);
            PLDNE = new double[freq_time.getM()/2+1][freq_time.Xf1[0].length];
            H12 = new double[freq_time.getM()/2+1][freq_time.Xf1[0].length];
            for (int i=0; i<freq_time.getM()/2+1; i++){
                if (freq_time.Xf1[0].length!=1){
                    for (int j=1; j<3; j++){
                        PSD1[i][j]=PSD1[i][j-1]*a1+(1-a1)*freq_time.Xf1[i][j-1].module()*freq_time.Xf1[i][j-1].module();
                        PSD2[i][j]=PSD2[i][j-1]*a1+(1-a1)*freq_time.Xf2[i][j-1].module()*freq_time.Xf2[i][j-1].module();
                        PSD12[i][j]=PSD12[i][j-1]*a1+(1-a1)*freq_time.Xf1[i][j-1].module()*freq_time.Xf2[i][j-1].module();
                        PLDNE[i][j-1]=Math.abs((PSD1[i][j]-PSD2[i][j])/(PSD1[i][j]+PSD2[i][j]));
                        if (PLDNE[i][j-1]<minth){
                            PSDr[i][j]=a2*PSDr[i][j-1]+(1-a2)*freq_time.Xf1[i][j-1].module()*freq_time.Xf1[i][j-1].module();
                        } else if (PLDNE[i][j-1]>maxth){
                            PSDr[i][j]=PSDr[i][j-1];
                        } else{
                            PSDr[i][j]=a3*PSDr[i][j-1]+(1-a3)*freq_time.Xf2[i][j-1].module()*freq_time.Xf2[i][j-1].module();
                        }
                        H12[i][j-1]=Math.pow((PSD12[i][j]-Coh[i]*PSDr[i][j])/(PSD1[i][j]-PSDr[i][j]),2);
                        G[i][j-1]=max(PSD1[i][j]-PSD2[i][j],0)/(max(PSD1[i][j]-PSD2[i][j],0)+gamma*(1-H12[i][j-1])*PSDr[i][j]);
                        System.out.println(G[i][j-1]);
                        if (1e-15>G[i][j-1]){
                            G[i][j-1]=1e-15;
                        } else if (1.0<G[i][j-1]){
                            G[i][j-1]=1.0;
                        }
                        if (j==2){
                            PSD1[i][0]=PSD1[i][j];
                            PSD2[i][0]=PSD2[i][j];
                            PSD12[i][0]=PSD12[i][j];
                            PSDr[i][0]=PSDr[i][j];
                        }
                    }
                }else{
                    PSD1[i][0]=freq_time.Xf1[i][0].module()*freq_time.Xf1[i][0].module();
                    PSD2[i][0]=freq_time.Xf2[i][0].module()*freq_time.Xf2[i][0].module();
                    PSD12[i][0]=freq_time.Xf1[i][0].module()*freq_time.Xf2[i][0].module();
                    PLDNE[i][0]=Math.abs((PSD1[i][0]-PSD2[i][0])/(PSD1[i][0]+PSD2[i][0]));
                    PSDr[i][0]=PSD2[i][0];
                    f[i]=freq_time.getFs()/(2*(freq_time.getM()/2+1.0))*i+freq_time.getFs()/2*1/(2*(freq_time.getM()/2+1.0));
                    Coh[i]=sinc(2*Math.PI*dmic*f[i]/c);
                    H12[i][0]=Math.pow((PSD12[i][0]-Coh[i]*PSDr[i][0])/(PSD1[i][0]-PSDr[i][0]),2);
                    G[i][0]=max(PSD1[i][0]-PSD2[i][0],0)/(max(PSD1[i][0]-PSD2[i][0],0)+gamma*(1-H12[i][0])*PSDr[i][0]);
                    if (G[i][0]<1e-15){
                        G[i][0]=1e-15;
                    } else if (G[i][0]>1.0){
                        G[i][0]=1.0;
                    }
                }
            }
            return obtainXrec(end);

        } catch (java.lang.NegativeArraySizeException e){
            System.out.println(e.toString());
            return null;
        }
    }
    
    private double sinc(double x){
        if (x==0){
            return 1;
        } else{
            return (Math.sin(Math.PI*x)/(Math.PI*x));
        }
    }
    
    private double max(double n1, double n2){
        if (n1>n2){
            return n1;
        } else{
            return n2;
        }
    }

}
