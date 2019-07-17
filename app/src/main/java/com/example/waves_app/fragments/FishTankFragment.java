package com.example.waves_app.fragments;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.waves_app.R;

import java.util.Random;

public class FishTankFragment extends Fragment {

    public static final String TAG = "FishTankFragment";
    private int removedCount;
    private int displayCount;
    private int maxHeight;
    private int maxWidth;
    private TextView tvFishCount;
    private ConstraintLayout layout;
//    private ViewTreeObserver vto;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fish_tank, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // TODO - use user persistence to find out how many tasks have been completed;
        // Calculate values for how many fish to display
        removedCount = 13;
        displayCount = ((removedCount % 15) == 0) ? 15 : removedCount % 15;

        // Set layout width and height range
        maxHeight = 1000;
        maxWidth = 800;

        // Get the objects by id
        tvFishCount = (TextView) view.findViewById(R.id.tvFishCount);
        layout = (ConstraintLayout) view.findViewById(R.id.cLayout);

//        vto = layout.getViewTreeObserver();

        // Set information for fishCount
        tvFishCount.setText(String.format("Fish Count: %d", displayCount));

//        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                maxHeight = layout.getMeasuredHeight();
//                maxWidth = layout.getMeasuredWidth();
//            }
//        });
//
//        Log.d(TAG, "Height: " + maxHeight);
//        Log.d(TAG, "Width: " + maxWidth);

        // Generate an image per count
        for (int i = 0; i < displayCount; i++) {
            // Prepare imageView for fish display
            ImageView fishImage = new ImageView(getContext());
            fishImage.setLayoutParams(new android.view.ViewGroup.LayoutParams(300, 200));

            int fishID = getRandomFishId();
            fishImage.setImageResource(fishID);
            fishImage.setX(new Random().nextInt(maxWidth));
            fishImage.setY(new Random().nextInt(maxHeight) + 100);
            layout.addView(fishImage);
        }
    }

    public int getRandomFishId() {
        // Generates random integer between 0 and 14 inclusive
        int random = new Random().nextInt(15);
        return getResources().getIdentifier("fish_" + random, "drawable", getContext().getPackageName());
    }
}