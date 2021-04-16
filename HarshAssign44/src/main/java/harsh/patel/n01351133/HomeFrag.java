package harsh.patel.n01351133;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextClock;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;



public class HomeFrag extends Fragment {
    public static final String SHARED_PREF = "userPref";
    public static final String ASWTICH = "switch";
    public static final String CLOCK = "clock";
    public static final String BG_COLOR = "bgColor";
    HarshSharedViewModel viewModel;
    Calendar calendar;
    int year, month, day;
    TextView currentDate;

    Spinner spinner;
    Context context;
    ImageButton imageButton;
    public TextClock clock;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    private boolean switchOnOff;
    String chosenBgColor;
    View view;
    String localLanguage;

    public HomeFrag() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.lu_home, container, false);
        view = root;
        context = getActivity().getApplicationContext();
        currentDate = root.findViewById(R.id.HarshCurrentDateTV);
        //courses = getResources().getStringArray(R.array.my_courses);

        clock = root.findViewById(R.id.HarshTextClock);
        sharedPref = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        boolean is12HrFormat = sharedPref.getBoolean(CLOCK, true);
        chosenBgColor = sharedPref.getString(BG_COLOR, getString(R.string.green));
        root.setBackgroundColor(Color.parseColor(chosenBgColor));
        localLanguage = Locale.getDefault().getLanguage().toString();
        //clock.setFormat12Hour("kk:mm:ss");
        // update clock
//        Toast.makeText(getContext(), "Selected: "+local, Toast.LENGTH_LONG).show();
        setClock(is12HrFormat);


        Calendar today = Calendar.getInstance();
        year = today.get(Calendar.YEAR);
        month = today.get(Calendar.MONTH) + 1;
        day = today.get(Calendar.DAY_OF_MONTH);
        String dateText = year + "/" + month + "/" + day;
        currentDate.setText(dateText);

        return root;
    }





    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(HarshSharedViewModel.class);
        viewModel.getFormat().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                setClock(aBoolean);
            }
        });

        viewModel.getHomeBgColor().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String color) {
                view.setBackgroundColor(Color.parseColor(color));
            }
        });
    }
    public void setClock(boolean is12HrFmt){
        if (is12HrFmt) {
            if(localLanguage.equals("fr")){
                clock.setFormat24Hour("hh:mm:ss a");
            }
            else{
                clock.setFormat12Hour("hh:mm:ss a");
            }
        } else {
            clock.setFormat12Hour("kk:mm:ss");
        }
    }
}