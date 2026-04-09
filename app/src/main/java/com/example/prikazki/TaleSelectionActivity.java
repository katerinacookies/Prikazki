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
            InputStream is = getAssets().open("example.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, StandardCharsets.UTF_8);

            JSONObject fullJson = new JSONObject(json);

            // Use an Iterator to go through all the unique keys like "ValshebnataGradinaNaProletta"
            java.util.Iterator<String> keys = fullJson.keys();

            while (keys.hasNext()) {
                String key = keys.next();
                JSONObject tale = fullJson.getJSONObject(key);

                // Check if the group matches (Converting group ID to String for comparison)
                if (tale.getString("group").equals(String.valueOf(groupId))) {
                    // In your new JSON, you use "name" instead of "title"
                    taleTitles.add(tale.getString("name"));
                    // We use the "id" field to tell the next activity which tale to play
                    taleIds.add(tale.getString("id"));
                }
            }

            if (taleTitles.isEmpty()) {
                //Toast.makeText(this, "Няма намерени приказки за тази група", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
            //Toast.makeText(this, "Грешка при зареждане на JSON", Toast.LENGTH_SHORT).show();
        }
    }
    }