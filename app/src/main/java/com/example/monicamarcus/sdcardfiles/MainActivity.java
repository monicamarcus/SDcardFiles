package com.example.monicamarcus.sdcardfiles;

import android.app.ListActivity;
import android.database.DataSetObserver;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class MainActivity extends ListActivity {
    TextView textView;
    ListView listView;
    Button scanButton;
    ArrayAdapter<String> adapter = null;
    File current = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.textView);
        scanButton = (Button) findViewById(R.id.button);
        scanButton.setOnClickListener(l);

        adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.row_layout, R.id.textView);
    }

    View.OnClickListener l = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ArrayList<Integer> sizeList = new ArrayList<Integer>();
            ArrayList<String> nameList = new ArrayList<String>();
            int n = 0, size = 0;
            String fileName = "";
            File sdCardDir = new File(Environment.getExternalStorageDirectory().getPath());
            current = sdCardDir;
            if (sdCardDir.isDirectory() && sdCardDir.listFiles() != null) {
                n = sdCardDir.listFiles().length;
                for (File f : sdCardDir.listFiles()) {
                    size = ((int) f.getAbsoluteFile().getTotalSpace());
                    fileName = f.getAbsoluteFile().getName();
                    sizeList.add(size);
                    nameList.add(fileName);
                }
            }
            Collections.sort(sizeList);
            String item = null;
            textView.setText("List directories and files in " + current.getName());
            adapter.clear();
            for (int i = 0; i < n; ++i) {
                //System.out.println(nameList.get(i) + " " + sizeList.get(i));
                item = nameList.get(i) + "\t " + sizeList.get(i);
                adapter.add(item);
            }
            setListAdapter(adapter);
        }
    };

    public void listFiles(File f) {
        ArrayList<Integer> sizeList = new ArrayList<Integer>();
        ArrayList<String> nameList = new ArrayList<String>();
        int n = 0, size = 0;
        String fileName = "";
        if (f.isDirectory()) {
            if (f.listFiles() != null) {
                n = f.listFiles().length;
                if (n > 0) {
                    for (File file : f.listFiles()) {
                        size = ((int) file.getAbsoluteFile().getTotalSpace());
                        fileName = file.getAbsoluteFile().getName();
                        sizeList.add(size);
                        nameList.add(fileName);
                    }
                    Collections.sort(sizeList);
                    int m = Math.min(n,10);
                    String item = null;
                    textView.setText("Largest " + m + " files in directory " + f.getAbsoluteFile().getName());
                    adapter.clear();
                    for (int i = 0; i < m; ++i) {
                        item = nameList.get(i) + "\t " + sizeList.get(i);
                        adapter.add(item);
                    }
                    setListAdapter(adapter);
                } else {
                    System.out.println("HEREHERE " + n);
                    textView.setText("No files");
                    adapter.clear();
                }
            } else {
                System.out.println("HEREHERE " + f.listFiles());
            }
        } else if (f.isFile()) {
            size = (int) f.getTotalSpace();
            fileName = f.getName();
            adapter.clear();
            adapter.add(fileName + "\t " + size);
            setListAdapter(adapter);
        } else {
            System.out.println("HEREHERE Neither " + f.getName());
        }
    }

    @Override
    protected void onListItemClick(ListView list, View view, int position, long id) {
        super.onListItemClick(list, view, position, id);

        String selectedItem = (String) getListView().getItemAtPosition(position);
        //String selectedItem = (String) getListAdapter().getItem(position);
        selectedItem = selectedItem.split("\t ")[0];
        textView.setText("You clicked " + selectedItem + " at position " + position);
        File oldCurrent = current;
        current = new File(current.getAbsolutePath() + "/" + selectedItem);
        System.out.println("HEREHERE current = " + current.toString());
        if (current.isDirectory()) {
            listFiles(current);
        } else if (current.isFile()) {
            textView.setText(current.toString() + " " + current.getFreeSpace());
            current = oldCurrent;
            adapter.clear();
        }
    }
}
