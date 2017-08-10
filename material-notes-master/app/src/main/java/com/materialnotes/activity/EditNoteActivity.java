package com.materialnotes.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Toast;

import com.materialnotes.R;
import com.materialnotes.data.Note;
import com.materialnotes.util.Strings;
import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.Date;

import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_edit_note)
public class EditNoteActivity extends RoboActionBarActivity {

    private static final String EXTRA_NOTE = "EXTRA_NOTE";

    @InjectView(R.id.note_title)   private EditText noteTitleText;
    @InjectView(R.id.note_content) private EditText noteContentText;

    private Note note;

    public static Intent buildIntent(Context context, Note note) {
        Intent intent = new Intent(context, EditNoteActivity.class);
        intent.putExtra(EXTRA_NOTE, note);
        return intent;
    }

    public static Intent buildIntent(Context context) {
        return buildIntent(context, null);
    }

    public static Note getExtraNote(Intent intent) {
        return (Note) intent.getExtras().get(EXTRA_NOTE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.toolbar_text_color));
        setSupportActionBar(toolbar);
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        new Drawer()
                .withActivity(this)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(true)
                .withHeader(R.layout.drawer_header)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.drawer_item_home).withIcon(FontAwesome.Icon.faw_home),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_notes).withIcon(FontAwesome.Icon.faw_newspaper_o),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_todo).withIcon(FontAwesome.Icon.faw_check_square),
                        //new PrimaryDrawerItem().withName(R.string.drawer_item_schedule).withIcon(FontAwesome.Icon.faw_list),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_settings).withIcon(FontAwesome.Icon.faw_cog),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_about).withIcon(FontAwesome.Icon.faw_info)
                )
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View drawerView) {
                        // Скрываем клавиатуру при открытии Navigation Drawer
                        InputMethodManager inputMethodManager = (InputMethodManager) EditNoteActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(EditNoteActivity.this.getCurrentFocus().getWindowToken(), 0);
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
                                        Intent intent1 = new Intent( EditNoteActivity.this, MainActivity.class);
                                        startActivityForResult(intent1, 0);
                                        finish();
                                        break;

                                    case 2:
                                        Intent intent2 = new Intent( EditNoteActivity.this, NotesActivity.class);
                                        startActivityForResult(intent2, 0);
                                        finish();
                                        break;

                                    case 3:
                                        Intent intent3 = new Intent( EditNoteActivity.this, ToDoActivity.class);
                                        startActivityForResult(intent3,0);
                                        finish();
                                        break;

                      /*      case 4:
                                Intent intent4 = new Intent( NotesActivity.this, NotesActivity.class);
                                startActivityForResult(intent4, NOTE_RESULT_CODE);
                                finish();
                                break;
*/
                                    case 5:
                                /*new AboutNoticeDialog()
                                        .show(getSupportFragmentManager(), "dialog_about_notice");*/
                                        break;
                                }
                            }
                            catch(NullPointerException e) {

                            }

                    }
                }).build();

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        note = (Note) getIntent().getSerializableExtra(EXTRA_NOTE);
        if (note != null) {
            noteTitleText.setText(note.getTitle());
            noteContentText.setText(note.getContent());
        } else {
            note = new Note();
            note.setCreatedAt(new Date());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_save:
                if (isNoteFormOk()) {
                    setNoteResult();
                    finish();
                } else validateNoteForm();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    private boolean isNoteFormOk() {
        return !Strings.isNullOrBlank(noteTitleText.getText().toString()) && !Strings.isNullOrBlank(noteContentText.getText().toString());
    }

    private void setNoteResult() {
        note.setTitle(noteTitleText.getText().toString().trim());
        note.setContent(noteContentText.getText().toString().trim());
        note.setUpdatedAt(new Date());
        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_NOTE, note);
        setResult(RESULT_OK, resultIntent);
    }

    private void validateNoteForm() {
        StringBuilder message = null;
        if (Strings.isNullOrBlank(noteTitleText.getText().toString())) {
            message = new StringBuilder().append(getString(R.string.title_required));
        }
        if (Strings.isNullOrBlank(noteContentText.getText().toString())) {
            if (message == null) message = new StringBuilder().append(getString(R.string.content_required));
            else message.append("\n").append(getString(R.string.content_required));
        }
        if (message != null) {
            Toast.makeText(getApplicationContext(),
                    message,
                    Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED, new Intent());
        finish();
    }
}