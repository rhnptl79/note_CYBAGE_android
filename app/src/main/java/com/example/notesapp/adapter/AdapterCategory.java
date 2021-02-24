package com.example.notesapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notesapp.ActivityAddNote;
import com.example.notesapp.ActivityNotesList;
import com.example.notesapp.R;
import com.example.notesapp.data.CategoryData;
import com.example.notesapp.data.NotesData;
import com.example.notesapp.database.DatabaseHandler;

import java.util.ArrayList;

public class AdapterCategory extends RecyclerView.Adapter<AdapterCategory.MyViewHolder> {
    Context _context;
    ArrayList<CategoryData> _list;
    boolean _status;
    NotesData _notesData;
    DatabaseHandler _db;
    onCatDelete listener;
    public AdapterCategory(Context context, ArrayList<CategoryData> list, boolean status, NotesData notesData, DatabaseHandler db,onCatDelete listener) {
        this._context = context;
        this._list = list;
        this._status = status;
        this._notesData = notesData;
        this._db = db;
        this.listener=listener;
    }

    @NonNull
    @Override
    public AdapterCategory.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.adapter_category, parent, false);

        return new MyViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterCategory.MyViewHolder holder, int position) {

        if (_status){
            if (_notesData.getCatId()!=_list.get(position).getcId()){
                holder.tv_name.setText(_list.get(position).getcName());
            }
        }else{
            holder.tv_name.setText(_list.get(position).getcName());
        }
        holder.rm_cat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _db.deleteCategory(new CategoryData(_list.get(position).getcId(),_list.get(position).getcName()),listener);
            }
        });
        holder.rv_base_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (_status){
                _db.moveNotes(_notesData,String.valueOf(_list.get(position).getcId()));
                Toast.makeText(_context,"Moved!!",Toast.LENGTH_SHORT).show();
                ((Activity)_context).finish();
            }else {
                Intent intent = new Intent(_context, ActivityNotesList.class);
                intent.putExtra("catId", String.valueOf(_list.get(position).getcId()));
                _context.startActivity(intent);

            }}
        });
    }

    @Override
    public int getItemCount() {
        return _list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name;
        RelativeLayout rv_base_click;
        ImageView rm_cat;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            rv_base_click = itemView.findViewById(R.id.rv_base_click);
            rm_cat = itemView.findViewById(R.id.rm_cat);
        }
    }

    public interface onCatDelete{
        public void onCatRemove();
    }
}
