package com.pavel.multitool;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.pavel.multitool.noteFileSupplement.DbHelper;
import com.pavel.multitool.noteFileSupplement.TextTableModel;

public class RedactNoteActivity extends AppCompatActivity {

    private int getId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        final EditText title, body;
        final String getTitle, getBody;

        Button saveButton;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redact_note);

        title = findViewById(R.id.redact_text_title);
        body = findViewById(R.id.redact_text_body);

        saveButton = findViewById(R.id.redact_button_save);

        Bundle args = getIntent().getExtras();
        if (args != null){
            TextTableModel model = (TextTableModel) args.getSerializable(TextTableModel.class.getSimpleName());
            getId = model.getId();
            getTitle = model.getTitle();
            getBody = model.getBody();

            title.setText(getTitle);
            body.setText(getBody);
        }

//кнопка сохранить
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newTitle, newBody;

                newTitle = title.getText().toString();
                newBody = body.getText().toString();

                if (newTitle.length() < 3 || newBody.length() < 3) {            //проверка длинны записей
                    Toast toast = Toast.makeText(getApplicationContext(), "напиши хоть чёнить, прежде чем сохранять", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    DbHelper db = new DbHelper(v.getContext());
                    //создадим обновлённый объект
                    TextTableModel note = new TextTableModel(newTitle, newBody);
                    note.setId(getId);
                    // и сохраним
                    int res = db.updateNote(note);

                    if (res == 1) {
                        Toast toast = Toast.makeText(v.getContext(), "Записка обновлена", Toast.LENGTH_SHORT);
                        toast.show();

                        finish();

                    } else {
                        Toast toast = Toast.makeText(v.getContext(), "Ошибка", Toast.LENGTH_LONG);
                        toast.show();
                    }
                }
            }
        });
    }


}