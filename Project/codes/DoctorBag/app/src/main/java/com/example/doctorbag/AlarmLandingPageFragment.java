package com.example.doctorbag;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public final class AlarmLandingPageFragment extends Fragment implements View.OnClickListener {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.fragment_alarm_landing_page, container, false);

        final Button launchMainActivityBtn = (Button) v.findViewById(R.id.load_main_activity_btn);
        final Button dismiss = (Button) v.findViewById(R.id.dismiss_btn);

        launchMainActivityBtn.setOnClickListener(this);
        dismiss.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.load_main_activity_btn:
                Toast.makeText(getContext(),"Checking if the pill is taken or not!",Toast.LENGTH_LONG).show();
                startActivity(new Intent(getContext(), HomePageActivity.class));
                getActivity().finish();
                break;
            case R.id.dismiss_btn:
                Toast.makeText(getContext(),"Notification Sent to guardian",Toast.LENGTH_LONG).show();
                getActivity().finish();
                break;
        }

    }
}
