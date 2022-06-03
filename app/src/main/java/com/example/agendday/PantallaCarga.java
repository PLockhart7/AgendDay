package com.example.agendday;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PantallaCarga extends AppCompatActivity {

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_carga);

        firebaseAuth = FirebaseAuth.getInstance();

        int Tiempo = 3000;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
               /*startActivity(new Intent(PantallaCarga.this, MainActivity.class));
               finish();*/
                verificarUsuario();
            }
        },Tiempo);
    }

    //si ya te has logeado entras directp en la aplicación sin volver a logearte
    private void verificarUsuario(){
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if(firebaseUser == null){
            startActivity(new Intent(PantallaCarga.this, MainActivity.class));
            finish();
        }else {
            startActivity(new Intent(PantallaCarga.this, MenuPrincipal.class));
            finish();
        }
    }
}