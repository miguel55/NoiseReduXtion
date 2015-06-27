/*  Copyright 2015 Miguel Molina

    FilteringNB is part of the package speech.note of library NoiseReduXtion.

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
public abstract class FilteringNB extends NoiseReductionNB{
    static double[][] at={{1.0,0.166666666666667},{1.0,0.167208993350234},
        {1.0,0.170915964320394},{1.0,0.184029770176026},{1.0,0.197560544228307},
        {1.0,0.190840527999757},{1.0,0.192047432177809},{1.0,0.183834725979586},
        {1.0,0.167650080266274},{1.0,0.150981806627262},{1.0,0.153266878253944},
        {1.0,0.166268180535902},{1.0,0.162614438565990},{1.0,0.153961663383010},
        {1.0,0.159909494794623},{1.0,0.172945154929599},{1.0,0.183481793364247},
        {1.0,0.192731193729044},{1.0,0.203016661932243},{1.0,0.197092159672005},
        {1.0,0.179377461668000},{1.0,0.165449102310079},{1.0,0.160821557698255},
        {1.0,0.153255514371948},{1.0,0.144576168398710},{1.0,0.137317154894612},
        {1.0,0.124396598965920},{1.0,0.120180549364993},{1.0,0.130791931187244},
        {1.0,0.145571778826412},{1.0,0.141218906987489},{1.0,0.130028627154861},
        {1.0,0.121440927307679},{1.0,0.132815375547346},{1.0,0.143066947079109},
        {1.0,0.131471825432247},{1.0,0.117397440935886},{1.0,0.111905263697132},
        {1.0,0.110213016596265},{1.0,0.127643885915046},{1.0,0.138729644309074},
        {1.0,0.130625208496672},{1.0,0.114396727972234},{1.0,0.110691210191480},
        {1.0,0.118684178909335},{1.0,0.122772233372909},{1.0,0.113675240471541},
        {1.0,0.108648074500687},{1.0,0.110724755365175},{1.0,0.122984352085588},
        {1.0,0.130826843452480},{1.0,0.139556180130904},{1.0,0.139227974360512},
        {1.0,0.136643615689669},{1.0,0.143440655916181},{1.0,0.162632621961658},
        {1.0,0.173751752297178},{1.0,0.169735515520989},{1.0,0.167521819400617},
        {1.0,0.170910309135608},{1.0,0.172787976694475},{1.0,0.174939638177146},
        {1.0,0.174570230896301},{1.0,0.172047231508049},{1.0,0.170871531543352},
        {1.0,0.171196561186246},{1.0,0.176694714805526},{1.0,0.181037533055351},
        {1.0,0.180732402893754},{1.0,0.186833350272195},{1.0,0.192893980592318},
        {1.0,0.198091186356784},{1.0,0.208239907947187},{1.0,0.207763558624124},
        {1.0,0.228140288351793},{1.0,0.222165164912611},{1.0,0.233236867676543},
        {1.0,0.273290432276087},{1.0,0.319764138886490},{1.0,0.348190484177468},
        {1.0,0.366762202424264},{1.0,0.379465510585720},{1.0,0.398219236868218},
        {1.0,0.363586491797512},{1.0,0.296467242160284},{1.0,0.239208606172832},
        {1.0,0.198111295880466},{1.0,0.171560274047292},{1.0,0.179932911752786},
        {1.0,0.169551281266584},{1.0,0.148082907254966},{1.0,0.125722490449906},
        {1.0,0.115342201429619},{1.0,0.115831738573581},{1.0,0.123046748618180},
        {1.0,0.112279124052163},{1.0,0.097557477967914},{1.0,0.101902063537745},
        {1.0,0.108783040397567},{1.0,0.105969731626750},{1.0,0.110582462463094},
        {1.0,0.112133654846756},{1.0,0.111199004300049},{1.0,0.105199375297250},
        {1.0,0.105827808331101},{1.0,0.115891487701435},{1.0,0.123392112224695},
        {1.0,0.118180342007004},{1.0,0.105346550520080},{1.0,0.104958382678547},
        {1.0,0.118829559127789},{1.0,0.139848501947333},{1.0,0.145997498258241},
        {1.0,0.152094009536106},{1.0,0.151946623414071},{1.0,0.147803356150199},
        {1.0,0.163498578111671},{1.0,0.186700500706312},{1.0,0.200531281164672},
        {1.0,0.208173632730702},{1.0,0.208971812839067},{1.0,0.210309300768061},
        {1.0,0.211483452596959},{1.0,0.214232636018993},{1.0,0.219937003996874},
        {1.0,0.209469799170342},{1.0,0.209658373493111},{1.0,0.211205851734264},
        {1.0,0.198113207547170}};
    private static double del=2.27451171875;
    final Complex[][] d;
    final double[] f;
    Complex[][] w;
    private double[][] phases;
    
    FilteringNB(short[] x1, short[] x2){
        super(x1,x2);
        w=new Complex[freq_time.getM()/2+1][2];
        d=new Complex[freq_time.getM()/2+1][2];
        f=new double[freq_time.getM()/2+1];
        for (int i=0; i<freq_time.getM()/2+1; i++){
            f[i]=freq_time.getFs()/(2*(freq_time.getM()/2+1.0))*i+freq_time.getFs()/2*1/(2*(freq_time.getM()/2+1.0));
            d[i][0]=new Complex(at[i][0],0);
            d[i][1]=new Complex(at[i][1]*Math.cos(-2*Math.PI*del*f[i]/freq_time.getFs()),
                    at[i][1]*Math.sin(-2*Math.PI*del*f[i]/freq_time.getFs()));
        }
    }
    
    FilteringNB(short[] x1, short[] x2, int Freq, int Lframe, int Ltransf) 
            throws EnhproNB.InvalidFrequencyException, EnhproNB.InvalidParametersException{
        super(x1,x2,Freq,Lframe,Ltransf);
        w=new Complex[freq_time.getM()/2+1][2];
        d=new Complex[freq_time.getM()/2+1][2];
        f=new double[freq_time.getM()/2+1];
        for (int i=0; i<freq_time.getM()/2+1; i++){
            f[i]=freq_time.getFs()/(2*(freq_time.getM()/2+1.0))*i+freq_time.getFs()/2*1/(2*(freq_time.getM()/2+1.0));
            d[i][0]=new Complex(at[i][0],0);
            d[i][1]=new Complex(at[i][1]*Math.cos(-2*Math.PI*del*f[i]/freq_time.getFs()),
                    at[i][1]*Math.sin(-2*Math.PI*del*f[i]/freq_time.getFs()));
        }
    }
    
    @Override
    void initProcessing(){
        super.initProcessing();
        phases=new double[freq_time.getM()][freq_time.Xf1[0].length];
    }
    
    short[] obtainXrec(){
        for (int i=1; i<freq_time.getM()/2; i++){
                System.arraycopy(Xf[freq_time.getM()/2-i], 0, Xf[freq_time.getM()/2+i], 0, Xf[1].length);
            }
            // Add phase from first-channel signal
            for (int i=0; i<freq_time.getM(); i++){
                for (int j=0; j<freq_time.Xf1[1].length; j++){
                    phases[i][j]=freq_time.Xf1[i][j].phase();
                    Xf[i][j]=Xf[i][j].product(new Complex(Math.cos(phases[i][j]),Math.sin(phases[i][j])));
                }
            }
            freq_time.postProcessing(Xf);
            return freq_time.Xrec;
    }
    
    public static void changeAtFactor(double[][] newAt){
        at=newAt;
    }
    
    public static void changeDel(double newDel){
        del=newDel;
    }
    
    double meanCorr (double[] v1, double[] v2){
        double suma=0.0;
        for (int i=0; i<v1.length; i++){
            suma=suma+v1[i]*v2[i];
        }
        return suma;
    }  
}

