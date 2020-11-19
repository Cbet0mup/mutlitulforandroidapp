package com.pavel.multitool;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.pavel.multitool.noteFileSupplement.DbHelper;
import com.pavel.multitool.noteFileSupplement.TextTableModel;

public class AddNoteActivity extends AppCompatActivity {
    private EditText title, body;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
    }

    //сохраняем записку в бд
    public void saveNote(View view) {
        title = findViewById(R.id.add_title_new_note);
        body = findViewById(R.id.add_body_new_note);
        String getTitle = title.getText().toString();
        String getBody = body.getText().toString();
        if (getTitle.length() < 3 || getBody.length() < 3) {
            Toast toast = Toast.makeText(getApplicationContext(), "напиши хоть чёнить, прежде чем сохранять", Toast.LENGTH_SHORT);
            toast.show();
        } else {
            DbHelper db = new DbHelper(this);
            db.addNote(new TextTableModel(getTitle, getBody), db.TABLE_NOTES_TEXT);

            Intent intent = new Intent(this, GetNoteActivity.class);
            startActivity(intent);
            finish();
        }
    }


}