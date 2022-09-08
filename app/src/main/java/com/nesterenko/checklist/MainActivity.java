package com.nesterenko.checklist;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> checklist = new ArrayList<>();
    ArrayAdapter<String> checklist_adapter;


    public void removeLineFromFile(File inputFile, String lineToRemove) {
        File lineRemoved = new File(getFilesDir(), "checklist_dupe");
        String nextLine;
        try {
            FileOutputStream out = new FileOutputStream(lineRemoved, true);
            Scanner copyFrom = new Scanner(inputFile);
            while (copyFrom.hasNext()) {
                nextLine = copyFrom.nextLine();
                if (!nextLine.equals(lineToRemove)) {
                    out.write(nextLine.getBytes(StandardCharsets.UTF_8));
                    out.write("\n".getBytes(StandardCharsets.UTF_8));
                }
            }
            out.close();
            copyFrom.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        inputFile.delete();
        lineRemoved.renameTo(inputFile);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        File checklistData = new File(getFilesDir(), "checklistData");
        try {
            Scanner readIN = new Scanner(checklistData);
            while (readIN.hasNext()) {
                checklist.add(readIN.nextLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        checklist_adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_multiple_choice, checklist);
        ListView checklist_listView = findViewById(R.id.listview_TODO);
        checklist_listView.setAdapter(checklist_adapter);
        EditText userInput = findViewById(R.id.input);
        Button save = findViewById(R.id.save);
        Button reset = findViewById(R.id.reset);



        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!userInput.getText().toString().matches("")) {

                    String noticeToSave = userInput.getText().toString();
                    try {
                    FileOutputStream out = new FileOutputStream(checklistData, true);
                    out.write(noticeToSave.getBytes(StandardCharsets.UTF_8));
                    out.write("\n".getBytes(StandardCharsets.UTF_8));
                    out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    checklist.add(userInput.getText().toString());
                    userInput.setText("");
                    checklist_adapter.notifyDataSetChanged();
                }
            }
        });



        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = checklist_listView.getCount() - 1; i >= 0; i--) {
                    checklist_adapter.remove((String) checklist_listView.getItemAtPosition(i));
                }
                try {
                    PrintWriter temp = new PrintWriter(checklistData);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });



        checklist_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                removeLineFromFile(checklistData, (String) checklist_listView.getItemAtPosition(pos).toString());
                checklist_adapter.remove(checklist.get(pos));
                checklist_adapter.notifyDataSetChanged();
            }
          });


    }

}