package com.crosska.testapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Task0Activity extends AppCompatActivity {

    private ListView deckListView;
    private int currentDeckIndex = 0;
    private SQLiteWorker dbWorker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_task0);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbWorker = new SQLiteWorker(this);
        deckListView = findViewById(R.id.task0Listview);
        task0();
    }

    public void task0PrevBtnClicked(View view) {
        if (currentDeckIndex > 0) {
            currentDeckIndex--;
            task0();
        } else {
            Toast.makeText(this, "Сейчас уже выбрана первая строка.", Toast.LENGTH_LONG).show();
        }
    }

    public void task0NextBtnClicked(View view) {
        currentDeckIndex++;
        task0();
    }

    public void task0() {
        List<String> dataList = new ArrayList<>();

        Map<String, String> rows = dbWorker.getRowByOrdinal("колоды", currentDeckIndex); // deck_id, deck
        if (!rows.values().isEmpty()) {
            for (String value : rows.values()) {
                Log.d("DECK_ORIGINAL", "Колода: \n" + value);
                String[] cards = value.replace("m", "").split(",");
                Log.d("DECK_CLEARED", "Колода: \n" + Arrays.toString(cards));
                dataList.addAll(Arrays.asList(cards));
                break;
            }

            dbWorker.createTempTable(dataList);
            dataList = dbWorker.getCardsInfo();

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dataList);
            deckListView.setAdapter(adapter);
        } else {
            Toast.makeText(this, "Это последняя строка в таблице", Toast.LENGTH_LONG).show();
            currentDeckIndex--;
        }
    }

}