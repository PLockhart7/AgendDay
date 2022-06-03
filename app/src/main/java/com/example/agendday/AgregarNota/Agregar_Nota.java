package com.example.agendday.AgregarNota;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.agendday.R;
import com.example.agendday.modelos.Nota;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Agregar_Nota extends AppCompatActivity {

    TextView uidUsuarioAgregarNota, correoAgregarNota, fechaHoraActualAgregarNota, fechaAgregarNota, estadoAgregarNota;
    EditText txtTituloAgregarNota, txtDescripcionAgregarNota;
    Button btnCalendarioAgregarNota;

    int dia, mes, anyo;

    DatabaseReference bdFirebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_nota);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        inicializarVariables();
        obtenerDatos();
        fechaHoraActual();

        btnCalendarioAgregarNota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();

                dia = calendar.get(Calendar.DAY_OF_MONTH);
                mes = calendar.get(Calendar.MONTH);
                anyo = calendar.get(Calendar.YEAR);

                DatePickerDialog datePickerDialog = new DatePickerDialog(Agregar_Nota.this, new DatePickerDialog.OnDateSetListener() {
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
                        fechaAgregarNota.setText(diaFormat + "/" + mesFormat + "/"+ anyoSelec);
                }

            },anyo, mes, dia);
                datePickerDialog.show();
        }
    });

}

    private void inicializarVariables() {
        uidUsuarioAgregarNota = findViewById(R.id.uidUsuarioAgregarNota);
        correoAgregarNota = findViewById(R.id.correoAgregarNota);
        fechaHoraActualAgregarNota = findViewById(R.id.fechaHoraActualAgregarNota);
        fechaAgregarNota = findViewById(R.id.fechaAgregarNota);
        estadoAgregarNota = findViewById(R.id.estadoAgregarNota);

        txtTituloAgregarNota = findViewById(R.id.txtTituloAgregarNota);
        txtDescripcionAgregarNota = findViewById(R.id.txtDescripcionAgregarNota);

        btnCalendarioAgregarNota = findViewById(R.id.btnCalendarioAgregarNota);

        bdFirebase = FirebaseDatabase.getInstance().getReference();
    }

    private void obtenerDatos() {
        String uidUsuario = getIntent().getStringExtra("UID");
        String correoUsuario = getIntent().getStringExtra("CORREO");

        uidUsuarioAgregarNota.setText(uidUsuario);
        correoAgregarNota.setText(correoUsuario);
    }

    private void fechaHoraActual() {
        String fechaHoraActual = new SimpleDateFormat("dd-MM-yyyy/HH:mm:ss a", Locale.getDefault()).format(System.currentTimeMillis());
        fechaHoraActualAgregarNota.setText(fechaHoraActual);
    }

    private void agregarNota() {
        String uidUsuario = uidUsuarioAgregarNota.getText().toString();
        String correoUsuario = correoAgregarNota.getText().toString();
        String fechaHoraActual = fechaHoraActualAgregarNota.getText().toString();
        String titulo = txtTituloAgregarNota.getText().toString();
        String descripcion = txtDescripcionAgregarNota.getText().toString();
        String fecha = fechaAgregarNota.getText().toString();
        String estado = estadoAgregarNota.getText().toString();
        String idNota = bdFirebase.push().getKey();

        if(!uidUsuario.equals("") && !correoUsuario.equals("") && !fechaHoraActual.equals("") &&
        !titulo.equals("") && !descripcion.equals("") && !fecha.equals("") && !estado.equals("")) {

            Nota nota = new Nota(idNota,
                    uidUsuario,correoUsuario,
                    fechaHoraActual,titulo,descripcion,fecha,estado);


            String bdNombre = "notas_publicadas";
            bdFirebase.child(bdNombre).child(idNota).setValue(nota);

            Toast.makeText(this, "La nota se insertó adecuadamente", Toast.LENGTH_SHORT).show();
            onBackPressed();

        }
        else{
            Toast.makeText(this, "Rellena todos los datos", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_agenda, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.agregarNotaMenu:
                agregarNota();
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