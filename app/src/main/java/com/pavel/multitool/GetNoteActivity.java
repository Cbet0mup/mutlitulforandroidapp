package com.pavel.multitool;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.pavel.multitool.noteFileSupplement.DbHelper;
import com.pavel.multitool.noteFileSupplement.RecycleViewDataAdapter;
import com.pavel.multitool.noteFileSupplement.TextTableModel;

import java.util.ArrayList;
import java.util.List;

public class GetNoteActivity extends AppCompatActivity {

    DbHelper dbHelper;
    List<TextTableModel> allNote = new ArrayList<>();

    private RecyclerView notesList;
    private RecycleViewDataAdapter dataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_note);

        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_get_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_note_trash:
                Intent intent = new Intent(this, TrashActivity.class);
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void addNote(View view) {
        Intent intent = new Intent(this, AddNoteActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        //обновляем табличку
        init();
    }

    void init() {
        notesList = findViewById(R.id.rv_notes);

        dbHelper = new DbHelper(this);
        allNote = dbHelper.getAllNotes(dbHelper.TABLE_NOTES_TEXT);

        //менеджер отображения последовательного списка
        //передаётся recycleview для обработки
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        notesList.setLayoutManager(layoutManager);

        notesList.setHasFixedSize(true); //фиксированный размер

        dataAdapter = new RecycleViewDataAdapter(allNote, this);
        notesList.setAdapter(dataAdapter); //назначаем адаптер нашему recycleview
    }
}