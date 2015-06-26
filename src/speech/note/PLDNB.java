package speech.note;

/**
 *
 * @author Miguel
 */
public class PLDNB extends PowerLevelNB{
    private final double a2, a3, minth, maxth, dmic, c, gamma;
    
    public PLDNB(short[] x1, short[] x2, boolean noise){
        super(x1,x2,noise);
        c = 340;
        dmic = 0.1;
        a2 = 0.9;
        a3 = 0.8;
        maxth = 0.8;
        minth = 0.2;
        gamma=4;
    }
    
    public PLDNB(short[] x1, short[] x2, boolean noise, int Freq, int Lframe, int Ltransf) 
            throws EnhproNB.InvalidFrequencyException, EnhproNB.InvalidParametersException{
        super(x1,x2,noise,Freq,Lframe,Ltransf);
        c = 340;
        dmic = 0.1;
        a2 = 0.9;
        a3 = 0.8;
        maxth = 0.8;
        minth = 0.2;
        gamma=4;
    }
    
    public short[] processing(){
        initProcessing();
        final double[] Coh=new double[freq_time.getM()/2+1];
        final double[] f=new double[freq_time.getM()/2+1];
        double[][] PSD12=new double [freq_time.getM()/2+1][freq_time.Xf1[0].length];
        double[][] PSDr=new double [freq_time.getM()/2+1][freq_time.Xf1[0].length];
        double[][] PLDNE=new double [freq_time.getM()/2+1][freq_time.Xf1[0].length];
        double[][] H12=new double [freq_time.getM()/2+1][freq_time.Xf1[0].length];
        G=new double [freq_time.getM()][freq_time.Xf1[0].length];
        for (int i=0; i<freq_time.getM()/2+1; i++){
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
            for (int j=1; j<freq_time.Xf1[1].length; j++){
                PSD1[i][j]=PSD1[i][j-1]*a1+(1-a1)*freq_time.Xf1[i][j].module()*freq_time.Xf1[i][j].module();
                PSD2[i][j]=PSD2[i][j-1]*a1+(1-a1)*freq_time.Xf2[i][j].module()*freq_time.Xf2[i][j].module();
                PSD12[i][j]=PSD12[i][j-1]*a1+(1-a1)*freq_time.Xf1[i][j].module()*freq_time.Xf2[i][j].module();
                PLDNE[i][j]=Math.abs((PSD1[i][j]-PSD2[i][j])/(PSD1[i][j]+PSD2[i][j]));
                if (PLDNE[i][j]<minth){
                    PSDr[i][j]=a2*PSDr[i][j-1]+(1-a2)*freq_time.Xf1[i][j].module()*freq_time.Xf1[i][j].module();
                } else if (PLDNE[i][j]>maxth){
                    PSDr[i][j]=PSDr[i][j-1];
                } else{
                   PSDr[i][j]=a3*PSDr[i][j-1]+(1-a3)*freq_time.Xf2[i][j].module()*freq_time.Xf2[i][j].module();
                }
                H12[i][j]=Math.pow(((PSD12[i][j]-Coh[i]*PSDr[i][j])/(PSD1[i][j]-PSDr[i][j])),2);
                G[i][j]=max(PSD1[i][j]-PSD2[i][j],0.0)/(max(PSD1[i][j]-PSD2[i][j],0.0)+gamma*(1-H12[i][j])*PSDr[i][j]);
                if (G[i][j]<1e-15){
                    G[i][j]=1e-15;
                } else if (G[i][j]>1.0){
                    G[i][j]=1.0;
                }
            }
        }
        return obtainXrec();
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