package com.pavel.multitool;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.pm.ActivityInfo;
import android.os.Bundle;

import com.pavel.multitool.noteFileSupplement.DbHelper;
import com.pavel.multitool.noteFileSupplement.RecycleViewTrashDataAdapter;
import com.pavel.multitool.noteFileSupplement.TextTableModel;

import java.util.List;

public class TrashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trash);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        RecyclerView notesList = findViewById(R.id.rv_notes_trash);

        DbHelper dbHelper = new DbHelper(this);
        List<TextTableModel> allNote = dbHelper.getAllNotes(dbHelper.TABLE_TRASH_TEXT);

        //менеджер отображения последовательного списка
        //передаётся recycleview для обработки
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        notesList.setLayoutManager(layoutManager);

        notesList.setHasFixedSize(true); //фиксированный размер

        RecycleViewTrashDataAdapter dataAdapter = new RecycleViewTrashDataAdapter(allNote, this);
        notesList.setAdapter(dataAdapter); //назначаем адаптер нашему recycleview
    }

    @Override
    public void recreate() {
        super.recreate();
    }
}