package com.materialnotes.activity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.materialnotes.R;
import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.shamanland.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.ContentView;

@ContentView(R.layout.activity_todo)
public class ToDoActivity extends RoboActionBarActivity {
    private PendingIntent pendingIntent;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    boolean dateOrdered, categoryOrdered, descriptionOrdered, allChecked;
    int ordering;
    String data , time;
    Context context;
    DatabaseHelper databaseHelper;
    TableLayout tableLayout;
    ArrayList<CheckBox> checkBoxes;
    CheckBox totalCheckBox;
    TextView orderByDate, orderByCategory, orderByDescription, clearButton;
    FloatingActionButton actionAdd;
    public AlarmManager am;
    public NotificationManager nm;
    public Long stamp;
    private static final int NOTE_RESULT_CODE = 6;
    Drawer.Result drawerResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.toolbar_text_color));
        setSupportActionBar(toolbar);
       // getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
  //                      new PrimaryDrawerItem().withName(R.string.drawer_item_schedule).withIcon(FontAwesome.Icon.faw_list).withIdentifier(4),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_settings).withIcon(FontAwesome.Icon.faw_cog).withIdentifier(5),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_about).withIcon(FontAwesome.Icon.faw_info).withIdentifier(6)
                )
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View drawerView) {
                        InputMethodManager inputMethodManager = (InputMethodManager) ToDoActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(ToDoActivity.this.getCurrentFocus().getWindowToken(), 0);
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
                                Intent intent1 = new Intent(ToDoActivity.this, MainActivity.class);
                                startActivityForResult(intent1, NOTE_RESULT_CODE);
                                finish();
                                break;

                            case 2:
                                Intent intent2 = new Intent(ToDoActivity.this, NotesActivity.class);
                                startActivityForResult(intent2, NOTE_RESULT_CODE);
                                finish();
                                break;

                            case 3:
                                Intent intent3 = new Intent(ToDoActivity.this, ToDoActivity.class);
                                startActivityForResult(intent3, NOTE_RESULT_CODE);
                                finish();
                                break;

               /*             case 4:
                                Intent intent4 = new Intent(ToDoActivity.this, AboutNoticeDialog.class);
                                startActivityForResult(intent4, NOTE_RESULT_CODE);
                                finish();
                                break;
*/
                            case 5:
                                /*Intent intent5 = new Intent(ToDoActivity.this, ToDoActivity.class);
                                startActivityForResult(intent5, NOTE_RESULT_CODE);
                                finish();*/
                                break;
                        }
                       }
                       catch(NullPointerException e) {

                       }
                    }
                }).build();


        databaseHelper = new DatabaseHelper(this);
        /*SQLiteDatabase wDb = databaseHelper.getWritableDatabase();
        SQLiteDatabase rDb = databaseHelper.getReadableDatabase();
        databaseHelper.onUpgrade(wDb,2, 3);
        databaseHelper.onUpgrade(rDb,2, 3);*/

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();
        ordering = sharedPreferences.getInt(getResources().getString(R.string.pref_key_ordering), 8);

        clearButton = (TextView) findViewById(R.id.clearAllTasks);
        tableLayout = (TableLayout) findViewById(R.id.list_table);
        totalCheckBox = (CheckBox) findViewById(R.id.select_all);
        actionAdd = (FloatingActionButton) findViewById(R.id.action_a);

        checkBoxes = new ArrayList<>();
        checkBoxes = new ArrayList<>();

        orderByDate = (TextView) findViewById(R.id.dateTitle);
        orderByCategory = (TextView) findViewById(R.id.categoryTitle);
        orderByDescription = (TextView) findViewById(R.id.descriptionTitle);

        databaseHelper = new DatabaseHelper(this);
        populateTable(DatabaseHelper.SELECT_ALL_QUERY);
        databaseHelper.printTableContents(Database.TasksTable.TABLE_NAME);
        orderTable();

        totalCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SQLiteDatabase readDb = databaseHelper.getReadableDatabase();
                Cursor cursor = readDb.rawQuery(DatabaseHelper.SELECT_ALL_QUERY, null);
                cursor.moveToFirst();
                int rows = cursor.getCount();

                if (isChecked) {
                    setAllChecked(true);
                    for (int i = 0; i < rows; i++) {
                        databaseHelper.editChecked(cursor.getInt(0), true);
                        cursor.moveToNext();
                    }
                } else {
                    setAllChecked(false);
                    for (int i = 0; i < rows; i++) {
                        databaseHelper.editChecked(cursor.getInt(0), false);
                        cursor.moveToNext();
                    }
                }
                tableLayout.invalidate();
                orderTable();
                readDb.close();
                cursor.close();
            }
        });


        orderByDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isDateOrdered()) {
                    editor.putInt(getResources().getString(R.string.pref_key_ordering), 1);
                    editor.apply();
                    setDateOrdered(true);
                    tableLayout.invalidate();
                    populateTable(DatabaseHelper.SELECT_BY_DATE_ASCENDING);
                    Toast.makeText(getBaseContext(), "Ordered by ascending date",
                            Toast.LENGTH_SHORT).show();
                } else {
                    editor.putInt(getResources().getString(R.string.pref_key_ordering), 0);
                    editor.apply();
                    setDateOrdered(false);
                    tableLayout.invalidate();
                    populateTable(DatabaseHelper.SELECT_BY_DATE_DESCENDING);
                    Toast.makeText(getBaseContext(), "Ordered by descending date",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        orderByCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isCategoryOrdered()) {
                    editor.putInt(getResources().getString(R.string.pref_key_ordering), 3);
                    editor.apply();
                    setCategoryOrdered(true);
                    tableLayout.invalidate();
                    populateTable(DatabaseHelper.SELECT_BY_CATEGORY_ASCENDING);
                    Toast.makeText(getBaseContext(), "Ordered by ascending category alphabetically",
                            Toast.LENGTH_SHORT).show();
                } else {
                    editor.putInt(getResources().getString(R.string.pref_key_ordering), 2);
                    editor.apply();
                    setCategoryOrdered(false);
                    tableLayout.invalidate();
                    populateTable(DatabaseHelper.SELECT_BY_CATEGORY_DESCENDING);
                    Toast.makeText(getBaseContext(), "Ordered by descending category alphabetically",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        orderByDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isDescriptionOrdered()){
                    editor.putInt(getResources().getString(R.string.pref_key_ordering), 5);
                    editor.apply();
                    setDescriptionOrdered(true);
                    tableLayout.invalidate();
                    populateTable(DatabaseHelper.SELECT_BY_DESCRIPTION_ASCENDING);
                    Toast.makeText(getBaseContext(), "Ordered by ascending description alphabetically",
                            Toast.LENGTH_SHORT).show();
                } else {
                    editor.putInt(getResources().getString(R.string.pref_key_ordering), 4);
                    editor.apply();
                    setDescriptionOrdered(false);
                    tableLayout.invalidate();
                    populateTable(DatabaseHelper.SELECT_BY_DESCRIPTION_DESCENDING);
                    Toast.makeText(getBaseContext(), "Ordered by descending description alphabetically",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseHelper.removeAllTasks();
                tableLayout.invalidate();
                populateTable(DatabaseHelper.SELECT_ALL_QUERY);

            }
        });

        actionAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AddTaskDialog addTaskDialog = new AddTaskDialog();
                addTaskDialog.show(ToDoActivity.this.getFragmentManager(), "setAddDialogListener");
                addTaskDialog.setAddDialogListener(new AddTaskDialog.setAddTaskListener() {
                    @Override
                    public void onDoneClick(DialogFragment dialogFragment) {
                        if (addTaskDialog.getDateField().matches("") &&
                                addTaskDialog.getTimeField().matches("") &&
                                addTaskDialog.getCategory().matches("") &&
                                addTaskDialog.getDescription().matches("")) {
                            Toast.makeText(ToDoActivity.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                        } else {
                            if (addTaskDialog.getDateField().matches("")) {
                                Toast.makeText(ToDoActivity.this, "Please add a date",
                                        Toast.LENGTH_SHORT).show();
                            }
                            if (addTaskDialog.getTimeField().matches("")) {
                                Toast.makeText(ToDoActivity.this, "Please add a time",
                                        Toast.LENGTH_SHORT).show();
                            }
                            if (!addTaskDialog.getTimeField().matches("^(([0,1][0-9])|(2[0-3])):[0-5][0-9]$")) {
                                Toast.makeText(ToDoActivity.this, "Please add a correct time",
                                        Toast.LENGTH_SHORT).show();
                            }
                            if (addTaskDialog.getCategory().matches("")) {
                                Toast.makeText(ToDoActivity.this, "Please add a category",
                                        Toast.LENGTH_SHORT).show();
                            }
                            if (addTaskDialog.getDescription().matches("")) {
                                Toast.makeText(ToDoActivity.this, "Please add a description",
                                        Toast.LENGTH_SHORT).show();
                            }
                            if (!addTaskDialog.getDateField().matches("")
                                    && !addTaskDialog.getTimeField().matches("")
                                    && !addTaskDialog.getCategory().matches("")
                                    && !addTaskDialog.getDescription().matches("")
                                    && addTaskDialog.getTimeField().matches("^(([0,1][0-9])|(2[0-3])):[0-5][0-9]$")) {
                                databaseHelper.insertTask(addTaskDialog.getCategory(),
                                        addTaskDialog.getDescription(),
                                        addTaskDialog.getDate(),
                                        addTaskDialog.getRawDate(),
                                        false,
                                        addTaskDialog.getPriority(),
                                        addTaskDialog.getNotification(),
                                        addTaskDialog.getTime());
                                databaseHelper.printTableContents(Database.TasksTable.TABLE_NAME);
                                tableLayout.invalidate();
                                populateTable(DatabaseHelper.SELECT_ALL_QUERY);
                                Toast.makeText(ToDoActivity.this, "Task Added Successfully!",
                                        Toast.LENGTH_SHORT).show();

                                if (addTaskDialog.getNotification()) {

                                    String id = getData();

                                   // NotificationHandler notification = new NotificationHandler(ToDoActivity.this);
                                    SQLiteDatabase readDb = databaseHelper.getReadableDatabase();
                                    Cursor cursor = readDb.rawQuery(DatabaseHelper.SELECT_ALL_QUERY, null);
                                    for (int i = 1; i <= cursor.getCount(); i++) {
                                        setData(i, DatabaseHelper.COL_DUE_DATE);
                                        String date = getData();
                                        setData(i, DatabaseHelper.COL_DESCRIPTION);
                                        String description = getData();
                                        setData(i, DatabaseHelper.COL_N_TIME);
                                        String time = getData();
                                        //notification.showNotification( ""+i, date,time, description);
                                    //    SimpleDateFormat format1 = new SimpleDateFormat("HH:mm");
                                        Calendar calendar = Calendar.getInstance();
                                        calendar.set(Calendar.HOUR_OF_DAY , Integer.parseInt(time.substring(0,2)));
                                        calendar.set(Calendar.MINUTE, Integer.parseInt(time.substring(3,5)));
                                       // format1 = new SimpleDateFormat("dd/MM/yyyy");
                                      //  Calendar calendar2 = Calendar.getInstance();
                                        calendar.set(Calendar.DAY_OF_MONTH , Integer.parseInt(date.substring(0,2)));
                                        calendar.set(Calendar.MONTH, Integer.parseInt(date.substring(3,5)));
                                        calendar.set(Calendar.YEAR, Integer.parseInt(date.substring(6,10)));
                                        //stamp = (calendar.getTimeInMillis()+ calendar2.getTimeInMillis()) - System.currentTimeMillis();
                                       // restartNotify();
                                        alarmMethod(calendar);
                                    }
                                }
                            }
                        }   }
                });
            }
        });
    }

    public void setDateOrdered(boolean dateOrdered){this.dateOrdered=dateOrdered;}
    public boolean isDateOrdered(){return dateOrdered;}
    public void setCategoryOrdered(boolean categoryOrdered){this.categoryOrdered=categoryOrdered;}
    public boolean isCategoryOrdered(){return categoryOrdered;}
    public void setDescriptionOrdered(boolean descriptionOrdered){this.descriptionOrdered=descriptionOrdered;}
    public boolean isDescriptionOrdered(){return descriptionOrdered;}
    public void setAllChecked(boolean allChecked){this.allChecked=allChecked;}

    public void orderTable(){
        this.ordering = sharedPreferences.getInt(getResources().getString(R.string.pref_key_ordering), 8);
        switch (ordering){
            //date descending
            case 0:
                tableLayout.invalidate();
                populateTable(DatabaseHelper.SELECT_BY_DATE_DESCENDING);
                break;
            //date ascending
            case 1:
                tableLayout.invalidate();
                populateTable(DatabaseHelper.SELECT_BY_DATE_ASCENDING);
                break;
            //category descending
            case 2:
                tableLayout.invalidate();
                populateTable(DatabaseHelper.SELECT_BY_CATEGORY_DESCENDING);
                break;
            //category ascending
            case 3:
                tableLayout.invalidate();
                populateTable(DatabaseHelper.SELECT_BY_CATEGORY_ASCENDING);
                break;
            //description descending
            case 4:
                tableLayout.invalidate();
                populateTable(DatabaseHelper.SELECT_BY_DESCRIPTION_DESCENDING);
                break;
            //description ascending
            case 5:
                tableLayout.invalidate();
                populateTable(DatabaseHelper.SELECT_BY_DESCRIPTION_ASCENDING);
                break;
            //priority descending
            case 6:
                break;
            //priority ascending
            case 7:
                break;
            //by id ascending default
            default:
                tableLayout.invalidate();
                populateTable(DatabaseHelper.SELECT_ALL_QUERY);
                break;
        }
    }

    public void populateTable(String query) {
        SQLiteDatabase readDb = databaseHelper.getReadableDatabase();

        final int currNumRows = tableLayout.getChildCount();
        if (currNumRows > 1)
            tableLayout.removeViewsInLayout(1, currNumRows - 1);

        final Cursor cursor = readDb.rawQuery(query, null);
        int numRows = cursor.getCount();
        System.out.println("Row count " + numRows);
        cursor.moveToFirst();

        for (int i = 0; i < numRows; i++) {
            final TableRow tableRow = new TableRow(this);
            TableRow.LayoutParams tableRowParams =
                    new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
            tableRow.setLayoutParams(tableRowParams);


            final int row = cursor.getInt(DatabaseHelper.COL_ID);
            final TextView dateField = new TextView(this);
            dateField.setText(cursor.getString(DatabaseHelper.COL_DUE_DATE) + ", " + cursor.getString(DatabaseHelper.COL_N_TIME));
            dateField.setGravity(Gravity.CENTER);

            final TextView categoryField = new TextView(this);
            categoryField.setText(cursor.getString(DatabaseHelper.COL_CATEGORY));
            categoryField.setGravity(Gravity.CENTER);

            final TextView descField = new TextView(this);
            descField.setText(cursor.getString(DatabaseHelper.COL_DESCRIPTION));
            descField.setGravity(Gravity.CENTER);

            final TextView priField = new TextView(this);
            if(cursor.getInt(DatabaseHelper.COL_PRIORITY) == 1){
                priField.setText("!");
                priField.setTypeface(null, Typeface.BOLD);
            } else {
                priField.setText("");
            }
            priField.setGravity(Gravity.CENTER);

            if (i % 2 == 0) {
                dateField.setTextColor(Color.WHITE);
                dateField.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                categoryField.setTextColor(Color.WHITE);
                categoryField.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                descField.setTextColor(Color.WHITE);
                descField.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                priField.setTextColor(Color.WHITE);
                priField.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            }

            final CheckBox checkBox = new CheckBox(this);
            checkBox.setId(cursor.getInt(0));

            checkBoxes.add(checkBox);
            if(cursor.getInt(6) == 1){
                checkBox.setChecked(true);
                strikeThrough(dateField, categoryField, descField, priField, true);
                tableLayout.invalidate();
            } else {
                checkBox.setChecked(false);
                strikeThrough(dateField, categoryField, descField, priField, false);
                tableLayout.invalidate();
            }

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    databaseHelper.editChecked(row, isChecked);
                    strikeThrough(dateField, categoryField, descField, priField, isChecked);
                    databaseHelper.printTableContents(Database.TasksTable.TABLE_NAME);
                }
            });

            tableRow.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(
                            new ContextThemeWrapper(ToDoActivity.this, R.style.LongClickDialog));
                    builder.setMessage("Would you like to alter your to-do list?")
                            .setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    databaseHelper.removeTask(row);
                                    tableLayout.invalidate();
                                    populateTable(DatabaseHelper.SELECT_ALL_QUERY);
                                    orderTable();
                                    databaseHelper.printTableContents(Database.TasksTable.TABLE_NAME);
                                }
                            })
                            .setNegativeButton("Modify", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    final EditTaskDialog editTaskDialog = new EditTaskDialog();

                                    setData(row, DatabaseHelper.COL_DUE_DATE);
                                    editTaskDialog.setDate(getData());

                                    setData(row, DatabaseHelper.COL_CATEGORY);
                                    editTaskDialog.setCategory(getData());

                                    setData(row, DatabaseHelper.COL_DESCRIPTION);
                                    editTaskDialog.setDescription(getData());

                                    setData(row, DatabaseHelper.COL_RAW_DATE);
                                    editTaskDialog.setRawDate(getData());

                                    setData(row, DatabaseHelper.COL_PRIORITY);
                                    editTaskDialog.setPriority(getData());

                                    setData(row, DatabaseHelper.COL_NOTIFY);
                                    editTaskDialog.setNotification(getData());

                                    setData(row, DatabaseHelper.COL_N_TIME);
                                    editTaskDialog.setTime(getData());

                                    editTaskDialog.show(ToDoActivity.this.getFragmentManager(), "setEditDialogListener");
                                    editTaskDialog.setEditDialogListener(new EditTaskDialog.setEditTaskListener() {
                                        @Override
                                        public void onDoneClick(DialogFragment dialogFragment) {

                                            if (editTaskDialog.getDateField().matches("") &&
                                                    editTaskDialog.getDescription().matches("") &&
                                                    editTaskDialog.getCategory().matches("")) {
                                                Toast.makeText(ToDoActivity.this, "Please complete all fields",
                                                        Toast.LENGTH_SHORT).show();
                                            } else {
                                                if (editTaskDialog.getDateField().matches("")) {
                                                    Toast.makeText(ToDoActivity.this, "Please add a date",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                                if (editTaskDialog.getTimeField().matches("")) {
                                                    Toast.makeText(ToDoActivity.this, "Please add a time",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                                if (!editTaskDialog.getTimeField().matches("^(([0,1][0-9])|(2[0-3])):[0-5][0-9]$")) {
                                                    Toast.makeText(ToDoActivity.this, "Please add a correct time",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                                if (editTaskDialog.getCategory().matches("")) {
                                                    Toast.makeText(ToDoActivity.this, "Please add a category",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                                if (editTaskDialog.getDescription().matches("")) {
                                                    Toast.makeText(ToDoActivity.this, "Please add a description",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                                if (!editTaskDialog.getDateField().matches("")
                                                        && !editTaskDialog.getTimeField().matches("")
                                                        && editTaskDialog.getTimeField().matches("^(([0,1][0-9])|(2[0-3])):[0-5][0-9]$")
                                                        && !editTaskDialog.getCategory().matches("")
                                                        && !editTaskDialog.getDescription().matches("")) {

                                                    databaseHelper.editTask(
                                                            row,
                                                            editTaskDialog.getCategoryField(),
                                                            editTaskDialog.getDescriptionField(),
                                                            editTaskDialog.getDateField(),
                                                            editTaskDialog.getRawDate(),
                                                            checkBox.isChecked(),
                                                            editTaskDialog.getPriority(),
                                                            editTaskDialog.getNotification(),
                                                            editTaskDialog.getTime());
                                                    databaseHelper.printTableContents(Database.TasksTable.TABLE_NAME);
                                                    tableLayout.invalidate();
                                                    populateTable(DatabaseHelper.SELECT_ALL_QUERY);
                                                    Toast.makeText(ToDoActivity.this, "Task modified successfully!",
                                                            Toast.LENGTH_SHORT).show();
                                                    Long alertTime = new GregorianCalendar().getTimeInMillis()+5*1000;
                                                    Intent notificationIntent = new Intent(ToDoActivity.this, NotificationHandler.class);

                                                    AlarmManager alarmManager = (AlarmManager)
                                                            getSystemService(Context.ALARM_SERVICE);

                                                    alarmManager.set(AlarmManager.RTC_WAKEUP, alertTime,
                                                            PendingIntent.getBroadcast(ToDoActivity.this, 1, notificationIntent,
                                                                    PendingIntent.FLAG_CANCEL_CURRENT));

                                                }
                                            }
                                        }
                                    });
                                }
                            });
                    builder.create().show();
                    return true;
                }
            });

            tableRow.addView(dateField);
            tableRow.addView(categoryField);
            tableRow.addView(descField);
            tableRow.addView(priField);
            tableRow.addView(checkBox);

            tableLayout.addView(tableRow);
            cursor.moveToNext();
        }

        readDb.close();
        cursor.close();
    }

    /**
     * given the data set in the row/col mix, the value is given in string format
     * @return the value for the field as a string
     */
    public String getData(){return data;}

    /**
     * retrieves the appropriate data for the edit record dialog box
     * in order to populate the given fields with the appropriate values
     * as a generic function
     * @param row the given row to be accessed with respect to ID
     *            which is always the row number + 1
     * @param col the given column to be accessed where the column
     *            corresponds to the queries in the database helper class
     */
    public void setData(int row, int col){
        SQLiteDatabase readDb = databaseHelper.getReadableDatabase();
        Cursor cursor = readDb.rawQuery(DatabaseHelper.SELECT_ALL_QUERY, null);
        cursor.moveToFirst();
        cursor.move(row - 1);
        this.data = cursor.getString(col);
        readDb.close();
        cursor.close();
    }

    /**
     * strikes through the appropriate field by adding or removing bitwise operator flags
     * @param dateField the date field in the given row to be struck through or unstruck
     * @param categoryField the category field in the given row to be struck/unstruck
     * @param descField the desc field in the given row to be struck/unstruck
     * @param completed toggles whether or not the row is to be struck through or not
     */
    public void strikeThrough(TextView dateField, TextView categoryField,
                              TextView descField, TextView priField, boolean completed){
        if(completed) {
            dateField.setPaintFlags(dateField.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            categoryField.setPaintFlags(dateField.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            descField.setPaintFlags(dateField.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            //priField.setTextColor(getResources().getColor(R.color.red));

        } else {
            dateField.setPaintFlags(dateField.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
            categoryField.setPaintFlags(dateField.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
            descField.setPaintFlags(dateField.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
        }
    }

    /*private void restartNotify() {

        am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, NotificationHandler.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
                intent, PendingIntent.FLAG_CANCEL_CURRENT );
        // На случай, если мы ранее запускали активити, а потом поменяли время,
        // откажемся от уведомления
        am.cancel(pendingIntent);
        // Устанавливаем разовое напоминание
        am.set(AlarmManager.RTC_WAKEUP, stamp, pendingIntent);
    }*/

    @Override
    public void onBackPressed() {
        if (drawerResult.isDrawerOpen()==true) {
            drawerResult.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }


    private void alarmMethod(Calendar calendar){
        Intent myIntent = new Intent(this, NotificationHandler.class);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        pendingIntent = PendingIntent.getService(this,0,myIntent,0);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),1000*60*60*24,pendingIntent);

        Toast.makeText(ToDoActivity.this , "Shototam" , Toast.LENGTH_LONG).show();
    }
}