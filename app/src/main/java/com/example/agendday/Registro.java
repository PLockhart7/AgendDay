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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Registro extends AppCompatActivity {

    EditText nombreRegistro,passRegistro,correoRegistro,confirmarPassRegistro;
    Button registroUsuario;
    TextView tengoCuenta;
    String nombre="";
    String correo="";
    String pass="";
    String confirmarPass="";

    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        //volver atras
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Registrar");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        nombreRegistro = findViewById(R.id.nombreRegistro);
        passRegistro = findViewById(R.id.passRegistro);
        correoRegistro = findViewById(R.id.correoRegistro);
        confirmarPassRegistro = findViewById(R.id.confirmarPassRegistro);
        registroUsuario = findViewById(R.id.registrarUsuario);
        tengoCuenta = findViewById(R.id.tengoCuenta);

        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(Registro.this);
        progressDialog.setTitle("Espere por favor");
        progressDialog.setCanceledOnTouchOutside(false);

        registroUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validarDatos();
            }
        });

        tengoCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Registro.this, Login.class));
            }
        });

    }

    private void validarDatos() {
        nombre = nombreRegistro.getText().toString();
        correo = correoRegistro.getText().toString();
        pass = passRegistro.getText().toString();
        confirmarPass = confirmarPassRegistro.getText().toString();

        if(TextUtils.isEmpty(nombre)){
            Toast.makeText(this, "Pon el nombre", Toast.LENGTH_SHORT).show();
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()){
            Toast.makeText(this, "Pon el Correo", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(pass)){
            Toast.makeText(this, "Necesitas contraseña", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(confirmarPass)){
            Toast.makeText(this, "Confirme la Contraseña", Toast.LENGTH_SHORT).show();
        }
        else if(!pass.equals(confirmarPass)){
            Toast.makeText(this, "Las contraseñas no son iguales", Toast.LENGTH_SHORT).show();
        }
        else{
           crearCuenta();
        }
    }

    private void crearCuenta() {
        progressDialog.setMessage("Creando la cuenta");
        progressDialog.show();

        //crear usuario Firebase
        firebaseAuth.createUserWithEmailAndPassword(correo,pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                guardarInfo();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(Registro.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void guardarInfo() {
        progressDialog.setTitle("Guardando su información");
        progressDialog.dismiss();

        //obtener identidad de usuario actual
        String uid = firebaseAuth.getUid();

        HashMap<String, String> Datos = new HashMap<>();
        Datos.put("uid", uid);
        Datos.put("correo", correo);
        Datos.put("nombre", nombre);
        Datos.put("password", pass);
        Datos.put("apellidos", "");
        Datos.put("edad", "");
        Datos.put("telefono", "");
        Datos.put("domicilio", "");
        Datos.put("imagenPerfil","");
        Datos.put("fechaNacimiento","");

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("usuarios");
        databaseReference.child(uid).setValue(Datos).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                progressDialog.dismiss();
                Toast.makeText(Registro.this, "Cuenta Creada", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Registro.this,MenuPrincipal.class));
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(Registro.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}