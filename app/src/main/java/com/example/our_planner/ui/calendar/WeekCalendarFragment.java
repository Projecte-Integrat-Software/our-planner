package com.example.our_planner.ui.calendar;

import static com.example.our_planner.ui.calendar.CalendarUtils.daysInWeekArray;
import static com.example.our_planner.ui.calendar.CalendarUtils.monthYearFromDate;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.our_planner.LocaleLanguage;
import com.example.our_planner.R;
import com.example.our_planner.ThemeSwitcher;
import com.example.our_planner.model.Event;
import com.example.our_planner.model.Group;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;


public class WeekCalendarFragment extends Fragment implements CalendarAdapter.OnItemListener, EventAdapter.OnNoteListener {

    private View view;
    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private RecyclerView recyclerViewEvents;

    private Button previousWeekBtn;
    private Button nextWeekBtn;
    private CalendarViewModel calendarViewModel;


    private Map<String, Boolean> selections;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_week_calendar, container, false);
        selections = new HashMap<>();
        calendarViewModel = new ViewModelProvider(this).get(CalendarViewModel.class);
        initWidgets();

        Bundle arguments = getArguments();
        if (arguments == null) {
            CalendarUtils.selectedDate = LocalDate.now();
        } else {
            int day = arguments.getInt("day");
            int month = arguments.getInt("month");
            int year = arguments.getInt("year");
            CalendarUtils.selectedDate = LocalDate.of(year, month, day);
        }
        setWeekView();
        setEventAdapter();


        Observer<Map<String, Boolean>> observerSelections = i -> {
            selections = i;
            setEventAdapter();
        };
        calendarViewModel.getSelections().observe(getActivity(), observerSelections);


        Observer<ArrayList<Event>> observerEvents = i -> {
            Event.updateEventsList(i);

            ArrayList<Event> dailyEvents = Event.eventsForDate(CalendarUtils.selectedDate);

            ArrayList<Event> events = new ArrayList<>();

            for (Event e : dailyEvents) {
                if (selections.containsKey(e.getGroupId())) {
                    if (selections.get(e.getGroupId()))
                        events.add(e);
                }
            }

            events.sort(new Comparator<Event>() {
                @Override
                public int compare(Event o1, Event o2) {
                    return o1.compareTo(o2);
                }
            });

            EventAdapter adapter2 = new EventAdapter(getContext(), events, this, calendarViewModel.getGroups());
            recyclerViewEvents.setAdapter(adapter2);
        };
        calendarViewModel.getEvents().observe(getActivity(), observerEvents);

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        initListeners();
        setEventAdapter();
    }

    private void changeLanguage() {
        Resources r = LocaleLanguage.getLocale(getActivity()).getResources();

        ((TextView) getActivity().findViewById(R.id.mondayTV)).setText(r.getString(R.string.mondayAbbreviation));
        ((TextView) getActivity().findViewById(R.id.tuesdayTV)).setText(r.getString(R.string.tuesdayAbbreviation));
        ((TextView) getActivity().findViewById(R.id.wednesdayTV)).setText(r.getString(R.string.wednesdayAbbreviation));
        ((TextView) getActivity().findViewById(R.id.thursdayTV)).setText(r.getString(R.string.thursdayAbbreviation));
        ((TextView) getActivity().findViewById(R.id.fridayTV)).setText(r.getString(R.string.fridayAbbreviation));
        ((TextView) getActivity().findViewById(R.id.saturdayTV)).setText(r.getString(R.string.saturdayAbbreviation));
        ((TextView) getActivity().findViewById(R.id.sundayTV)).setText(r.getString(R.string.sundayAbbreviation));

    }

    private void initWidgets() {
        monthYearText = view.findViewById(R.id.monthYearTV);
        calendarRecyclerView = view.findViewById(R.id.calendarRecyclerView);

        recyclerViewEvents = view.findViewById(R.id.recyclerViewEvents);
        recyclerViewEvents.setLayoutManager(new LinearLayoutManager(getContext()));

        previousWeekBtn = view.findViewById(R.id.previousWeekBtn);
        nextWeekBtn = view.findViewById(R.id.nextWeekBtn);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initListeners() {
        previousWeekBtn.setOnClickListener(this::previousWeekAction);
        nextWeekBtn.setOnClickListener(this::nextWeekAction);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setWeekView()
    {
        monthYearText.setText(monthYearFromDate(CalendarUtils.selectedDate));
        monthYearText.setTextColor(getResources().getColor(ThemeSwitcher.lightThemeSelected() ? R.color.black : R.color.white));
        previousWeekBtn.setTextColor(getResources().getColor(ThemeSwitcher.lightThemeSelected() ? R.color.black : R.color.white));
        nextWeekBtn.setTextColor(getResources().getColor(ThemeSwitcher.lightThemeSelected() ? R.color.black : R.color.white));

        ArrayList<LocalDate> days = daysInWeekArray(CalendarUtils.selectedDate);

        CalendarAdapter calendarAdapter = new CalendarAdapter(days, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(view.getContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
        setEventAdapter();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void previousWeekAction(View view)
    {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusWeeks(1);
        setWeekView();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void nextWeekAction(View view)
    {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusWeeks(1);
        setWeekView();
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onItemClick(int position, LocalDate date) {
        CalendarUtils.selectedDate = date;
        setWeekView();
    }
    
    @Override
    public void onResume() {
        super.onResume();
        setEventAdapter();
        changeLanguage();
    }

    private void setEventAdapter() {
        ArrayList<Event> dailyEvents = Event.eventsForDate(CalendarUtils.selectedDate);

        ArrayList<Event> events = new ArrayList<>();

        for (Event e : dailyEvents) {
            if (selections.containsKey(e.getGroupId())) {
                if (selections.get(e.getGroupId()))
                    events.add(e);
            }
        }

        events.sort(new Comparator<Event>() {
            @Override
            public int compare(Event o1, Event o2) {
                return o1.compareTo(o2);
            }
        });


        EventAdapter adapter = new EventAdapter(getContext(), events, this, calendarViewModel.getGroups());
        recyclerViewEvents.setAdapter(adapter);

        // TODO diria no se necessari aquest loadEvents
        calendarViewModel.loadEvents();
    }

    @Override
    public void onNoteClick(int position, Event event) {
        Context c = view.getContext();
        Intent i = new Intent(c, EditEventActivity.class);
        i.putExtra("event", event);

        //Trobem el grup al qual pertany l'event i el passem
        for (Group g : calendarViewModel.getGroups().getValue()) {
            if (g.getId().equals(event.getGroupId())) {
                i.putExtra("group", g);
            }
        }
        c.startActivity(i);
    }


}