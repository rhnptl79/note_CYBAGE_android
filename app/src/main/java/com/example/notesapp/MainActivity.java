package com.example.notesapp;

import android.Manifest;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notesapp.adapter.AdapterCategory;
import com.example.notesapp.data.CategoryData;
import com.example.notesapp.data.NotesData;
import com.example.notesapp.database.DatabaseHandler;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterCategory.onCatDelete {
    RecyclerView rv_category;
    ImageView btnAddCategory;
    DatabaseHandler db;
    AdapterCategory adapterCategory;
    ArrayList<CategoryData> listData = new ArrayList<>();
    boolean status;
    NotesData notesData;
    String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.RECORD_AUDIO};

    int permsRequestCode = 200;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_main);
        rv_category = findViewById(R.id.rv_category);
        btnAddCategory = findViewById(R.id.btn_add_catogory);
        requestPermissions(perms,101);

        db = new DatabaseHandler(this);

        btnAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        if (getIntent().hasExtra("from")) {
            notesData = (NotesData) getIntent().getSerializableExtra("noteData");
            btnAddCategory.setVisibility(View.GONE);
            status = true;
        } else {
            status = false;
        }
        initView();
        getAllCategory();
    }

    private void initView() {
        adapterCategory = new AdapterCategory(this, listData, status, notesData, db,this);
        rv_category.setHasFixedSize(true);
        rv_category.setLayoutManager(new LinearLayoutManager(this));
        rv_category.setAdapter(adapterCategory);
    }

    private void getAllCategory() {
        List<CategoryData> categoryData = db.getAllCategory();
        listData.clear();
        listData.addAll(categoryData);
        if (getIntent().hasExtra("from")) {
            int pos = -1;
            for (int i = 0; i < listData.size(); i++) {
                if (notesData.getCatId() == listData.get(i).getcId()) {
                    pos = i;
                }
            }
            if (pos != -1)
                listData.remove(pos);
        }

        adapterCategory.notifyDataSetChanged();
    }


    public void showDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.add_category_custom_dialog);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        EditText text = dialog.findViewById(R.id.et_name);


        TextView btnCancel = dialog.findViewById(R.id.btn_cancel);
        TextView btnAdd = dialog.findViewById(R.id.btn_add);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.addCategory(new CategoryData(1, text.getText().toString()));
                dialog.dismiss();
                getAllCategory();
            }
        });

        dialog.show();

    }

    @Override
    public void onCatRemove() {
        getAllCategory();
    }
}