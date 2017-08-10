package com.materialnotes.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.TextView;

import com.materialnotes.R;
import com.materialnotes.data.Note;
import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.shamanland.fab.FloatingActionButton;

import java.text.DateFormat;

import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_view_note)
public class ViewNoteActivity extends RoboActionBarActivity {

    private static final int EDIT_NOTE_RESULT_CODE = 8;
    private static final String EXTRA_NOTE = "EXTRA_NOTE";
    private static final String EXTRA_UPDATED_NOTE = "EXTRA_UPDATED_NOTE";
    private static final DateFormat DATETIME_FORMAT = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);

    @InjectView(R.id.edit_note_button)     private FloatingActionButton editNoteButton;
    @InjectView(R.id.note_title)           private TextView noteTitleText;
    @InjectView(R.id.note_content)         private TextView noteContentText;
    @InjectView(R.id.note_created_at_date) private TextView noteCreatedAtDateText;
    @InjectView(R.id.note_updated_at_date) private TextView noteUpdatedAtDateText;

    private Note note;

    public static Intent buildIntent(Context context, Note note) {
        Intent intent = new Intent(context, ViewNoteActivity.class);
        intent.putExtra(EXTRA_NOTE, note);
        return intent;
    }

    public static Note getExtraUpdatedNote(Intent intent) {
        return (Note) intent.getExtras().get(EXTRA_UPDATED_NOTE);
    }
    Drawer.Result drawerResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.toolbar_text_color));
        toolbar.setSubtitleTextColor(getResources().getColor(R.color.toolbar_text_color));
        setSupportActionBar(toolbar);
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawerResult = new Drawer()
                .withActivity(this)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(true)
                .withHeader(R.layout.drawer_header)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.drawer_item_home).withIcon(FontAwesome.Icon.faw_home),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_notes).withIcon(FontAwesome.Icon.faw_newspaper_o),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_todo).withIcon(FontAwesome.Icon.faw_check_square),
                        //   new PrimaryDrawerItem().withName(R.string.drawer_item_schedule).withIcon(FontAwesome.Icon.faw_list),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_settings).withIcon(FontAwesome.Icon.faw_cog),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_about).withIcon(FontAwesome.Icon.faw_info)
                )
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View drawerView) {
                        InputMethodManager inputMethodManager = (InputMethodManager) ViewNoteActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(ViewNoteActivity.this.getCurrentFocus().getWindowToken(), 0);
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                    }
                })
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                        @Override
                        // Обработка клика
                        public void onItemClick (AdapterView < ? > parent, View view,int position,
                        long id, IDrawerItem drawerItem){
                            try {
                                switch (drawerItem.getIdentifier()) {
                                    case 1:
                                        Intent intent1 = new Intent(ViewNoteActivity.this, MainActivity.class);
                                        startActivityForResult(intent1,0);
                                        finish();
                                        break;

                                    case 2:
                                        Intent intent2 = new Intent(ViewNoteActivity.this, NotesActivity.class);
                                        startActivityForResult(intent2, 0);
                                        finish();
                                        break;

                                    case 3:
                                        Intent intent3 = new Intent(ViewNoteActivity.this, ToDoActivity.class);
                                        startActivityForResult(intent3, 0);
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
                            } catch (NullPointerException e) {

                            }

                        }
                    }

                    ).

                    build();

                    // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    // scrollView.setOnTouchListener(new ShowHideOnScroll(editNoteButton, getSupportActionBar()));
                    editNoteButton.setOnClickListener(new View.OnClickListener()

                    {

                        @Override
                        public void onClick (View view){
                        startActivityForResult(EditNoteActivity.buildIntent(ViewNoteActivity.this, note), EDIT_NOTE_RESULT_CODE);
                    }
                    }

                    );

                    note=(Note)

                    getIntent()

                    .

                    getSerializableExtra(EXTRA_NOTE);

                    noteTitleText.setText(note.getTitle());
                    noteContentText.setText(note.getContent());
                    noteCreatedAtDateText.setText(DATETIME_FORMAT.format(note.getCreatedAt()));
                    noteUpdatedAtDateText.setText(DATETIME_FORMAT.format(note.getUpdatedAt()));
                }

        @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDIT_NOTE_RESULT_CODE) {
            if (resultCode == RESULT_OK) {
                Intent resultIntent = new Intent();
                Note note = EditNoteActivity.getExtraNote(data);
                resultIntent.putExtra(EXTRA_UPDATED_NOTE, note);
                setResult(RESULT_OK, resultIntent);
                finish();
            } else if (resultCode == RESULT_CANCELED) onBackPressed();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED, new Intent());
        finish();
    }
}