package com.pavel.multitool.noteFileSupplement;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pavel.multitool.R;

import java.util.List;

public class RecycleViewTrashDataAdapter extends RecyclerView.Adapter<RecycleViewTrashDataAdapter.ViewHolder> {

    // принимаем на вход коллекцию
    private List<TextTableModel> itemNotes;
    private Context parent;

    public RecycleViewTrashDataAdapter(List<TextTableModel> notes, Context parent) {
        this.itemNotes = notes;
        this.parent = parent;
    }

    @NonNull
    @Override
    public RecycleViewTrashDataAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.list_note_view; // xml layout id помещаем в переменную

        LayoutInflater inflater = LayoutInflater.from(context); //класс который позволяет из xml создавать новые представления
        // создаём новый элемент списка
        View view = inflater.inflate(layoutIdForListItem, parent, false);

        //оборачиваем его во вьюхолдер чтобы использовать многократно и возвращаем

        return new RecycleViewTrashDataAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleViewTrashDataAdapter.ViewHolder holder, int position) {
        //достаём экземпляр класса модели из коллекции
        TextTableModel textTableModel = itemNotes.get(position);
        //распихиваем взятые с него данные по вьюшкам
        holder.listItemTitle.setText(textTableModel.getTitle());
        holder.bodyItem.setText(textTableModel.getBody());
    }

    @Override
    public int getItemCount() {
        return itemNotes.size();
    }

    //класс вьюхолдера
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView listItemTitle;  //заголовок
        TextView bodyItem;       // записка

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            listItemTitle = itemView.findViewById(R.id.title_note);
            bodyItem = itemView.findViewById(R.id.body_note);

            //подключим слушатель кликов

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {

                    final AlertDialog.Builder builder = new AlertDialog.Builder(parent);
                    builder.setMessage(R.string.dialog_delete_update);
                    builder.setCancelable(true);

                    builder.setPositiveButton("Дезинтегрировать", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int positionIndex = getAdapterPosition();

                            TextTableModel note;
                            note = itemNotes.get(positionIndex);

                            DbHelper db = new DbHelper(parent);
                            db.deleteNote(note, db.TABLE_TRASH_TEXT);
                            db.close();
                            Toast toast = Toast.makeText(parent, "Записка отправлена Ктулху", Toast.LENGTH_SHORT);
                            toast.show();

                            //этот кусок кода удаляет из списка ненужный объект и обновляет вьюху
                            itemNotes.remove(positionIndex);
                            notifyItemRemoved(positionIndex);
                            notifyItemRangeChanged(positionIndex, itemNotes.size());

                            dialog.dismiss();
                        }
                    });

                    builder.setNeutralButton("Реабилитировать", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int positionIndex = getAdapterPosition();

                            TextTableModel note;
                            note = itemNotes.get(positionIndex);

                            DbHelper db = new DbHelper(parent);
                            db.addNote(note, db.TABLE_NOTES_TEXT);
                            db.deleteNote(note, db.TABLE_TRASH_TEXT);
                            db.close();
                            Toast toast = Toast.makeText(parent, "Записка реабилитированна", Toast.LENGTH_SHORT);
                            toast.show();

                            //этот кусок кода удаляет из списка ненужный объект и обновляет вьюху
                            itemNotes.remove(positionIndex);
                            notifyItemRemoved(positionIndex);
                            notifyItemRangeChanged(positionIndex, itemNotes.size());

                            dialog.dismiss();

                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();//старт


                }
            });
        }

    }
}
