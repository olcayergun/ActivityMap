package com.olcayergun.activitymap.listlocation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.olcayergun.activitymap.R;

import java.util.ArrayList;

public class CoordinateAdapter extends RecyclerView.Adapter<CoordinateAdapter.ViewHolder> {

    private final ArrayList<Coordinate> coordinates;

    public CoordinateAdapter(ArrayList<Coordinate> coordinates) {
        this.coordinates = coordinates;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.list_coordinates, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Coordinate coordinate = coordinates.get(position);
        holder.tvCoordinate.setText(coordinate.getCoordinate());
    }

    @Override
    public int getItemCount() {
        if (coordinates != null) {
            return coordinates.size();
        } else {
            return 0;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView tvCoordinate;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            tvCoordinate = view.findViewById(R.id.tv_Coordinate);
        }
    }
}
