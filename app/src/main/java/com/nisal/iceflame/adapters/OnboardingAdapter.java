package com.nisal.iceflame.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nisal.iceflame.R;

import java.util.List;

public class OnboardingAdapter extends RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder> {

    private List<Integer> images;
    private List<String> titles;
    private List<String> texts;

    public OnboardingAdapter(List<Integer> images, List<String> titles, List<String> texts) {
        this.images = images;
        this.titles = titles;
        this.texts = texts;
    }

    @NonNull
    @Override
    public OnboardingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.onboard_page_item, parent, false);
        return new OnboardingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OnboardingViewHolder holder, int position) {
        holder.imageView.setImageResource(images.get(position));
        holder.titleView.setText(titles.get(position));
        holder.textView.setText(texts.get(position));
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    static class OnboardingViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView titleView;
        TextView textView;

        public OnboardingViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.onBordImage);
            titleView = itemView.findViewById(R.id.onBordTitle);
            textView = itemView.findViewById(R.id.onBordText);
        }
    }
}