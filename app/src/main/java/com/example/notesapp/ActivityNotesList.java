package com.example.notesapp;

import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notesapp.adapter.AdapterCategory;
import com.example.notesapp.adapter.AdapterNotes;
import com.example.notesapp.data.CategoryData;
import com.example.notesapp.data.NotesData;
import com.example.notesapp.database.DatabaseHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static java.lang.String.CASE_INSENSITIVE_ORDER;

public class ActivityNotesList extends AppCompatActivity {
    RecyclerView rv_notes_list;
    DatabaseHandler db;
    ArrayList<NotesData> listData=new ArrayList<>();
    ImageView btn_add_notes,btn_sort;
    AdapterNotes adapterNotes;
    EditText et_search;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_list);
        rv_notes_list=findViewById(R.id.rv_notes_list);
        btn_add_notes=findViewById(R.id.btn_add_notes);
        btn_sort=findViewById(R.id.btn_sort);
        et_search=findViewById(R.id.et_search);

        db=new DatabaseHandler(this);
        initView();

        btn_add_notes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ActivityNotesList.this, ActivityAddNote.class);
                intent.putExtra("catId",getIntent().getStringExtra("catId"));
                startActivity(intent);
            }
        });
        btn_sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                listData.sort(String::compareToIgnoreCase);
                Collections.sort(listData, new Comparator<NotesData>() {
                    @Override
                    public int compare(NotesData item, NotesData t1) {
                        String s1 = item.getTitle();
                        String s2 = t1.getTitle();
                        return s1.compareToIgnoreCase(s2);
                    }

                });
                adapterNotes.notifyDataSetChanged();
            }
        });

        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length()==0){

                    getAllNotes(getIntent().getStringExtra("catId"));
                }else{
                    searchText(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAllNotes(getIntent().getStringExtra("catId"));
    }

    private void initView(){
        adapterNotes = new AdapterNotes(this,listData,db);
        rv_notes_list.setHasFixedSize(true);
        rv_notes_list.setLayoutManager(new LinearLayoutManager(this));
        rv_notes_list.setAdapter(adapterNotes);
    }
    private void getAllNotes(String catId){
        List<NotesData> data = db.getAllNotes(catId);
        listData.clear();
        listData.addAll(data);
        adapterNotes.notifyDataSetChanged();
    }
    private ArrayList<NotesData> searchText(String text){
        ArrayList<NotesData> list=new ArrayList<>();
        for(NotesData d : listData){
            if(d.getTitle() != null && d.getTitle().contains(text)){
                list.add(d);
            }
            //something here
        }
        listData.clear();
        listData.addAll(list);
        adapterNotes.notifyDataSetChanged();
        return list;
    }
}
