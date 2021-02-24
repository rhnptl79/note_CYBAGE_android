package com.example.notesapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notesapp.ActivityAddNote;
import com.example.notesapp.MainActivity;
import com.example.notesapp.R;
import com.example.notesapp.data.NotesData;
import com.example.notesapp.database.DatabaseHandler;

import java.util.ArrayList;

public class AdapterNotes extends RecyclerView.Adapter<AdapterNotes.MyViewHolder> {

    Context _context;
    ArrayList<NotesData> _list;
    DatabaseHandler _db;

    public AdapterNotes(Context _context, ArrayList<NotesData> _list, DatabaseHandler db) {
        this._context = _context;
        this._list = _list;
        this._db = db;
    }


    @NonNull
    @Override
    public AdapterNotes.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.adapter_notes_list, parent, false);

        return new AdapterNotes.MyViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterNotes.MyViewHolder holder, int position) {
        holder.tv_name.setText(_list.get(position).getTitle());
        holder.tv_date.setText(_list.get(position).getTimeStamp());

        holder.rl_notes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(_context, ActivityAddNote.class);
                intent.putExtra("edit", _list.get(position));
                intent.putExtra("catId", String.valueOf(_list.get(position).getCatId()));
                _context.startActivity(intent);
            }
        });

        holder.iv_dlt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _db.deleteNotes(_list.get(position));
                _list.remove(position);
                notifyDataSetChanged();
            }
        });
        holder.iv_move.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(_context, MainActivity.class);
                intent.putExtra("from", "move");
                intent.putExtra("noteData", _list.get(position));
                _context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return _list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name, tv_date;
        RelativeLayout rl_notes;
        ImageView iv_dlt, iv_move;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_date = itemView.findViewById(R.id.tv_date);
            rl_notes = itemView.findViewById(R.id.rl_notes);
            iv_dlt = itemView.findViewById(R.id.iv_dlt);
            iv_move = itemView.findViewById(R.id.iv_move);
        }
    }
}
