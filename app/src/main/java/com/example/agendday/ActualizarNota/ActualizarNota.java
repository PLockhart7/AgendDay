package com.example.agendday.ActualizarNota;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.agendday.AgregarNota.Agregar_Nota;
import com.example.agendday.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class ActualizarNota extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    TextView idActuNota;
    TextView uidUsuarioActuNota;
    TextView correoActuNota;
    TextView fechaHoraActualActuNota;
    TextView fechaActuNota;
    TextView estadoActuNota;
    TextView estadoNuevo;

    EditText txtTituloActuNota;
    EditText txtDescripcionActuNota;

    Button btnCalendarioActuNota;

    String idNotaR;
    String uidNotaR;
    String correoR;
    String fechaRegistroR;
    String tituloR;
    String descripcionR;
    String fechaR;
    String estadoR;

    ImageView ivNotaFinalizada;
    ImageView ivNotaNoFinalizada;

    Spinner spEstado;
    int dia, mes, anyo;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualizar_nota);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Actualizar Nota");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        iniciarVistas();
        recuperarDatos();
        setearDatos();
        comprobarEstadoNota();
        spinnerEstado();

        btnCalendarioActuNota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectFecha();
            }
        });

    }

    private void iniciarVistas() {
        idActuNota = findViewById(R.id.idActuNota);
        uidUsuarioActuNota = findViewById(R.id.uidUsuarioActuNota);
        correoActuNota = findViewById(R.id.correoActuNota);
        fechaHoraActualActuNota = findViewById(R.id.fechaHoraActualActuNota);
        fechaActuNota = findViewById(R.id.fechaActuNota);
        estadoActuNota = findViewById(R.id.estadoActuNota);
        txtTituloActuNota = findViewById(R.id.txtTituloActuNota);
        txtDescripcionActuNota = findViewById(R.id.txtDescripcionActuNota);
        btnCalendarioActuNota = findViewById(R.id.btnCalendarioActuNota);
        ivNotaFinalizada = findViewById(R.id.ivNotaFinalizada);
        ivNotaNoFinalizada = findViewById(R.id.ivNotaNoFinalizada);
        spEstado = findViewById(R.id.spEstado);
        estadoNuevo = findViewById(R.id.estadoNuevo);


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

    private void setearDatos() {
        idActuNota.setText(idNotaR);
        uidUsuarioActuNota.setText(uidNotaR);
        correoActuNota.setText(correoR);
        fechaHoraActualActuNota.setText(fechaRegistroR);
        txtTituloActuNota.setText(tituloR);
        txtDescripcionActuNota.setText(descripcionR);
        fechaActuNota.setText(fechaR);
        estadoActuNota.setText(estadoR);
    }

    private  void comprobarEstadoNota () {
        String estadoNota = estadoActuNota.getText().toString();

        if (estadoNota.equals("No finalizado")) {
            ivNotaNoFinalizada.setVisibility(View.VISIBLE);
        }
        if (estadoNota.equals("Finalizado")){
            ivNotaFinalizada.setVisibility(View.VISIBLE);
        }
    }

    private void selectFecha() {
        final Calendar calendar = Calendar.getInstance();

        dia = calendar.get(Calendar.DAY_OF_MONTH);
        mes = calendar.get(Calendar.MONTH);
        anyo = calendar.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(ActualizarNota.this, new DatePickerDialog.OnDateSetListener() {
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
                fechaActuNota.setText(diaFormat + "/" + mesFormat + "/"+ anyoSelec);
            }

        },anyo, mes, dia);
        datePickerDialog.show();
    }

    private void spinnerEstado() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.estado_nota, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spEstado.setAdapter(adapter);
        spEstado.setOnItemSelectedListener(this);
    }

    private void actualizarNotabd() {
        String tituloActu = txtTituloActuNota.getText().toString();
        String descripcionActu = txtDescripcionActuNota.getText().toString();
        String fechaActu = fechaActuNota.getText().toString();
        String estadoActu = estadoNuevo.getText().toString();

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("notas_publicadas");

        Query query = databaseReference.orderByChild("idNota").equalTo(idNotaR);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){
                    ds.getRef().child("titulo").setValue(tituloActu);
                    ds.getRef().child("descripcion").setValue(descripcionActu);
                    ds.getRef().child("fechaNota").setValue(fechaActu);
                    ds.getRef().child("estado").setValue(estadoActu);
                }
                Toast.makeText(ActualizarNota.this, "Nota Actualizada con exito", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String estadoActual = estadoActuNota.getText().toString();
        String posicion1 = adapterView.getItemAtPosition(1).toString();

        String estadoSelect = adapterView.getItemAtPosition(i).toString();
        estadoNuevo.setText(estadoSelect
        );
        if(estadoActual.equals("Finalizado")){
            estadoNuevo.setText(posicion1);
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_actualizar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actualizarNotaMenu:
                actualizarNotabd();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}