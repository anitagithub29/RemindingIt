package com.anaremind.remindingit;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class MainActivity extends ListActivity {
    private static final int ACTIVITY_CREATE=0;
    private static final int ACTIVITY_EDIT=1;

    private static final int INSERT_ID = Menu.FIRST;
    private static final int DELETE_ID = Menu.FIRST + 1;

    private DbAdapter mDbHelper;
    private Cursor mNotesCursor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDbHelper = new DbAdapter(this);
        mDbHelper.open();
        fillData();
        registerForContextMenu(getListView());
    }

    private void fillData() {

        mNotesCursor = mDbHelper.fetchAllNotes();
        startManagingCursor(mNotesCursor);

        // Create an array to specify the fields we want to display in the list (only TITLE)
        String[] from = new String[]{DbAdapter.KEY_TITLE};

        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] to = new int[]{R.id.text1};

        // Now create a simple cursor adapter and set it to display
        SimpleCursorAdapter notes =
                new SimpleCursorAdapter(this, R.layout.notes_row, mNotesCursor, from, to);
        setListAdapter(notes);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, INSERT_ID, 0, R.string.menu_insert);
                return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
            case INSERT_ID:
                createNote();
                return true;
        }

        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, DELETE_ID, 0, R.string.menu_delete);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case DELETE_ID:
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                mDbHelper.deleteNote(info.id);
                fillData();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    private void createNote() {
        Intent i = new Intent(this, NotesChange.class);
        startActivityForResult(i, ACTIVITY_CREATE);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Cursor c = mNotesCursor;
        c.moveToPosition(position);
        Intent i = new Intent(this, NotesChange.class);
        i.putExtra(DbAdapter.KEY_ROWID, id);
        i.putExtra(DbAdapter.KEY_TITLE, c.getString(
                c.getColumnIndexOrThrow(DbAdapter.KEY_TITLE)));
        i.putExtra(DbAdapter.KEY_BODY, c.getString(
                c.getColumnIndexOrThrow(DbAdapter.KEY_BODY)));
        startActivityForResult(i, ACTIVITY_EDIT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Bundle extras = intent.getExtras();
        switch(requestCode) {
            case ACTIVITY_CREATE:
                String title = extras.getString(DbAdapter.KEY_TITLE);
                String body = extras.getString(DbAdapter.KEY_BODY);
                mDbHelper.createNote(title, body);
                fillData();
                break;
            case ACTIVITY_EDIT:
                Long rowId = extras.getLong(DbAdapter.KEY_ROWID);
                if (rowId != null) {
                    String editTitle = extras.getString(DbAdapter.KEY_TITLE);
                    String editBody = extras.getString(DbAdapter.KEY_BODY);
                    mDbHelper.updateNote(rowId, editTitle, editBody);
                }
                fillData();
                break;
        }
    }
}

