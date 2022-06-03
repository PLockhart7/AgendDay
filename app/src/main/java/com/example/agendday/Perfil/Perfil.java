package com.example.agendday.Perfil;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.agendday.ActualizarNota.ActualizarNota;
import com.example.agendday.ActualizarPass.ActualizarPassUsuario;
import com.example.agendday.MenuPrincipal;
import com.example.agendday.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

import java.util.Calendar;
import java.util.HashMap;

public class Perfil extends AppCompatActivity {

    ImageView imagenPerfil;

    TextView correoPerfil;
    TextView uidPerfil;
    TextView telefonoPerfil;
    TextView fechaNacimientoPerfil;

    EditText nombrePerfil;
    EditText apellidosPerfil;
    EditText edadPErfil;
    EditText domicilioPerfil;

    ImageView editarTlf;
    ImageView editarFecha;
    ImageView editarImagen;

    Button btnGuardarDatosPerfil;

    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    DatabaseReference usuarios;

    Dialog dialogTlf;

    int dia, mes, anyo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Perfil de usuario");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        inicializarVariables();

        editarTlf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                telefonoUsuario();
            }
        });

        editarFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirCalendario();
            }
        });

        btnGuardarDatosPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actualizarDatos();
            }
        });

        editarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Perfil.this,EditarImagenPerfil.class));
            }
        });
    }

    private void inicializarVariables(){
        imagenPerfil = findViewById(R.id.imagenPerfil);
        correoPerfil = findViewById(R.id.correoPerfil);
        uidPerfil = findViewById(R.id.uidPerfil);
        nombrePerfil = findViewById(R.id.nombrePerfil);
        apellidosPerfil = findViewById(R.id.apellidosPerfil);
        edadPErfil = findViewById(R.id.edadPErfil);
        telefonoPerfil = findViewById(R.id.telefonoPerfil);
        domicilioPerfil = findViewById(R.id.domicilioPerfil);
        editarTlf = findViewById(R.id.editarTlf);
        editarFecha = findViewById(R.id.editarFecha);
        fechaNacimientoPerfil = findViewById(R.id.fechaNacimientoPerfil);
        editarImagen = findViewById(R.id.editarImagen);



        dialogTlf = new Dialog(Perfil.this);

        btnGuardarDatosPerfil = findViewById(R.id.btnGuardarDatosPerfil);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        usuarios = FirebaseDatabase.getInstance().getReference("usuarios");
    }

    private void leerDatos() {
        usuarios.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    //Obtener sus datos
                    String uid = ""+snapshot.child("uid").getValue();
                    String nombre = ""+snapshot.child("nombre").getValue();
                    String apellidos = ""+snapshot.child("apellidos").getValue();
                    String correo = ""+snapshot.child("correo").getValue();
                    String edad = ""+snapshot.child("edad").getValue();
                    String telefono = ""+snapshot.child("telefono").getValue();
                    String domicilio = ""+snapshot.child("domicilio").getValue();
                    String imgPerfil = ""+snapshot.child("imagenPerfil").getValue();
                    String fechaNacimiento = ""+snapshot.child("fechaNacimiento").getValue();


                    //Seteo de datos
                    uidPerfil.setText(uid);
                    nombrePerfil.setText(nombre);
                    apellidosPerfil.setText(apellidos);
                    correoPerfil.setText(correo);
                    edadPErfil.setText(edad);
                    telefonoPerfil.setText(telefono);
                   domicilioPerfil.setText(domicilio);
                   fechaNacimientoPerfil.setText(fechaNacimiento);

                   cargarImagen(imgPerfil);


                }
                else {
                    Toast.makeText(Perfil.this, "Esperando datos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Perfil.this, ""+error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarImagen(String imgPerfil) {
        try {
            /*Cuando la imagen ha sido traida exitosamente desde Firebase*/
            Glide.with(getApplicationContext()).load(imgPerfil).placeholder(R.drawable.imagen_perfil_usuario).into(imagenPerfil);

        }catch (Exception e){
            /*Si la imagen no fue traida con éxito*/
            Glide.with(getApplicationContext()).load(R.drawable.imagen_perfil_usuario).into(imagenPerfil);

        }
    }

    private void telefonoUsuario() {
        CountryCodePicker ccp;
        EditText txtEscribirTelefono;
        Button btnAceptarTlf;

        dialogTlf.setContentView(R.layout.dialogo_codigo_telefono);

        ccp = dialogTlf.findViewById(R.id.ccp);
        txtEscribirTelefono = dialogTlf.findViewById(R.id.txtEscribirTelefono);
        btnAceptarTlf = dialogTlf.findViewById(R.id.btnAceptarTlf);

        btnAceptarTlf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String codigoPais =ccp.getSelectedCountryCodeWithPlus();
                String tlf = txtEscribirTelefono.getText().toString();
                String codigoPaisTlf = codigoPais+tlf;

                if(!tlf.equals("")){
                    telefonoPerfil.setText(codigoPaisTlf);
                    dialogTlf.dismiss();
                }else{
                    Toast.makeText(Perfil.this, "Pon un numero", Toast.LENGTH_SHORT).show();
                    dialogTlf.dismiss();
                }
            }
        });
        dialogTlf.show();
        dialogTlf.setCanceledOnTouchOutside(true);
    }

    private void abrirCalendario() {
        final Calendar calendar = Calendar.getInstance();

        dia = calendar.get(Calendar.DAY_OF_MONTH);
        mes = calendar.get(Calendar.MONTH);
        anyo = calendar.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(Perfil.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int anyoSelec, int mesSelec, int diaSelec) {

                String diaFormat, mesFormat;

                if (diaSelec < 10) {
                    diaFormat = "0" + String.valueOf(diaSelec);
                } else {
                    diaFormat = String.valueOf(diaSelec);
                }

                int mes0 = mesSelec + 1;
                if(mes0 < 10) {
                    mesFormat = "0"+String.valueOf(mes0);
                }else{
                    mesFormat = String.valueOf(mes0);
                }
                fechaNacimientoPerfil.setText(diaFormat + "/" + mesFormat + "/"+ anyoSelec);
            }

        },anyo, mes, dia);
        datePickerDialog.show();
    }

    private void actualizarDatos(){

        String nombreActu = nombrePerfil.getText().toString().trim();
        String apellidoActu = apellidosPerfil.getText().toString().trim();
        String edadActu = edadPErfil.getText().toString().trim();
        String tlfActu = telefonoPerfil.getText().toString().trim();
        String domicilioActu = domicilioPerfil.getText().toString().trim();
        String fechaNaciActu = fechaNacimientoPerfil.getText().toString().trim();

        HashMap<String, Object> actuDatos = new HashMap<>();

        actuDatos.put("nombre", nombreActu);
        actuDatos.put("apellidos", apellidoActu);
        actuDatos.put("edad", edadActu);
        actuDatos.put("telefono", tlfActu);
        actuDatos.put("domicilio", domicilioActu);
        actuDatos.put("fechaNacimiento", fechaNaciActu);

        usuarios.child(user.getUid()).updateChildren(actuDatos)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(Perfil.this, "Actualizado correctamente", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Perfil.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_actualizar_pass, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.actualizarPass){
            startActivity(new Intent(Perfil.this, ActualizarPassUsuario.class));
        }
        return super.onOptionsItemSelected(item);
    }



    private void comprobarInicioSesion() {
        if (user!=null){
            leerDatos();
        }else {
            startActivity(new Intent(Perfil.this, MenuPrincipal.class));
            finish();
        }
    }

    @Override
    protected void onStart() {
        comprobarInicioSesion();
        super.onStart();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}