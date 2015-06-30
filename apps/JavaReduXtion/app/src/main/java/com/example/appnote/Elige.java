/*  Copyright 2015 Miguel Molina

    Elige is part of JavaReduXtion app.

    JavaReduXtion is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License version 3 as published
    by the Free Software Foundation.

    JavaReduXtion is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with JavaReduXtion.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.example.appnote;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;


public class Elige extends Activity implements View.OnClickListener{

    private RadioGroup rg;
    private Button btnCont;
    private String alg;
    private String mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elige);
        mode=getIntent().getStringExtra("mode");

        rg = (RadioGroup) findViewById(R.id.radioG);
        btnCont = (Button)findViewById(R.id.buttonCont);

        btnCont.setOnClickListener(this);
        btnCont.setEnabled(false);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
                if (checkedId == R.id.r2) {
                    alg = getString(R.string.alg2);
                    btnCont.setEnabled(true);
                } else if (checkedId == R.id.r3) {
                    alg = getString(R.string.alg3);
                    btnCont.setEnabled(true);
                } else if (checkedId == R.id.r4) {
                    alg = getString(R.string.alg4);
                    btnCont.setEnabled(true);
                } else if (checkedId == R.id.r5) {
                    alg = getString(R.string.alg5);
                    btnCont.setEnabled(true);
                } else if (checkedId == R.id.r6) {
                    alg = getString(R.string.alg6);
                    btnCont.setEnabled(true);
                } else if (checkedId == R.id.r7) {
                    alg = getString(R.string.alg7);
                    btnCont.setEnabled(true);
                }

            }

        });
    }

    public void onClick(View v){

        if(v==btnCont) {
            Intent act = new Intent(this, Graba.class);
            act.putExtra("mode", mode);
            act.putExtra("alg", alg);
            startActivity(act);
        }
    }

}
