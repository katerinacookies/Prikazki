package com.example.prikazki;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class TaleSelectionActivity extends AppCompatActivity {

    private ArrayList<String> taleTitles = new ArrayList<>();
    private ArrayList<String> taleIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tale_selection);

        int groupId = getIntent().getIntExtra("GROUP_ID", 1);
        TextView title = (TextView) findViewById(R.id.selectionTitle);
        title.setText("Група " + groupId + ": Избери приказка");

        loadTalesFromJson(groupId);

        ListView listView = (ListView) findViewById(R.id.talesListView);
        // Standard Android list adapter to show the names
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, taleTitles);
        listView.setAdapter(adapter);

        // When a user clicks a tale name
        listView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedTaleId = taleIds.get(position);

            // Now we go to the actual Robot Activity
            Intent intent = new Intent(TaleSelectionActivity.this, TaleActivityMain.class);
            intent.putExtra("TALE_ID", selectedTaleId);
            startActivity(intent);
        });
    }

    private void loadTalesFromJson(int groupId) {
        try {
            // Read your JSON file from assets
            InputStream is = getAssets().open("tales_config.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, StandardCharsets.UTF_8);

            JSONObject obj = new JSONObject(json);
            // Get the specific group (e.g., "group1")
            JSONArray groupArray = obj.getJSONArray("group" + groupId);

            for (int i = 0; i < groupArray.length(); i++) {
                JSONObject tale = groupArray.getJSONObject(i);
                taleTitles.add(tale.getString("title")); // What the user sees
                taleIds.add(tale.getString("id"));       // What the code uses
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}