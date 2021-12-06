package com.olcayergun.activitymap.listlocation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.olcayergun.activitymap.MapsActivity;
import com.olcayergun.activitymap.R;
import com.olcayergun.activitymap.distance.DistanceActivity;
import com.olcayergun.activitymap.listlocation.Coordinate;
import com.olcayergun.activitymap.listlocation.CoordinateAdapter;

import java.util.ArrayList;

public class ListLocActivity extends AppCompatActivity {

    private RecyclerView coordinates;
    private RecyclerView.Adapter adapter;

    private Button btnBack, btnFwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_loc);

        ArrayList<Coordinate> coordinates = new ArrayList<>();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            ArrayList<String> strCoordinates = bundle.getStringArrayList("coordinates");
            coordinates = initCoordinates(strCoordinates);
        }


        this.coordinates = (RecyclerView) findViewById(R.id.rv_Coordinates);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        this.coordinates.setLayoutManager(mLayoutManager);

        adapter = new CoordinateAdapter(coordinates);
        this.coordinates.setAdapter(adapter);

        btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener((View.OnClickListener)(new View.OnClickListener() {
                public final void onClick(View it) {
                    Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                    startActivity(intent);
                }
            }));
        }

        btnFwd = findViewById(R.id.btnFwd);
        if (btnFwd != null) {
            btnFwd.setOnClickListener((View.OnClickListener)(new View.OnClickListener() {
                public final void onClick(View it) {
                    Intent intent = new Intent(getApplicationContext(), DistanceActivity.class);
                    startActivity(intent);
                }
            }));
        }

    }

    private ArrayList<Coordinate> initCoordinates(ArrayList<String> strCoordinates) {
        ArrayList<Coordinate> list = new ArrayList<>();
        for (int i = 0; i < strCoordinates.size(); i++) {
            list.add(new Coordinate(strCoordinates.get(i)));
        }
        return list;
    }
}