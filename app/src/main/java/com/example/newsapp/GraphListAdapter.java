package com.example.newsapp;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.text.Html;
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
        return group_father.getmRelationList().size() + group_father.getmPropertyMap().size() + 1;
    }

    @Override
    public Object getGroup(int groupPosition) { return group_list.get(groupPosition); }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        SearchEntity group_father = group_list.get(groupPosition);
        int size = 1 + group_father.getmPropertyMap().size();
        if(childPosition == 0) {
            return group_father.mIntroduction;
        } else if ( childPosition > 0 && childPosition < size) {
            return group_father.getmPropertyMap().get(childPosition-1);
        } else {
            return group_father.getmRelationList().get(childPosition-size);
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
        //TextView text = view.findViewById(R.id.entry_text);
        //text.setText(entity.mIntroduction);
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

        SearchEntity group_father = group_list.get(groupPosition);
        int size = group_father.getmPropertyMap().size() + 1;
        if(childPosition == 0) {
            entityRelationView.getLayoutParams().width = 1000;
            entityLabelView.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            entityLabelView.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
            entityRelationView.setText(group_father.mIntroduction);
        } else if (childPosition > 0 && childPosition < size) {
            Property property = group_father.getmPropertyMap().get(childPosition - 1);
            entityRelationView.setText(property.name);
            entityLabelView.setText(property.intro);
            imageView.setImageResource(R.mipmap.right_arrow_little);
        } else {
            RelationEntity relationentity = group_father.getmRelationList().get(childPosition - size);
            entityRelationView.setText(relationentity.mRelation);
            entityLabelView.setText(Html.fromHtml("<a href='" + relationentity.mRelationURL + "'>"  + relationentity.mLabel + "</a>", Build.VERSION.SDK_INT));
            if(relationentity.isForward)
                imageView.setImageResource(R.drawable.right_arrow);
            else
                imageView.setImageResource(R.drawable.left_arrow);
        }
        return cardView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) { return true; }
}
