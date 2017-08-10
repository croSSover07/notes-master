package com.materialnotes.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.materialnotes.R;
import com.materialnotes.data.Note;
import com.materialnotes.data.dao.NoteDAO;
import com.materialnotes.widget.AboutNoticeDialog;
import com.materialnotes.widget.NotesAdapter;
import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.shamanland.fab.FloatingActionButton;

import java.util.ArrayList;

import javax.inject.Inject;

import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_notes)
public class NotesActivity extends RoboActionBarActivity {

    private static final int NEW_NOTE_RESULT_CODE = 4;
    private static final int EDIT_NOTE_RESULT_CODE = 5;
    private static final int NOTE_RESULT_CODE = 6;
    private Drawer.Result drawerResult = null;

    @InjectView(android.R.id.empty)   private TextView emptyListTextView;
    @InjectView(android.R.id.list)    private ListView listView;
    @InjectView(R.id.add_note_button) private FloatingActionButton addNoteButton;

    @Inject private NoteDAO noteDAO;

    private ArrayList<Integer> selectedPositions;
    private ArrayList<NotesAdapter.NoteViewWrapper> notesData;
    private NotesAdapter listAdapter;
    private ActionMode.Callback actionModeCallback;
    private ActionMode actionMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawerResult = new Drawer()
                .withActivity(this)
                .withDrawerWidthDp(300)
                .withToolbar(toolbar)
                .withSelectedItem(-1)
                .withActionBarDrawerToggle(true)
                .withHeader(R.layout.drawer_header)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.drawer_item_home).withIcon(FontAwesome.Icon.faw_home).withIdentifier(1),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_notes).withIcon(FontAwesome.Icon.faw_newspaper_o).withIdentifier(2),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_todo).withIcon(FontAwesome.Icon.faw_check_square).withIdentifier(3),
