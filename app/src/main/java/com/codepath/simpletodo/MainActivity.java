package com.codepath.simpletodo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //a numeric code to identify the edit activity
    public final static int EDIT_REQUEST_CODE = 20;
    // keys used for passing data between activities
    public final static String ITEM_TEXT = "itemText";
    public final static String ITEM_POSITION = "itemPosition";


    ArrayList<String> items;
    ArrayAdapter<String> itemsAdapter;
    ListView lvItems;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        readItems();
        itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        lvItems = (ListView) findViewById(R.id.lvItems);
        lvItems.setAdapter(itemsAdapter);

        setupListViewListener();

    }

    public void onAddItem(View v){
        EditText editText = (EditText) findViewById(R.id.editText);
        String itemText = editText.getText().toString();
        itemsAdapter.add(itemText);
        editText.setText("");
        writeItems();
        Toast.makeText(getApplicationContext(), "Item added to List", Toast.LENGTH_SHORT).show();
    }

    private void setupListViewListener() {
        Log.i("MainActivity", "Setting up Listener on list view");
        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("MainActivity", "Item removed from the list:" + position);
                items.remove(position);
                itemsAdapter.notifyDataSetChanged();
                writeItems();
                return true;
            }

        });

        //set up item listener for edit(regular click)
        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //create the new activity
                Intent i = new Intent(MainActivity.this, EditItemActivity.class);

                //pass the data being edited
                i.putExtra(ITEM_TEXT,items.get(position));
                i.putExtra(ITEM_POSITION,position);

                //display the activity
                startActivityForResult(i,EDIT_REQUEST_CODE);


            }
        });

    }

    //handle results from edit activity


    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //if the dit activity completed OK
        if(resultCode==RESULT_OK && requestCode==EDIT_REQUEST_CODE){
            //extract updated item text from result intent extras
            String updatedItem=data.getExtras().getString(ITEM_TEXT);
            //extract edited position from edited item
            int position=data.getExtras().getInt(ITEM_POSITION);
            //update the model with new text
            items.set(position,updatedItem);
            //notify the adapter that the model changed
            itemsAdapter.notifyDataSetChanged();
            //persist the changed model
            writeItems();
            //notify the user the operation completed ok
            Toast.makeText(this, "Item updated successfully",Toast.LENGTH_SHORT).show();
        }

    }

    private File getDataFile() {
        return new File(getFilesDir(), "todo.txt");

    }

    private void readItems() {
        File filesDir = getDataFile();
        try {
            items = new ArrayList<>(FileUtils.readLines(filesDir, Charset.defaultCharset()));
        } catch (IOException e) {
            items = new ArrayList<String>();
        }
    }

    private void writeItems() {
        File filesDir = getDataFile();
        try {
            FileUtils.writeLines(filesDir, items);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
