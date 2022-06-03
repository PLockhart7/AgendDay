package com.example.agendday.Detalle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

import org.w3c.dom.Text;

import java.util.HashMap;

public class DetalleNota extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseUser user;

    Button btnImportanteDetalle;

    TextView idNotaDetalle;
    TextView uidUsuarioDetalle;
    TextView correoDetalle;
    TextView tituloDetalle;
    TextView descripcionDetalle;
    TextView fechaRegistroDetalle;
    TextView fechaNotaDetalle;
    TextView estadoDetalle;


    String idNotaR;
    String uidNotaR;
    String correoR;
    String fechaRegistroR;
    String tituloR;
    String descripcionR;
    String fechaR;
    String estadoR;

    boolean comprobarNotaImportante = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_nota);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Detalle de nota");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        inicializarVistas();
        recuperarDatos();
        setearDatosRecu();
        verificarNotaImportante();

        btnImportanteDetalle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(comprobarNotaImportante) {
                    eliminarNotaImportante();
                }else {
                    agregarNotasImportantes();
                }
            }
        });
    }

    private void inicializarVistas() {
        idNotaDetalle = findViewById(R.id.idNotaDetalle);
        uidUsuarioDetalle = findViewById(R.id.uidUsuarioDetalle);
        correoDetalle = findViewById(R.id.correoUsuarioDetalle);
        tituloDetalle = findViewById(R.id.tituloDetalle);
        descripcionDetalle = findViewById(R.id.descripcionDetalle);
        fechaRegistroDetalle = findViewById(R.id.fechaRegistroDetalle);
        fechaNotaDetalle = findViewById(R.id.fechaDetalle);
        estadoDetalle = findViewById(R.id.estadoDetalle);

        btnImportanteDetalle = findViewById(R.id.btnImportanteDetalle);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
    }

    private void recuperarDatos() {
        Bundle intent = getIntent().getExtras();

        idNotaR = intent.getString("idNota");
        uidNotaR = intent.getString("uidNota");
        correoR = intent.getString("correo");
        fechaRegistroR = intent.getString("fechaRegistro");
        tituloR = intent.getString("titulo");
        descripcionR = intent.getString("descripcion");
        fechaR = intent.getString("fechaNota");
        estadoR = intent.getString("estado");
    }

    private  void setearDatosRecu() {
        idNotaDetalle.setText(idNotaR);
        uidUsuarioDetalle.setText(uidNotaR);
        correoDetalle.setText(correoR);
        fechaRegistroDetalle.setText(fechaRegistroR);
        tituloDetalle.setText(tituloR);
        descripcionDetalle.setText(descripcionR);
        fechaNotaDetalle.setText(fechaR);
        estadoDetalle.setText(estadoR);
    }

    private void agregarNotasImportantes() {
        if(user == null) {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }else{
            Bundle intent = getIntent().getExtras();

            idNotaR = intent.getString("idNota");
            uidNotaR = intent.getString("uidNota");
            correoR = intent.getString("correo");
            fechaRegistroR = intent.getString("fechaRegistro");
            tituloR = intent.getString("titulo");
            descripcionR = intent.getString("descripcion");
            fechaR = intent.getString("fechaNota");
            estadoR = intent.getString("estado");



            HashMap<String , String> nota_importante = new HashMap<>();
            nota_importante.put("idNota", idNotaR);
            nota_importante.put("uidNota",uidNotaR);
            nota_importante.put("correo",correoR);
            nota_importante.put("fechaHoraActual",fechaRegistroR);
            nota_importante.put("titulo", tituloR);
            nota_importante.put("descripcion", descripcionR);
            nota_importante.put("fechaNota", fechaR);
            nota_importante.put("estado", estadoR);


            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("usuarios");
            reference.child(firebaseAuth.getUid()).child("notas importantes").child(idNotaR)
                    .setValue(nota_importante).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(DetalleNota.this, "Se ha añadido a notas importantes", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(DetalleNota.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void eliminarNotaImportante() {
        if(user == null) {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }else{
            Bundle intent = getIntent().getExtras();
            idNotaR = intent.getString("idNota");


            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("usuarios");
            reference.child(firebaseAuth.getUid()).child("notas importantes").child(idNotaR)
                    .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(DetalleNota.this, "La nota ya no es importante", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(DetalleNota.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        }
    }

    private void verificarNotaImportante() {
        if(user == null) {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }else{
            Bundle intent = getIntent().getExtras();
            idNotaR = intent.getString("idNota");

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("usuarios");
            reference.child(firebaseAuth.getUid()).child("notas importantes").child(idNotaR)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            comprobarNotaImportante = snapshot.exists();
                            if(comprobarNotaImportante) {
                                String important = "Importante";
                                btnImportanteDetalle.setCompoundDrawablesRelativeWithIntrinsicBounds(0,R.drawable.icono_nota_importante,0,0);
                                btnImportanteDetalle.setText(important);
                            }else{
                                String noImportant = "No importante";
                                btnImportanteDetalle.setCompoundDrawablesRelativeWithIntrinsicBounds(0,R.drawable.icono_nota_no_importante,0,0);
                                btnImportanteDetalle.setText(noImportant);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}