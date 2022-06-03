package com.example.agendday;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    EditText txtCorreoLogin, txtPassLogin;
    Button btnIniciarLogin;
    TextView txtUsuarioNuevo;

    ProgressDialog progressDialog;

    FirebaseAuth firebaseAuth;

    String correo="";
    String pass="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Login");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        txtCorreoLogin = findViewById(R.id.txtCorreoLogin);
        txtPassLogin = findViewById(R.id.txtPassLogin);
        btnIniciarLogin = findViewById(R.id.btnIniciarLogin);
        txtUsuarioNuevo = findViewById(R.id.txtUsuarioNuevo);

        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(Login.this);
        progressDialog.setTitle("Espere por favor");
        progressDialog.setCanceledOnTouchOutside(false);
        
        btnIniciarLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarDatos();
            }
        });

        txtUsuarioNuevo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Registro.class));
            }
        });

    }

    private void validarDatos() {
        correo = txtCorreoLogin.getText().toString();
        pass = txtPassLogin.getText().toString();
        
        if(!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            Toast.makeText(this, "El correo es incorrecto", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(pass)) {
            Toast.makeText(this, "Pon la contraseña", Toast.LENGTH_SHORT).show();
        }else {
            loginUsuario();
        }
    }

    private void loginUsuario() {
        progressDialog.setMessage("Iniciando sesión..");
        progressDialog.show();
        firebaseAuth.signInWithEmailAndPassword(correo,pass).addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task. isSuccessful()) {
                    progressDialog.dismiss();
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    startActivity(new Intent(Login.this, MenuPrincipal.class));
                    Toast.makeText(Login.this, "Bienvenid@: "+user.getEmail(), Toast.LENGTH_SHORT).show();
                    finish();
                }
                else {
                    progressDialog.dismiss();
                    Toast.makeText(Login.this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(Login.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}