//                        new PrimaryDrawerItem().withName(R.string.drawer_item_schedule).withIcon(FontAwesome.Icon.faw_list).withIdentifier(4),
                        new DividerDrawerItem(),
                        //new SecondaryDrawerItem().withName(R.string.drawer_item_settings).withIcon(FontAwesome.Icon.faw_cog).withIdentifier(5),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_about).withIcon(FontAwesome.Icon.faw_info).withIdentifier(4)
                )
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View drawerView) {
                        InputMethodManager inputMethodManager = (InputMethodManager) NotesActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(NotesActivity.this.getCurrentFocus().getWindowToken(), 0);
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                    }
                })
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    // Обработка клика
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
                        try{
                        switch(drawerItem.getIdentifier())
                        {
                            case 1:
                                Intent intent1 = new Intent(NotesActivity.this, MainActivity.class);
                                startActivityForResult(intent1, NOTE_RESULT_CODE);
                                finish();
                                break;

                            case 2:
                                Intent intent2 = new Intent(NotesActivity.this, NotesActivity.class);
                                startActivityForResult(intent2, NOTE_RESULT_CODE);
                                break;

                            case 3:
                                Intent intent3 = new Intent(NotesActivity.this, ToDoActivity.class);
                                startActivityForResult(intent3, NOTE_RESULT_CODE);
                                break;

                            case 4:
                                new AboutNoticeDialog().show(getSupportFragmentManager(), "dialog_about_notice");
                                break;
                        }
                    }
                    catch(NullPointerException e) {

                    }
                    }
                }).build();

        //listView.setOnTouchListener(new ShowHideOnScroll(addNoteButton, getSupportActionBar()));
        addNoteButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                startActivityForResult(EditNoteActivity.buildIntent(NotesActivity.this), NEW_NOTE_RESULT_CODE);
            }
        });
        selectedPositions = new ArrayList<>();
        setupNotesAdapter();
        setupActionModeCallback();
        setListOnItemClickListenersWhenNoActionMode();
        updateView();
    }

    @Override
    public void onBackPressed() {
        if (drawerResult.isDrawerOpen()==true) {
            drawerResult.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NEW_NOTE_RESULT_CODE) {
            if (resultCode == RESULT_OK) addNote(data);
        }
        if (requestCode == EDIT_NOTE_RESULT_CODE) {
            if (resultCode == RESULT_OK) updateNote(data);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setupActionModeCallback() {
        actionModeCallback = new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                setListOnItemClickListenersWhenActionMode();
                mode.getMenuInflater().inflate(R.menu.context_note, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_delete:
                        if (!selectedPositions.isEmpty()) {
                            new AlertDialog.Builder(NotesActivity.this)
                                    .setMessage(getString(R.string.delete_notes_alert, selectedPositions.size()))
                                    .setNegativeButton(android.R.string.no, null)
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            deleteNotes(selectedPositions);
                                            mode.finish();
                                        }
                                    })
                                    .show();
                        } else mode.finish();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                setListOnItemClickListenersWhenNoActionMode();
                resetSelectedListItems();
            }
        };
    }

    private void setupNotesAdapter() {
        notesData = new ArrayList<>();
        for (Note note : noteDAO.fetchAll()) {
            NotesAdapter.NoteViewWrapper noteViewWrapper = new NotesAdapter.NoteViewWrapper(note);
            notesData.add(noteViewWrapper);
        }
        listAdapter = new NotesAdapter(notesData);
        listView.setAdapter(listAdapter);
    }

    private void updateView() {
        if (notesData.isEmpty()) {
            listView.setVisibility(View.GONE);
            emptyListTextView.setVisibility(View.VISIBLE);
        } else {
            listView.setVisibility(View.VISIBLE);
            emptyListTextView.setVisibility(View.GONE);
        }
    }

    private void addNote(Intent data) {
        Note note = EditNoteActivity.getExtraNote(data);
        noteDAO.insert(note);
        NotesAdapter.NoteViewWrapper noteViewWrapper = new NotesAdapter.NoteViewWrapper(note);
        notesData.add(noteViewWrapper);
        updateView();
        listAdapter.notifyDataSetChanged();
        setupNotesAdapter();
    }

    private void deleteNotes(ArrayList<Integer> selectedPositions) {
        ArrayList<NotesAdapter.NoteViewWrapper> toRemoveList = new ArrayList<>(selectedPositions.size());
        for (int position : selectedPositions) {
            NotesAdapter.NoteViewWrapper noteViewWrapper = notesData.get(position);
            toRemoveList.add(noteViewWrapper);
            noteDAO.delete(noteViewWrapper.getNote());
        }
        for (NotesAdapter.NoteViewWrapper noteToRemove : toRemoveList) notesData.remove(noteToRemove);
        updateView();
        listAdapter.notifyDataSetChanged();
    }

    private void updateNote(Intent data) {
        Note updatedNote = ViewNoteActivity.getExtraUpdatedNote(data);
        noteDAO.update(updatedNote);
        for (NotesAdapter.NoteViewWrapper noteViewWrapper : notesData) {
            if (noteViewWrapper.getNote().getId().equals(updatedNote.getId())) {
                noteViewWrapper.getNote().setTitle(updatedNote.getTitle());
                noteViewWrapper.getNote().setContent(updatedNote.getContent());
                noteViewWrapper.getNote().setUpdatedAt(updatedNote.getUpdatedAt());
            }
        }
        listAdapter.notifyDataSetChanged();
    }

    private void resetSelectedListItems() {
        for (NotesAdapter.NoteViewWrapper noteViewWrapper : notesData) noteViewWrapper.setSelected(false);
        selectedPositions.clear();
        listAdapter.notifyDataSetChanged();
    }

    private void setListOnItemClickListenersWhenNoActionMode() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivityForResult(ViewNoteActivity.buildIntent(NotesActivity.this, notesData.get(position).getNote()), EDIT_NOTE_RESULT_CODE);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                notesData.get(position).setSelected(true);
                listAdapter.notifyDataSetChanged();
                selectedPositions.add(position);
                actionMode = startSupportActionMode(actionModeCallback);
                actionMode.setTitle(String.valueOf(selectedPositions.size()));
                return true;
            }
        });
    }

    private void setListOnItemClickListenersWhenActionMode() {
        listView.setOnItemLongClickListener(null);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (selectedPositions.contains(position)) {
                    selectedPositions.remove((Object)position);
                    if (selectedPositions.isEmpty()) actionMode.finish();
                    else {
                        actionMode.setTitle(String.valueOf(selectedPositions.size()));
                        notesData.get(position).setSelected(false);
                        listAdapter.notifyDataSetChanged();
                    }
                } else {
                    notesData.get(position).setSelected(true);
                    listAdapter.notifyDataSetChanged();
                    selectedPositions.add(position);
                    actionMode.setTitle(String.valueOf(selectedPositions.size()));
                }
            }
        });
    }
}