package com.example.newsapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

public class expert_detail_fragment extends Fragment {
    public expert_detail_fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View ret_view = inflater.inflate(R.layout.expert_detail_fragment, container, false);
        ExpertEntity expert = (ExpertEntity) getArguments().getSerializable("expert");
        String zhName = expert.getmZhName();
        String enName = expert.getmEnName();
        if(zhName.equals(enName)) {
            ((TextView)ret_view.findViewById(R.id.expert_body_name)).setText(zhName);
        } else {
            ((TextView)ret_view.findViewById(R.id.expert_body_name)).setText(zhName + " " + enName);
        }
        ((TextView)ret_view.findViewById(R.id.expert_body_content)).setText(expert.mPosition);
        ((TextView)ret_view.findViewById(R.id.expert_body_date)).setText(expert.mAssociation.replace("/null", ""));
        if(expert.mHomePage != null)((TextView)ret_view.findViewById(R.id.expert_body_url)).setText(expert.mHomePage);
        ((TextView)ret_view.findViewById(R.id.expert_body_source)).setText("\n\n\n基本情况:\n" + expert.mBasicIntro + "\n\n教育经历:\n " + expert.mEduIntro);
        if(expert.mImgURL != null) {
            ImageView expertImageView = ret_view.findViewById(R.id.expert_body_image);
            Glide.with(ret_view.getContext()).load(expert.mImgURL).into(expertImageView);
        }
        return ret_view;
    }
}
