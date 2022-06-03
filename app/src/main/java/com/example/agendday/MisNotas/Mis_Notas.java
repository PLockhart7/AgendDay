package com.example.agendday.MisNotas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agendday.ActualizarNota.ActualizarNota;
import com.example.agendday.Detalle.DetalleNota;
import com.example.agendday.ViewHolder.ViewHolderNota;
import com.example.agendday.modelos.Nota;
import com.firebase.ui.database.FirebaseRecyclerAdapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.agendday.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Mis_Notas extends AppCompatActivity {

    RecyclerView rvNotas;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference bd;

    LinearLayoutManager linearLayoutManager;

    FirebaseRecyclerAdapter<Nota, ViewHolderNota> firebaseRecyclerAdapter;
    FirebaseRecyclerOptions<Nota> options;

    Dialog dialog;

    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_notas);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Mis Notas");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        rvNotas = findViewById(R.id.rvNotas);
        rvNotas.setHasFixedSize(true);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        firebaseDatabase = FirebaseDatabase.getInstance();
        bd = firebaseDatabase.getReference("notas_publicadas");
        dialog = new Dialog(Mis_Notas.this);
        ListarNotas();
    }

    private void ListarNotas() {

        Query query =bd.orderByChild("uidNota").equalTo(user.getUid());

        options = new FirebaseRecyclerOptions.Builder<Nota>().setQuery(query, Nota.class).build();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Nota, ViewHolderNota>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolderNota viewHolderNota, int position, @NonNull Nota nota) {
                viewHolderNota.setearDatos(getApplicationContext(),nota.getIdNota(), nota.getUidNota(), nota.getCorreo(),
                        nota.getFechaHoraActual(), nota.getTitulo(), nota.getDescripcion(), nota.getFechaNota(), nota.getEstado());
            }

            @Override
            public ViewHolderNota onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nota, parent,false);
                ViewHolderNota viewHolderNota = new ViewHolderNota(view);
                viewHolderNota.setOnClickListener(new ViewHolderNota.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        String idNota = getItem(position).getIdNota();
                        String uidNota = getItem(position).getUidNota();
                        String correo = getItem(position).getCorreo();
                        String fechaRegistro = getItem(position).getFechaHoraActual();
                        String titulo = getItem(position).getTitulo();
                        String descripcion = getItem(position).getDescripcion();
                        String fechaNota = getItem(position).getFechaNota();
                        String estado = getItem(position).getEstado();

                        Intent intent = new Intent(Mis_Notas.this, DetalleNota.class);
                        intent.putExtra("idNota",idNota);
                        intent.putExtra("uidNota", uidNota);
                        intent.putExtra("correo",correo);
                        intent.putExtra("fechaRegistro",fechaRegistro);
                        intent.putExtra("titulo", titulo);
                        intent.putExtra("descripcion", descripcion);
                        intent.putExtra("fechaNota", fechaNota);
                        intent.putExtra("estado", estado);
                        startActivity(intent);
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {

                        String idNota = getItem(position).getIdNota();
                        String uidNota = getItem(position).getUidNota();
                        String correo = getItem(position).getCorreo();
                        String fechaRegistro = getItem(position).getFechaHoraActual();
                        String titulo = getItem(position).getTitulo();
                        String descripcion = getItem(position).getDescripcion();
                        String fechaNota = getItem(position).getFechaNota();
                        String estado = getItem(position).getEstado();


                        Button btnEliminarOpciones;
                        Button btnActualizarOpciones;

                        dialog.setContentView(R.layout.dialogo_opciones);

                        btnEliminarOpciones = dialog.findViewById(R.id.btnEliminarOpciones);
                        btnActualizarOpciones = dialog.findViewById(R.id.btnActualizarOpciones);
                        
                        btnEliminarOpciones.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                eliminarNota(idNota);
                                dialog.dismiss();
                            }
                        });
                        
                        btnActualizarOpciones.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //Toast.makeText(Mis_Notas.this, "Nota Actualizada", Toast.LENGTH_SHORT).show();
                                //startActivity(new Intent(Mis_Notas.this, ActualizarNota.class));
                                Intent intent = new Intent(Mis_Notas.this, ActualizarNota.class);
                                intent.putExtra("idNota",idNota);
                                intent.putExtra("uidNota", uidNota);
                                intent.putExtra("correo",correo);
                                intent.putExtra("fechaRegistro",fechaRegistro);
                                intent.putExtra("titulo", titulo);
                                intent.putExtra("descripcion", descripcion);
                                intent.putExtra("fechaNota", fechaNota);
                                intent.putExtra("estado", estado);
                                startActivity(intent);
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    }
                });
                return viewHolderNota;
            }
        };

        linearLayoutManager = new LinearLayoutManager(Mis_Notas.this, LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        rvNotas.setLayoutManager(linearLayoutManager);
        rvNotas.setAdapter(firebaseRecyclerAdapter);
    }

    private void eliminarNota(String idNota) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Mis_Notas.this);
        builder.setTitle("Eliminar Nota");
        builder.setMessage("¿Quieres eliminar la nota?");
        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Query query = bd.orderByChild("idNota").equalTo(idNota);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds : snapshot.getChildren()) {
                            ds.getRef().removeValue();
                        }
                        Toast.makeText(Mis_Notas.this, "Nota Eliminada", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(Mis_Notas.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(Mis_Notas.this, "Cancelado", Toast.LENGTH_SHORT).show();
            }
        });

        builder.create().show();
    }

    private void eliminarRegistroNotas() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Mis_Notas.this);
        builder.setTitle("Eliminar todos los registros");
        builder.setMessage("¿Eliminar todas las notas?");

        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Query query = bd.orderByChild("uidNota").equalTo(user.getUid());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds : snapshot.getChildren()) {
                            ds.getRef().removeValue();
                        }
                        Toast.makeText(Mis_Notas.this, "Se eliminaron todas las notas", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(Mis_Notas.this, "Cancelado", Toast.LENGTH_SHORT).show();
            }
        });
        builder.create().show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_eliminar_todas_notas, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.eliminarTodasNotasMenu) {
            eliminarRegistroNotas();
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onStart() {
        super.onStart();
        if(firebaseRecyclerAdapter!=null){
            firebaseRecyclerAdapter.startListening();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}