package com.example.watcher24;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.watcher24.adapter.Myadapter;

public class AddRegionActivity extends AppCompatActivity {

    private Myadapter myadapter;
    private RecyclerView recyclerView;
    int[] flag;
    private String[] papernames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_region);
        recyclerView=findViewById(R.id.add_region_recycler);
        flag=new int[]{R.drawable.bd,R.drawable.united_states,R.drawable.malaysia,R.drawable.india};
        papernames = getResources().getStringArray(R.array.country);
        myadapter=new Myadapter(this, flag, papernames, 4);
        recyclerView.setAdapter(myadapter);
    }
}