package com.example.newsapp;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class GraphListAdapter extends BaseExpandableListAdapter {
    public List<SearchEntity> group_list;
    private static Handler network_handler;

    public GraphListAdapter(Handler handler) {
        group_list = new ArrayList<>();
        network_handler = handler;
    }

    @Override
    public int getGroupCount() { return group_list.size(); }

    @Override
    public int getChildrenCount(int groupPosition) {
        SearchEntity group_father = group_list.get(groupPosition);
        return group_father.getmRelationList().size() + group_father.getmPropertyMap().size();
    }

    @Override
    public Object getGroup(int groupPosition) { return group_list.get(groupPosition); }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        SearchEntity group_father = group_list.get(groupPosition);
        if(childPosition < group_father.getmRelationList().size()) {
            return group_father.getmRelationList().get(childPosition);
        } else {
            return group_father.getmPropertyMap().get(childPosition);
        }
    }

    @Override
    public long getGroupId(int groupPosition) { return groupPosition; }

    @Override
    public long getChildId(int groupPosition, int childPosition) { return childPosition; }

    @Override
    public boolean hasStableIds() { return false; }

    @Override
    public View getGroupView(int groupPosition, boolean b, View view, ViewGroup viewGroup) {
        view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.graph_content, viewGroup, false);
        SearchEntity entity = (SearchEntity)getGroup(groupPosition);
        TextView label = view.findViewById(R.id.graph_content);
        label.setText(entity.mLabel);
        TextView text = view.findViewById(R.id.entry_text);
        text.setText(entity.mIntroduction);
        if(entity.mImageURL != null) {
            ImageView imgView = view.findViewById(R.id.entry_image);
            Glide.with(viewGroup.getContext()).load(entity.mImageURL).into(imgView);
        }
        return view;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean b, View view, ViewGroup viewGroup) {
        CardView cardView = (CardView) LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.graph_entry, viewGroup, false);
        TextView entityRelationView = cardView.findViewById(R.id.entity_relation);
        TextView entityLabelView = cardView.findViewById(R.id.entity_label);
        ImageView imageView = cardView.findViewById(R.id.entity_image);
        TextView entityURLView = cardView.findViewById(R.id.entity_url);

        SearchEntity group_father = group_list.get(groupPosition);
        int size = group_father.getmRelationList().size();
        if(childPosition < size) {
            RelationEntity relationentity = group_father.getmRelationList().get(childPosition);
            entityRelationView.setText(relationentity.mRelation);
            entityLabelView.setText(relationentity.mLabel);
            entityURLView.setText("link: " + relationentity.mRelationURL);
            if(relationentity.isForward)
                imageView.setImageResource(R.drawable.right_arrow);
            else
                imageView.setImageResource(R.drawable.left_arrow);
        } else {
            Property property = group_father.getmPropertyMap().get(childPosition - size);
            entityRelationView.setText(property.name);
            entityLabelView.setText(property.intro);
            imageView.setImageResource(R.mipmap.right_arrow_little);
        }
        return cardView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) { return true; }
}
