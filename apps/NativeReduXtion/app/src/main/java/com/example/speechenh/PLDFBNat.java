/*  Copyright 2015 Miguel Molina

    PLDFBNat is part of native code NativeReduXtion app.

    NativeReduXtion is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License version 3 as published
    by the Free Software Foundation.

    NativeReduXtion is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with NativeReduXtion.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.example.speechenh;

/**
 * Created by Miguel on 07/06/2015.
 */
public abstract class PLDFBNat {

    public static native void PLDFB(boolean noise);

    public static native short[] processing(short x1[], short x2[], int end);
}
