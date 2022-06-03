package com.example.agendday.ActualizarPass;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.agendday.Login;
import com.example.agendday.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ActualizarPassUsuario extends AppCompatActivity {

    TextView tvPassActual;

    EditText txtPassActual;
    EditText txtNuevaPass;
    EditText txtNuevaPassR;

    Button btnActualizarPass;

    DatabaseReference BD_Usuarios;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualizar_pass_usuario);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Cambiar contraseña");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        inicializarVariables();
        leerDato();

        btnActualizarPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String passActual = txtPassActual.getText().toString().trim();
                String passNueva = txtNuevaPass.getText().toString().trim();
                String passNuevaR = txtNuevaPassR.getText().toString().trim();

                if (passActual.equals("")){
                    txtPassActual.setError("Llene el campo");
                }
                else if (passNueva.equals("")){
                    txtNuevaPass.setError("Llene el campo");
                }
                else if (passNuevaR.equals("")){
                    txtNuevaPassR.setError("Llene el campo");
                }
                else if (!passNueva.equals(passNuevaR)){
                    Toast.makeText(ActualizarPassUsuario.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                }
                else if (passNueva.length()<6){
                    txtNuevaPass.setError("Debe ser igual o mayor de 6 caracteres");
                }else {
                    actualizar_Password(passActual, passNueva);
                }
            }
        });

    }

    private void actualizar_Password(String passActual, String passNueva) {
        progressDialog.show();
        progressDialog.setTitle("Actualizando");
        progressDialog.setMessage("Espere por favor");

        AuthCredential authCredential = EmailAuthProvider.getCredential(user.getEmail(), passActual);
        user.reauthenticate(authCredential)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        user.updatePassword(passNueva)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        progressDialog.dismiss();
                                        String passNueva = txtNuevaPass.getText().toString().trim();
                                        HashMap<String, Object> hashMap = new HashMap<>();
                                        hashMap.put("password", passNueva);
                                        BD_Usuarios = FirebaseDatabase.getInstance().getReference("usuarios");
                                        BD_Usuarios.child(user.getUid()).updateChildren(hashMap)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Toast.makeText(ActualizarPassUsuario.this, "Contraseña cambiada con éxito", Toast.LENGTH_SHORT).show();
                                                        firebaseAuth.signOut();
                                                        Intent intent = new Intent(ActualizarPassUsuario.this, Login.class)
                                                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        startActivity(intent);
                                                        finish();

                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        progressDialog.dismiss();
                                                        Toast.makeText(ActualizarPassUsuario.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(ActualizarPassUsuario.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(ActualizarPassUsuario.this, "La contraseña actual no es la correcta", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void inicializarVariables(){
        tvPassActual = findViewById(R.id.tvPassActual);
        txtPassActual = findViewById(R.id.txtPassActual);
        txtNuevaPass = findViewById(R.id.txtNuevaPass);
        txtNuevaPassR = findViewById(R.id.txtNuevaPassR);
        btnActualizarPass = findViewById(R.id.btnActualizarPass);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        progressDialog = new ProgressDialog(ActualizarPassUsuario.this);

    }

    private  void leerDato() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("usuarios");
        reference.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String pass = ""+snapshot.child("password").getValue();
                tvPassActual.setText(pass);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}