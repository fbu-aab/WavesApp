/*
 * Project: Waves
 *
 * Purpose: Displays the view for the calendar page
 *
 * Reference(s): Angela Liu, Briana Berger
 */

package com.example.waves_app.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.waves_app.R;
import com.example.waves_app.model.Task;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarFragment extends Fragment {

    private CompactCalendarView compactCalendar;
    private SimpleDateFormat dateFormatMonth = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
    private TextView tvMonthYear;

    // Variables for displaying tasks due on a selected day
    private TextView tvEventHolder;
    private TextView tvDaySelected;
    private TextView tvTasksForDay;
    private List<String> categoryData;
    private List<Task> taskEvents = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_calendar, container, false);
        view.setBackgroundDrawable(getResources().getDrawable(R.drawable.sand_background));
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvMonthYear = (TextView) getActivity().findViewById(R.id.tvMonthYear);
        tvEventHolder = (TextView) getActivity().findViewById(R.id.tvEventHolder);
        tvDaySelected = (TextView) getActivity().findViewById(R.id.tvDaySelected);
        tvTasksForDay = (TextView) getActivity().findViewById(R.id.tvTasksForDay);
        compactCalendar = (CompactCalendarView) getActivity().findViewById(R.id.calendarView);

        compactCalendar.setFirstDayOfWeek(Calendar.SUNDAY); // Sets the first day of the calendar as specified
        tvEventHolder.setText("Tasks on ");
        tvTasksForDay.setMovementMethod(new ScrollingMovementMethod());

        // Initially sets the monthYear textView with information
        Calendar c = Calendar.getInstance();   // This gets current date
        onMonthScroll(c.getTime());

        loadEvents();

        // Add the tasks as events into calendar
        for (Task task : taskEvents) {
            String dueDate = task.getDueDate();
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            Date date = null;

            try {
                date = sdf.parse(dueDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long millis = date.getTime();

            Event ev1 = new Event(Color.BLUE, millis, task.getTaskDetail());
            compactCalendar.addEvent(ev1);
        }

        // Start with tasks on the current day
        loadTasksForDay(c.getTime());

        // Display the tasks for a day that is selected
        compactCalendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                loadTasksForDay(dateClicked);
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                // For month and year at top of calendar
                tvMonthYear.setText(dateFormatMonth.format(firstDayOfNewMonth));

                // Displays the tasks for the day selected when user scrolls to another month
                loadTasksForDay(compactCalendar.getFirstDayOfCurrentMonth());
            }
        });
    }

    private void onMonthScroll(Date firstDayOfNewMonth) {
        tvMonthYear.setText(dateFormatMonth.format(firstDayOfNewMonth));
    }

    private void loadTasksForDay(Date dateClicked) {
        String month = tvMonthYear.getText().toString().substring(0, tvMonthYear.getText().toString().indexOf(" "));
        int date = dateClicked.getDate();
        int day = dateClicked.getDay();
        
        tvDaySelected.setText(getDayOfWeek(day) + month + " " + date + ":");
        tvTasksForDay.scrollTo(0, 0);

        List<Event> events = compactCalendar.getEvents(dateClicked);
        String remindersOfDay = "- ";
        if (events != null) {
            for (int i = 0; i < events.size(); i++) {
                String temp;
                if (i == (events.size() - 1)) {
                    temp = events.get(i).getData().toString();
                } else {
                    temp = events.get(i).getData().toString() + "\n- ";
                }
                remindersOfDay += temp;
            }
            if (remindersOfDay.length() != 2) {
                tvTasksForDay.setText(remindersOfDay);
            } else {
                tvTasksForDay.setText("Nothing for this day!");
            }
        }
    }

    private void loadEvents() {
        readCategoryItems();
        taskEvents = new ArrayList<>();

        // Go through all categories and search for their tasks
        for (String categoryName : categoryData) {
            String categoryFile = categoryName + ".txt";

            try {
                List<String> tasks = new ArrayList<>(FileUtils.readLines(getTasksFile(categoryFile), Charset.defaultCharset()));

                // Load any tasks into the taskEvent list so that we can make events later
                for (String task : tasks) {
                    Task tEvent = new Task();

                    int delimiter = task.lastIndexOf(",");
                    String taskDetail = task.substring(0, delimiter);
                    String dueDate = task.substring(delimiter + 1);

                    if (!dueDate.equals("set due date")) {
                        // Ensures that no event that has a no due date is added
                        tEvent.setTaskDetail(taskDetail);
                        tEvent.setDueDate(dueDate);
                        taskEvents.add(tEvent);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String getDayOfWeek(int day) {
        switch (day) {
            case 0:
                return "Sunday, ";
            case 1:
                return "Monday, ";
            case 2:
                return "Tuesday, ";
            case 3:
                return "Wednesday, ";
            case 4:
                return "Thursday, ";
            case 5:
                return "Friday, ";
            default:
                return "Saturday, ";
        }
    }

    private void readCategoryItems() {
        try {
            // Create the array of categories
            categoryData = new ArrayList<String>(FileUtils.readLines(getCategoriesFile(), Charset.defaultCharset()));
        } catch (IOException e) {
            categoryData = new ArrayList<>();
            e.printStackTrace();
        }
    }

    private File getCategoriesFile() {
        return new File(getContext().getFilesDir(), "allCategories.txt");
    }

    private File getTasksFile(String category) {
        return new File(getContext().getFilesDir(), category);
    }
}