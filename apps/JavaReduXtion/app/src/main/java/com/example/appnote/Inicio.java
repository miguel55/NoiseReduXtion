/*  Copyright 2015 Miguel Molina

    Inicio is part of JavaReduXtion app.

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


public class Inicio extends Activity implements View.OnClickListener{
    private Button frame, note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        frame = (Button) findViewById(R.id.btnFrame);
        note = (Button) findViewById(R.id.btnNote);

        frame.setOnClickListener(this);
        note.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v==frame){
            Intent intent=new Intent(this,Elige.class);
            intent.putExtra("mode","frame");
            startActivity(intent);
        } else if (v==note){
            Intent intent=new Intent(this,Elige.class);
            intent.putExtra("mode","note");
            startActivity(intent);
        }
    }
}
