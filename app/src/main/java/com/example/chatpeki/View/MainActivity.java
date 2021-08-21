package com.example.chatpeki.View;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.chatpeki.R;

public class MainActivity extends AppCompatActivity {
    private Button kayit_ol,hesabim_var;

    public void init(){
        kayit_ol=(Button)findViewById(R.id.kayit_Ol);
        hesabim_var=(Button)findViewById(R.id.hesabim_Var);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        hesabim_var.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentLogin=new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intentLogin);

            }
        });
        kayit_ol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentKayıt_ol=new Intent(MainActivity.this,Kayit_olActivity.class);
                startActivity(intentKayıt_ol);

            }
        });

    }
}
