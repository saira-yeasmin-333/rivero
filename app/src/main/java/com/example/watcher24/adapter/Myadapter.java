package com.example.watcher24.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.watcher24.R;

import java.util.ArrayList;
import java.util.List;

public class Myadapter extends RecyclerView.Adapter<Myadapter.AddCountryViewHolder> {

    private Context context;
    private int[] flag;
    private String[] papernames;
    private List<Pair<String, Integer>> pii = new ArrayList<>();
    private List<Pair<String, Integer>> clone = new ArrayList<>();
    private int intentVar;
    Intent intent2;

    public Myadapter(Context context, int[] flag, String[] papernames, int intentVar) {
        this.context = context;
        this.flag = flag;
        this.papernames = papernames;
        this.intentVar = intentVar;

        int length = papernames.length;

        for(int i=0; i<length; i++)
        {
            pii.add(new Pair(papernames[i], flag[i]));
        }

        clone.addAll(pii);
    }

    @NonNull
    @Override
    public AddCountryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.countrynames, viewGroup, false);
        return new AddCountryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddCountryViewHolder holder, final int position) {

        holder.imageView.setImageResource(pii.get(position).second);
        holder.textView.setText(pii.get(position).first);


    }


    @Override
    public int getItemCount() {
        return flag.length;
    }

    class  AddCountryViewHolder extends RecyclerView.ViewHolder
    {
        private TextView textView;
        private ImageView imageView;

        public AddCountryViewHolder(@NonNull View itemView)
        {
            super(itemView);

            textView= itemView.findViewById(R.id.add_country_text);
            imageView=itemView.findViewById(R.id.addcountry_image);
        }
    }

}

