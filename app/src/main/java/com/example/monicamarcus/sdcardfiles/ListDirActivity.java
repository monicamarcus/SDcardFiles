package com.example.monicamarcus.sdcardfiles;

import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class ListDirActivity extends ListActivity {
    private static final String TAG = "ListActivity";
    private TextView textView;
    private ArrayAdapter<String> adapter = null;
    private File current = null;
    private File oldCurrent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_list_dir);

        ActionBar actionBar = getActionBar();
        try {
            actionBar.setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException e) {
            e.printStackTrace();
            Log.d(TAG, " " + e.getMessage());
        }
        textView = (TextView) findViewById(R.id.textView);

        adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.row_layout, R.id.textView);
        Intent intent = getIntent();
        current = new File(intent.getStringExtra("dirname"));

        if (savedInstanceState != null) {
            current = new File(savedInstanceState.getString("currentdir"));
            if (savedInstanceState.getString("previousdir")  != null)
                oldCurrent = new File(savedInstanceState.getString("previousdir"));
        }

        listFiles(current);
    }

    public void listFiles(File f) {
        ArrayList<Long> sizeList = new ArrayList<Long>();
        ArrayList<String> nameList = new ArrayList<String>();
        int n = 0;
        long size = 0;
        String fileName = "";
        if (f.isDirectory()) {
            if (f.listFiles() != null) {
                n = f.listFiles().length;
                if (n > 0) {
                    for (File file : f.listFiles()) {
                        size = file.getAbsoluteFile().getTotalSpace();
                        fileName = file.getAbsoluteFile().getName();
                        sizeList.add(size);
                        nameList.add(fileName);
                    }
                    Collections.sort(sizeList);
                    //int m = Math.min(n,10); //use in case I want to list the largest 10 files
                    String item = null;
                    textView.setText(f.getAbsoluteFile().getName());
                    adapter.clear();
                    for (int i = 0; i < n; ++i) {
                        item = nameList.get(i) + "\t " + sizeList.get(i);
                        adapter.add(item);
                    }
                    setListAdapter(adapter);
                } else {
                    Log.d(TAG, "n = " + n);
                    textView.setText("No files");
                    adapter.clear();
                }
            } else {
                Log.d(TAG, "f.listFiles() is " + f.listFiles());
            }
        } else if (f.isFile()) {
            fileName = f.getName();
            adapter.clear();
            adapter.add(fileName + "\t " + f.getTotalSpace());
            setListAdapter(adapter);
        } else {
            Log.d(TAG, "Smth is wrong " + f.getName());
        }
    }

    @Override
    protected void onListItemClick(ListView list, View view, int position, long id) {
        super.onListItemClick(list, view, position, id);

        String selectedItem = (String) getListView().getItemAtPosition(position);
        //String selectedItem = (String) getListAdapter().getItem(position);
        selectedItem = selectedItem.split("\t ")[0];
        oldCurrent = current;
        current = new File(current.getAbsolutePath() + "/" + selectedItem);
        textView.setText(current.toString());
        if (current.isDirectory()) {
            listFiles(current);
        } else if (current.isFile()) {
            textView.setText(current.toString() + "\n" + current.getTotalSpace());
            current = oldCurrent;
            adapter.clear();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list_dir, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_undo:
                undo();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void undo(View v) {
        undo();
    }

    private void undo() {
        if (oldCurrent != null) {
            textView.setText(oldCurrent.toString());
            listFiles(oldCurrent);
            current = oldCurrent;
            int pos = oldCurrent.toString().lastIndexOf("/");
            if (pos > 0)
                oldCurrent = new File(oldCurrent.toString().substring(0,pos));
        } else {
            textView.setText(current.toString());
            listFiles(current);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString("currentdir", current.toString());
        if (oldCurrent != null) {
            savedInstanceState.putString("previousdir", oldCurrent.toString());
        } else {
            savedInstanceState.putString("previousdir", null);
        }
    }
}
