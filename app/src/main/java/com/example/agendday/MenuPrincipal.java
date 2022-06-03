package com.example.agendday;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.agendday.AgregarNota.Agregar_Nota;
import com.example.agendday.MisNotas.Mis_Notas;
import com.example.agendday.NotasImportantes.Notas_Importantes;
import com.example.agendday.Perfil.Perfil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MenuPrincipal extends AppCompatActivity {

    TextView nombrePersonaPrincipal, correoPersonaPrincipal, uidPrincipal;
    Button btnCerrarSesion, btnAgregarNota, btnMisNotas, btnNotasImportantes, btnContactos, btnAcercaDe, btnEstadoCuentaPrincipal;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;

    ProgressBar pbDatos;
    ProgressDialog progressDialog;

    LinearLayoutCompat linearNombres, linearCorreo, linearCorreoVerificacion;

    DatabaseReference usuarios;

    Dialog dialogCuentaVerificada, dialogInformacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("AgendDay");

        uidPrincipal = findViewById(R.id.uidPrincipal);
        nombrePersonaPrincipal = findViewById(R.id.nombrePersonaPrincipal);
        correoPersonaPrincipal = findViewById(R.id.correoPersonaPrincipal);
        btnEstadoCuentaPrincipal = findViewById(R.id.btnEstadoCuentaPrincipal);
        pbDatos = findViewById(R.id.pbDatos);

        dialogCuentaVerificada = new Dialog(this);
        dialogInformacion = new Dialog(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Espere...");
        progressDialog.setCanceledOnTouchOutside(false);

        linearNombres = findViewById(R.id.linearNombre);
        linearCorreo = findViewById(R.id.linearCorreo);
        linearCorreoVerificacion = findViewById(R.id.linearCorreoVerificacion);

        usuarios = FirebaseDatabase.getInstance().getReference("usuarios");

        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        btnAgregarNota = findViewById(R.id.btnAgregarNota);
        btnMisNotas = findViewById(R.id.btnMisNotas);
        btnNotasImportantes = findViewById(R.id.btnNotasImportantes);
        btnContactos = findViewById(R.id.btnContactos);
        btnAcercaDe = findViewById(R.id.btnAcercaDe);

        firebaseAuth = FirebaseAuth.getInstance();
        user =  firebaseAuth.getCurrentUser();

        btnEstadoCuentaPrincipal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user.isEmailVerified()) {
                    //Toast.makeText(MenuPrincipal.this, "Cuenta Verificada", Toast.LENGTH_SHORT).show();
                    cuentaVeriAnimacion();
                }else {
                    verificarCuenta();
                }
            }
        });

        btnAgregarNota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uidUsuario = uidPrincipal.getText().toString();
                String correoUsuario = correoPersonaPrincipal.getText().toString();

               Intent intent = new Intent(MenuPrincipal.this, Agregar_Nota.class);
               intent.putExtra("UID", uidUsuario);
               intent.putExtra("CORREO", correoUsuario);
               startActivity(intent);

            }
        });
        
        btnMisNotas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MenuPrincipal.this, Mis_Notas.class));
                Toast.makeText(MenuPrincipal.this, "Mis Notas", Toast.LENGTH_SHORT).show();
            }
        });
        
        btnNotasImportantes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MenuPrincipal.this, Notas_Importantes.class));
                Toast.makeText(MenuPrincipal.this, "Notas Archivadas", Toast.LENGTH_SHORT).show();
            }
        });
        
        btnContactos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MenuPrincipal.this, "Contactos Usuario", Toast.LENGTH_SHORT).show();
            }
        });
        
        btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salirAplicacion();
            }
        });

        btnAcercaDe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                informacion();
            }
        });

    }

    private void verificarCuenta() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Verificar cuenta").setMessage("Se enviaran instrucciones de verificacion a su correo" + user.getEmail())
                .setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                       enviarCorreoVerificacion();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(MenuPrincipal.this, "Cancelado", Toast.LENGTH_SHORT).show();
                    }
                }).show();
    }

    private void enviarCorreoVerificacion() {
            progressDialog.setMessage("Enviando instrucciones a su correo" + user.getEmail());
            progressDialog.show();
            user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    progressDialog.dismiss();
                    Toast.makeText(MenuPrincipal.this, "Instrucciones Enviadas " + user.getEmail(), Toast.LENGTH_SHORT).show();
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MenuPrincipal.this, "Fallo:" +e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
    }

    private void verificarEstadoCuenta() {
        String verificado = "Verificado";
        String noVerificado = "No Verificado";
        if (user.isEmailVerified()){
            btnEstadoCuentaPrincipal.setText(verificado);
            btnEstadoCuentaPrincipal.setBackgroundColor(Color.rgb(1, 249, 234 ));
        }else{
            btnEstadoCuentaPrincipal.setText(noVerificado);
            btnEstadoCuentaPrincipal.setBackgroundColor(Color.rgb(250, 4, 4 ));
        }
    }

    private void cuentaVeriAnimacion() {
        Button btnVerificadoCuenta;

        dialogCuentaVerificada.setContentView(R.layout.dialogo_cuenta_verificada);

        btnVerificadoCuenta = dialogCuentaVerificada.findViewById(R.id.btnVerificadoCuenta);

        btnVerificadoCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogCuentaVerificada.dismiss();
            }
        });
        dialogCuentaVerificada.show();
        dialogCuentaVerificada.setCanceledOnTouchOutside(false);
    }

    private void informacion(){
        Button btnEntendido;

        dialogInformacion.setContentView(R.layout.cuadro_dialogo_informacion);

        btnEntendido = dialogInformacion.findViewById(R.id.btnEntendido);

        btnEntendido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogInformacion.dismiss();
            }
        });

        dialogInformacion.show();
        dialogInformacion.setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onStart() {
        comprobarInicioSesion();
        super.onStart();
    }

    private void comprobarInicioSesion() {
        if(user!=null) {
            cargarDatos();
        }else {
            startActivity(new Intent(MenuPrincipal.this,MainActivity.class));
            finish();
        }
    }

    private void  cargarDatos() {

        verificarEstadoCuenta();

        usuarios.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //si existe el usuario
                if(snapshot.exists()){
                    //pb se oculta
                    pbDatos.setVisibility(View.GONE);
                    //TV se muestran
                    //uidPrincipal.setVisibility(View.VISIBLE);
                    //nombrePersonaPrincipal.setVisibility(View.VISIBLE);
                    //correoPersonaPrincipal.setVisibility(View.VISIBLE);
                    linearNombres.setVisibility(View.VISIBLE);
                    linearCorreo.setVisibility(View.VISIBLE);
                    linearCorreoVerificacion.setVisibility(View.VISIBLE);

                    String uid = ""+snapshot.child("uid").getValue();
                    String nombre = ""+snapshot.child("nombre").getValue();
                    String correo = ""+snapshot.child("correo").getValue();

                    uidPrincipal.setText(uid);
                    nombrePersonaPrincipal.setText(nombre);
                    correoPersonaPrincipal.setText(correo);

                    btnAgregarNota.setEnabled(true);
                    btnAcercaDe.setEnabled(true);
                    btnContactos.setEnabled(true);
                    btnCerrarSesion.setEnabled(true);
                    btnMisNotas.setEnabled(true);
                    btnNotasImportantes.setEnabled(true);



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_principal, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.perfilUsuarioMenu){
            startActivity(new Intent(MenuPrincipal.this, Perfil.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private void salirAplicacion() {
        firebaseAuth.signOut();
        startActivity(new Intent(MenuPrincipal.this, MainActivity.class));
        Toast.makeText(this, "Has cerrado sesión", Toast.LENGTH_SHORT).show();
    }
}