package com.example.agendday.ViewHolder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agendday.R;

public class ViewHolderNota extends RecyclerView.ViewHolder {

    View mView;

    private ViewHolderNota.ClickListener mClickListener;

    public interface ClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }

    public void setOnClickListener(ViewHolderNota.ClickListener clickListener) {
        mClickListener = clickListener;
    }

    public ViewHolderNota(@NonNull View itemView) {
        super(itemView);
        mView = itemView;

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onItemClick(v, getBindingAdapterPosition());
            }
        });

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mClickListener.onItemLongClick(v, getBindingAdapterPosition());
                return false;
            }
        });
    }

    public void setearDatos(Context context, String idNota, String uidNota, String correo,
                            String fechaHoraRegistro, String titulo, String descripcion,
                            String fechaNota, String estado) {

        TextView idNotaItem, uidUsuarioItem, correoUsuarioItem, fechaHoraActualItem, tituloItem,
                descripcionItem, fechaItem, estadoItem;

        ImageView ivNotaFinalizadaItem, ivNotaNoFinalizadaItem;

        idNotaItem = mView.findViewById(R.id.idNotaItem);
        uidUsuarioItem = mView.findViewById(R.id.uidUsuarioItem);
        correoUsuarioItem = mView.findViewById(R.id.correoUsuarioItem);
        fechaHoraActualItem = mView.findViewById(R.id.fechaHoraActualItem);
        tituloItem = mView.findViewById(R.id.tituloItem);
        descripcionItem = mView.findViewById(R.id.descripcionItem);
        fechaItem = mView.findViewById(R.id.fechaItem);
        estadoItem = mView.findViewById(R.id.estadoItem);

        ivNotaFinalizadaItem = mView.findViewById(R.id.ivNotaFinalizadaItem);
        ivNotaNoFinalizadaItem = mView.findViewById(R.id.ivNotaNoFinalizadaItem);

        idNotaItem.setText(idNota);
        uidUsuarioItem.setText(uidNota);
        correoUsuarioItem.setText(correo);
        fechaHoraActualItem.setText(fechaHoraRegistro);
        tituloItem.setText(titulo);
        descripcionItem.setText(descripcion);
        fechaItem.setText(fechaNota);
        estadoItem.setText(estado);

        if(estado.equals("Finalizado")){
            ivNotaFinalizadaItem.setVisibility(View.VISIBLE);
        }else {
            ivNotaNoFinalizadaItem.setVisibility(View.VISIBLE);
        }
    }
}
