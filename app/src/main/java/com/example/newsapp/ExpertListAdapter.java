package com.example.newsapp;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.LinkedList;
import java.util.List;

public class ExpertListAdapter extends RecyclerView.Adapter<ExpertListAdapter.ExpertListViewHolder> {
    List<ExpertEntity> expertlist;
    static public class ExpertListViewHolder extends RecyclerView.ViewHolder {
        public View layout;
        public ExpertListViewHolder(View v) {
            super(v);
            layout = v;
        }
    }
    ExpertListAdapter() { expertlist = new LinkedList<>(); }
    @Override
    public ExpertListAdapter.ExpertListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_expert_layout, parent, false);
        ExpertListViewHolder vh = new ExpertListViewHolder(v);
        return vh;
    }
    @SuppressLint({"ResourceAsColor", "SetTextI18n"})
    @Override
    public void onBindViewHolder(ExpertListViewHolder holder, int position) {
        TextView expertTextView = holder.layout.findViewById(R.id.expert_name);
        TextView expertSourceView = holder.layout.findViewById(R.id.expert_source);
        final ExpertEntity expert = expertlist.get(position);
        String zhName = expert.getmZhName();
        String enName = expert.getmEnName();
        if(zhName.equals(enName)) {
            expertTextView.setText(zhName);
        } else {
            expertTextView.setText(zhName + " " + enName);
        }
        expertSourceView.setText(expert.mPosition + "\n" + expert.mAssociation);

        if(expert.mImgURL != null) {
            ImageView expertImageView = holder.layout.findViewById(R.id.expert_image);
            Glide.with(holder.layout.getContext()).load(expert.mImgURL).into(expertImageView);
        }
        holder.layout.findViewById(R.id.expert_name).setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("expert", expert);
            Navigation.findNavController(v).navigate(R.id.action_view_expert_body, bundle);
        });
    }
    @Override
    public int getItemCount() { return expertlist.size(); }
}
