package com.cst2335.lab5;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Lab5_MainActivity";

    private List<TodoItem> todoList = new ArrayList<>();
    private TodoAdapter adapter;

    private ListView listView;
    private EditText etTodo;
    private SwitchCompat switchUrgent;
    private Button btnAdd;

    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.app_name);
        }

        listView = findViewById(R.id.list_view_todo);
        etTodo = findViewById(R.id.et_todo);
        switchUrgent = findViewById(R.id.switch_urgent);
        btnAdd = findViewById(R.id.btn_add);

        adapter = new TodoAdapter();
        listView.setAdapter(adapter);

        // Initialize and open database
        MyOpener opener = new MyOpener(this);
        db = opener.getWritableDatabase();

        // Load existing todos from database and print cursor info
        loadDataFromDatabase();

        // When Add is clicked: save to database, add to list, clear EditText, refresh adapter
        btnAdd.setOnClickListener(v -> {
            String text = etTodo.getText().toString();
            if (!text.trim().isEmpty()) {
                boolean isUrgent = switchUrgent.isChecked();

                ContentValues cValues = new ContentValues();
                cValues.put(MyOpener.COL_TODO, text);
                cValues.put(MyOpener.COL_IS_URGENT, isUrgent ? 1 : 0);
                long newId = db.insert(MyOpener.TABLE_NAME, null, cValues);

                todoList.add(new TodoItem(text, isUrgent, newId));
                etTodo.setText("");
                switchUrgent.setChecked(false);
                adapter.notifyDataSetChanged();
            }
        });

        // Set onItemLongClickListener to confirm deletion and delete from DB
        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle(R.string.dialog_title);
            builder.setMessage(getString(R.string.dialog_message, position));

            builder.setPositiveButton(R.string.btn_yes, (dialog, which) -> {
                TodoItem itemToDelete = todoList.get(position);
                db.delete(MyOpener.TABLE_NAME, MyOpener.COL_ID + "=?", new String[]{String.valueOf(itemToDelete.getId())});
                todoList.remove(position);
                adapter.notifyDataSetChanged();
            });

            builder.setNegativeButton(R.string.btn_no, (dialog, which) -> {
                dialog.dismiss();
            });

            builder.create().show();
            return true;
        });
    }

    /**
     * Loads all todos from SQLite database into the list and calls printCursor.
     */
    private void loadDataFromDatabase() {
        String[] columns = {MyOpener.COL_ID, MyOpener.COL_TODO, MyOpener.COL_IS_URGENT};
        Cursor cursor = db.query(MyOpener.TABLE_NAME, columns, null, null, null, null, null);

        printCursor(cursor);

        int idColIndex = cursor.getColumnIndex(MyOpener.COL_ID);
        int todoColIndex = cursor.getColumnIndex(MyOpener.COL_TODO);
        int urgentColIndex = cursor.getColumnIndex(MyOpener.COL_IS_URGENT);

        todoList.clear();
        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(idColIndex);
                String todoText = cursor.getString(todoColIndex);
                boolean isUrgent = cursor.getInt(urgentColIndex) == 1;

                todoList.add(new TodoItem(todoText, isUrgent, id));
            } while (cursor.moveToNext());
        }
        cursor.close();
        adapter.notifyDataSetChanged();
    }

    /**
     * Prints cursor details and database version to the Logcat window.
     */
    public void printCursor(Cursor c) {
        Log.i(TAG, "========== printCursor Debug Information ==========");
        Log.i(TAG, "Database Version: " + db.getVersion());
        Log.i(TAG, "Number of Columns in Cursor: " + c.getColumnCount());
        Log.i(TAG, "Column Names: " + Arrays.toString(c.getColumnNames()));
        Log.i(TAG, "Number of Results in Cursor: " + c.getCount());

        if (c.moveToFirst()) {
            int rowNumber = 1;
            do {
                StringBuilder rowString = new StringBuilder("Row " + rowNumber + ": ");
                for (int i = 0; i < c.getColumnCount(); i++) {
                    rowString.append(c.getColumnName(i))
                            .append(" = ")
                            .append(c.getString(i))
                            .append(" | ");
                }
                Log.i(TAG, rowString.toString());
                rowNumber++;
            } while (c.moveToNext());
        } else {
            Log.i(TAG, "Cursor is empty (0 rows).");
        }
        Log.i(TAG, "===================================================");
    }

    /**
     * Custom BaseAdapter for ToDo items.
     */
    private class TodoAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return todoList.size();
        }

        @Override
        public Object getItem(int position) {
            return todoList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return todoList.get(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_todo, parent, false);
            }

            TodoItem item = (TodoItem) getItem(position);
            TextView tv = view.findViewById(R.id.tv_todo_text);
            tv.setText(item.getText());

            if (item.isUrgent()) {
                view.setBackgroundColor(Color.RED);
                tv.setTextColor(Color.WHITE);
            } else {
                view.setBackgroundColor(Color.WHITE);
                tv.setTextColor(Color.BLACK);
            }

            return view;
        }
    }
}
