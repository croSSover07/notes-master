package com.materialnotes.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;

import com.materialnotes.R;
import com.materialnotes.widget.AboutNoticeDialog;
import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

public class MainActivity extends ActionBarActivity {
    private static final int NOTE_RESULT_CODE = 6;
    private Drawer.Result drawerResult = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawerResult = new Drawer()
                .withActivity(this)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(true)
                .withDrawerWidthDp(300)
                .withHeader(R.layout.drawer_header)
                .withSelectedItem(-1)
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
                        InputMethodManager inputMethodManager = (InputMethodManager) MainActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(MainActivity.this.getCurrentFocus().getWindowToken(), 0);
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                    }
                })
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    // Обработка клика
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
                        try {
                            switch (drawerItem.getIdentifier()) {
                                case 1:
                                    Intent intent1 = new Intent(MainActivity.this, MainActivity.class);
                                    startActivityForResult(intent1, NOTE_RESULT_CODE);
                                    break;

                                case 2:
                                    Intent intent2 = new Intent(MainActivity.this, NotesActivity.class);
                                    startActivityForResult(intent2, NOTE_RESULT_CODE);
                                    break;

                                case 3:
                                    Intent intent3 = new Intent(MainActivity.this, ToDoActivity.class);
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
    }

    @Override
    public void onBackPressed() {
        if (drawerResult.isDrawerOpen()) {
            drawerResult.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        new AboutNoticeDialog().show(getSupportFragmentManager(), "dialog_about_notice");
        return true;
    }
}