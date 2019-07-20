package com.example.waves_app;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.waves_app.fragments.TasksFragment;
import com.example.waves_app.model.Category;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private List<Category> categories;
    private Context context;
    private List<String> parsedData;
    int pos;

    // Data is passed into the constructor
    public CategoryAdapter(Context context, List<Category> data, List<String> parsedData) {
        this.categories = data;
        this.context = context;
        this.parsedData = parsedData;
    }

    // returns the file in which the data is stored
    private File getDataFile() {
        return new File(context.getFilesDir(), "allCategories.txt");
    }

    // write the items to the filesystem
    private void writeCatItems() {
        try {
            // save the item list as a line-delimited text file
            FileUtils.writeLines(getDataFile(), parsedData);
        } catch (IOException e) {
            // print the error to the console
            e.printStackTrace();
        }
    }

    // Inflates the row layout from xml when needed and returns the holder
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(context);
        View view = mInflater.inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }

    // Binds data into the item through holder
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.bind(category);
    }

    // Returns total count of items in the list
    @Override
    public int getItemCount() {
        return categories.size();
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Member variable for view that will be set as row renders
        public EditText etCategory;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            etCategory = (EditText) itemView.findViewById(R.id.etNewCategory);

            // Attach a click listener to the entire row view
            itemView.setOnClickListener((View.OnClickListener)this);
        }

        // goes into the actual task list
        @Override
        public void onClick(View view) {
            String catName = etCategory.getText().toString();

            FragmentManager manager = ((FragmentActivity)context).getSupportFragmentManager();
            Fragment fragment = new TasksFragment();
            Bundle information = new Bundle();

            information.putString("catName", catName);
            //information.putBoolean("edited", edited);
            //information.putString("ogName", ogName);
            if (parsedData.indexOf(catName) == -1) {
                parsedData.add(catName);
                writeCatItems();
            }
            fragment.setArguments(information);
            manager.beginTransaction().replace(R.id.flContainer, fragment).commit();
        }

        public void bind(final Category category) {
            etCategory.setText(category.getCategoryName());

            //ogName = category.getCategoryName();

            // Get data from editText and set name for new category
            etCategory.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    String ogName = category.getCategoryName();
                    // When focus is lost check that the text field has valid values.
                    if (!hasFocus) {
                        // If anything was typed
                        if (etCategory.getText().toString().length() > 0) {

                            File ogFile = new File(context.getFilesDir(), ogName + ".txt");
                            File renameFile = new File(context.getFilesDir(), etCategory.getText().toString() + ".txt");
                            try {
                                FileUtils.moveFile(ogFile, renameFile);
                                ogFile.delete();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            // the case if the user edits the reminder/task
                            category.setCategoryName(etCategory.getText().toString());
                            pos = getAdapterPosition();
                            parsedData.set(pos, category.getCategoryName());
                            writeCatItems(); // update the persistence
                        } else {
                            Toast.makeText(v.getContext(), "No category name has been entered!", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });
        }
    }
}