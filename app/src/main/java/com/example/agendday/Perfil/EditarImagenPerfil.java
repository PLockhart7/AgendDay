package com.example.agendday.Perfil;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.agendday.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class EditarImagenPerfil extends AppCompatActivity {

    ImageView imgPerfilActualizar;

    Button btnElegirImagenDe;
    Button btnActualizarImagen;

    FirebaseAuth firebaseAuth;
    FirebaseUser user;

    Dialog dialogElegirImagen;

    Uri imagenUri = null;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_imagen_perfil);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Seleccionar imagen");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        imgPerfilActualizar = findViewById(R.id.imgPerfilActualizar);
        btnActualizarImagen = findViewById(R.id.btnActualizarImagen);
        btnElegirImagenDe = findViewById(R.id.btnElegirImagenDe);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();



        dialogElegirImagen = new Dialog(EditarImagenPerfil.this);

        btnElegirImagenDe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                elegirImagenDe();
            }
        });

        btnActualizarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imagenUri == null){
                    Toast.makeText(EditarImagenPerfil.this, "Inserte una nueva imagen", Toast.LENGTH_SHORT).show();
                }else {
                    subirImgStorage();
                }
            }
        });

        progressDialog = new ProgressDialog(EditarImagenPerfil.this);
        progressDialog.setTitle("Espere por favor");
        progressDialog.setCanceledOnTouchOutside(false);

        leerImagen();


    }



    private void leerImagen() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("usuarios");
        reference.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Obtener el dato imagen
                String imagenPerfil = ""+snapshot.child("imagenPerfil").getValue();

                Glide.with(getApplicationContext())
                        .load(imagenPerfil)
                        .placeholder(R.drawable.imagen_perfil_usuario)
                        .into(imgPerfilActualizar);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void subirImgStorage() {
        progressDialog.setMessage("Subiendo imagen");
        progressDialog.show();
        String carpetaImagenes = "ImagenesPerfil/";
        String NombreImagen = carpetaImagenes+firebaseAuth.getUid();
        StorageReference reference = FirebaseStorage.getInstance().getReference(NombreImagen);
        reference.putFile(imagenUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());
                        String uriImagen = ""+uriTask.getResult();
                        actualizarImagenBD(uriImagen);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditarImagenPerfil.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void actualizarImagenBD(String uriImagen) {
        progressDialog.setTitle("Actualizando la imagen");
        progressDialog.show();

        HashMap<String, Object> hashMap = new HashMap<>();
        if (imagenUri != null){
            hashMap.put("imagenPerfil", ""+uriImagen);
        }

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("usuarios");
        databaseReference.child(user.getUid())
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Toast.makeText(EditarImagenPerfil.this, "Imagen se ha actualizado con éxito", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditarImagenPerfil.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void elegirImagenDe() {
        Button btnElegirGaleria, btnElegirCamara;

        dialogElegirImagen.setContentView(R.layout.cuadro_dialogo_elegir_imagen);

        btnElegirGaleria = dialogElegirImagen.findViewById(R.id.btnElegirGaleria);
        btnElegirCamara = dialogElegirImagen.findViewById(R.id.btnElegirCamara);

        btnElegirGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(EditarImagenPerfil.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                    seleccionarImagenGaleria();
                    dialogElegirImagen.dismiss();
                }else {
                    solicitudPermisoGaleria.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    dialogElegirImagen.dismiss();
                }

            }
        });

       btnElegirCamara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(EditarImagenPerfil.this,
                        Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                    seleccionarImagenCamara();
                    dialogElegirImagen.dismiss();
                }else {
                    solicitudPermisoCamara.launch(Manifest.permission.CAMERA);
                    dialogElegirImagen.dismiss();
                }
            }
        });

        dialogElegirImagen.show();
        dialogElegirImagen.setCanceledOnTouchOutside(true);
    }



    private void seleccionarImagenGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galeriaActivityResultLauncher.launch(intent);
    }

    /*PERMISO PARA ACCEDER A LA GALERIA*/
    private ActivityResultLauncher<String> solicitudPermisoGaleria =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted){
                    seleccionarImagenGaleria();
                }else{
                    Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show();
                }
            });

    private ActivityResultLauncher<Intent> galeriaActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK){
                        //Obtener uri de la imagen
                        Intent data = result.getData();
                        imagenUri = data.getData();
                        //Setear la imagen seleccionada en el imageView
                        imgPerfilActualizar.setImageURI(imagenUri);

                    }else{
                        Toast.makeText(EditarImagenPerfil.this, "Cancelado", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private void seleccionarImagenCamara()  {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Nueva imagen");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Descripción de imagen");
        imagenUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imagenUri);
        camaraActivityResultLauncher.launch(intent);

    }

    private ActivityResultLauncher<Intent> camaraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK){
                        imgPerfilActualizar.setImageURI(imagenUri);
                    }
                    else {
                        Toast.makeText(EditarImagenPerfil.this, "Cancelado", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    /*PERMISO PARA ACCEDER A LA CAMARA*/
    private ActivityResultLauncher<String> solicitudPermisoCamara =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted ->{
                if (isGranted){
                    seleccionarImagenCamara();
                }else {
                    Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

}