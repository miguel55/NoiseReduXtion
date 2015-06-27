/*  Copyright 2015 Miguel Molina

    Complex is part of the package speech.common of library NoiseReduXtion.

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

package speech.common;

/**
 *
 * @author Miguel
 */
public class Complex {
    private final double real;
    private final double imag;
    
    public Complex(){
        real=0;
        imag=0;
    }
    
    public Complex(double real_part, double imag_part){
        real=real_part;
        imag=imag_part;
    }
    
    public double getReal(){
        return real;
    }
    
    public double getImag(){
        return imag;
    }
    
    
    public Complex conjugated(){
        return new Complex(real, -imag);
    }
    
    public Complex opposite(){
        return new Complex(-real, -imag);
    }
    
    public double module(){
        return Math.sqrt(real*real+imag*imag);
    }
    
    public double phase(){
        return Math.atan2(imag,real);
    }
    
    public Complex modToComp(double mod, double phase){
        return new Complex(mod*Math.cos(phase),mod*Math.sin(phase));
    }
    
    public Complex addition(Complex n){
        return new Complex(real+n.real, imag+n.imag);
    }  
    
    public Complex addition(double n){
        return new Complex(real+n, imag);
    } 
    
    public Complex subtraction(Complex n){
        return new Complex(real-n.real, imag-n.imag);
    }
    
    public Complex product(Complex n){
        return new Complex(real*n.real-imag*n.imag, real*n.imag+imag*n.real);
    }

    public Complex product(double n){
        return new Complex(real*n, imag*n);
    }
    
    public Complex division(Complex n) throws ArithmeticException{
        Complex div=new Complex();
        if(n.module()==0.0){
            throw new ArithmeticException("Divide by zero");
        } else{
            div=this.modToComp(this.module()/n.module(), this.phase()-n.phase());
        }

        return div;
    }
    
    public Complex division(double n) throws ArithmeticException{
        Complex div=null;
        if(n==0.0){
            throw new ArithmeticException("Divide by zero");
        } else{
            div=new Complex(real/n, imag/n);
        }

        return div;
    }
    
    @Override
    public String toString(){
        return real+"  "+imag+"i\n";
    }
   
}