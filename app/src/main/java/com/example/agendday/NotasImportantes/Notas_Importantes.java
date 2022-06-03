package com.example.agendday.NotasImportantes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.agendday.ActualizarNota.ActualizarNota;
import com.example.agendday.Detalle.DetalleNota;
import com.example.agendday.MisNotas.Mis_Notas;
import com.example.agendday.R;
import com.example.agendday.ViewHolder.ViewHolderNota;
import com.example.agendday.ViewHolder.ViewHolderNotaImportante;
import com.example.agendday.modelos.Nota;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class Notas_Importantes extends AppCompatActivity {

    RecyclerView rvNotasImp;
    FirebaseDatabase firebaseDatabase;

    DatabaseReference misUsuarios;
    DatabaseReference notasImportantes;

    FirebaseAuth firebaseAuth;
    FirebaseUser user;

    FirebaseRecyclerAdapter<Nota, ViewHolderNotaImportante> firebaseRecyclerAdapter;
    FirebaseRecyclerOptions<Nota> firebaseRecyclerOptions;

    LinearLayoutManager linearLayoutManager;

    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notas_archivadas);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Notas Importantes");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        rvNotasImp = findViewById(R.id.rvNotasImp);
        rvNotasImp.setHasFixedSize(true);
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        misUsuarios = firebaseDatabase.getReference("usuarios");
        notasImportantes = firebaseDatabase.getReference("notas importantes");
        dialog = new Dialog(Notas_Importantes.this);

        comprobarUsuario();
    }
    
    private void comprobarUsuario() {
        if(user == null) {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }else {
            listarNotasImportantes();
        }
    }

    private void listarNotasImportantes() {

        firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Nota>().setQuery(misUsuarios.child(user.getUid()).child("notas importantes").orderByChild("fechaNota"), Nota.class).build();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Nota, ViewHolderNotaImportante>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolderNotaImportante viewHolderNotaImportante, int position, @NonNull Nota nota) {
                viewHolderNotaImportante.setearDatos(getApplicationContext(),nota.getIdNota(), nota.getUidNota(), nota.getCorreo(),
                        nota.getFechaHoraActual(), nota.getTitulo(), nota.getDescripcion(), nota.getFechaNota(), nota.getEstado());
            }

            @Override
            public ViewHolderNotaImportante onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nota_importante, parent,false);
                ViewHolderNotaImportante viewHolderNotaImportante = new ViewHolderNotaImportante(view);
                viewHolderNotaImportante.setOnClickListener(new ViewHolderNotaImportante.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {


                    }

                    @Override
                    public void onItemLongClick(View view, int position) {

                        String idNota = getItem(position).getIdNota();

                        Button btnEliminarDialogoNotaImp;
                        Button btnCancelarDialogoNotaImp;

                        dialog.setContentView(R.layout.cuadro_dialogo_eliminar_nota_importante);

                        btnEliminarDialogoNotaImp = dialog.findViewById(R.id.btnEliminarDialogoNotaImp);
                        btnCancelarDialogoNotaImp = dialog.findViewById(R.id.btnCancelarDialogoNotaImp);

                        btnEliminarDialogoNotaImp.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                eliminarNotaImportante(idNota);
                                dialog.dismiss();
                            }
                        });

                        btnCancelarDialogoNotaImp.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(Notas_Importantes.this, "Cancelado por el usuario", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        });

                        dialog.show();

                    }
                });
                return viewHolderNotaImportante;
            }
        };

        linearLayoutManager = new LinearLayoutManager(Notas_Importantes.this, LinearLayoutManager.VERTICAL, false);


        rvNotasImp.setLayoutManager(linearLayoutManager);
        rvNotasImp.setAdapter(firebaseRecyclerAdapter);
    }

    private void eliminarNotaImportante(String idNota) {
        if(user == null) {
            Toast.makeText(Notas_Importantes.this, "Error", Toast.LENGTH_SHORT).show();
        }else{

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("usuarios");
            reference.child(firebaseAuth.getUid()).child("notas importantes").child(idNota)
                    .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(Notas_Importantes.this, "La nota ya no es importante", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Notas_Importantes.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        }
    }

    @Override
    protected void onStart() {
        if(firebaseRecyclerAdapter != null) {
            firebaseRecyclerAdapter.startListening();
        }
        super.onStart();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}