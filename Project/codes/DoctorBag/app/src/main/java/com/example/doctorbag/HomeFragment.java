package com.example.doctorbag;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private DatabaseReference mUserDatabase;
    private DatabaseReference mDevicesDatabase;
    private FirebaseUser mCurrentUser;
    private TextView heartRate;
    private String switchstate;

    private LineChart Temp_linechart;
    ArrayList<Entry> yData;
    private ImageView img;
    DatabaseReference heart;
    DatabaseReference mPostReference;
    ValueEventListener valueEventListener;
    View v;
    private MediaPlayer beat;
    Switch security_key;
    Animation animBlink;


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_home, container, false);
        heartRate = (TextView)v.findViewById(R.id.heartrate);

        beat = MediaPlayer.create(getContext(), R.raw.beat);
        img = (ImageView)v.findViewById(R.id.indicator);


        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = mCurrentUser.getUid();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
        mDevicesDatabase = FirebaseDatabase.getInstance().getReference().child("Devices");
        mUserDatabase.keepSynced(true);
        mDevicesDatabase.keepSynced(true);

        animBlink = AnimationUtils.loadAnimation(getContext(),R.anim.blink);

        Temp_linechart = (LineChart)v.findViewById(R.id.heartgraphrate);

        security_key = (Switch)v.findViewById(R.id.on_switch);

        security_key.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    mUserDatabase.child("security").setValue(true);
                }
                else
                {
                    mUserDatabase.child("security").setValue(false);
                }
            }
        });


        mUserDatabase.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String deviceid = dataSnapshot.child("deviceid").getValue().toString();

                heart =  mDevicesDatabase.child(deviceid).child("Heartsensor");

                switchstate = dataSnapshot.child("security").getValue().toString();
                if(switchstate.equals("true"))
                {
                    security_key .setChecked(true);
                }
                else
                {
                    security_key .setChecked(false);
                }

                heart.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        String value = dataSnapshot.child("Heartbeat").getValue().toString();
                        heartRate.setText(value);


                        mPostReference =heart.child("History");
                        mPostReference.addValueEventListener(valueEventListener= new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                yData = new ArrayList<>();
                                float i =0;
                                for (DataSnapshot ds : dataSnapshot.getChildren()){
                                    i=i+1;
                                    String SV = ds.child("pulse").getValue().toString();
                                    Float SensorValue = Float.parseFloat(SV);
                                    yData.add(new Entry(i,SensorValue));
                                }

                                YAxis leftAxis = Temp_linechart.getAxisLeft();
                                LimitLine ll = new LimitLine(80f, "Critical Blood Pressure");
                                ll.setLineColor(Color.RED);
                                ll.setLineWidth(4f);
                                ll.setTextColor(Color.BLACK);
                                ll.setTextSize(12f);
                                leftAxis.addLimitLine(ll);

                                final LineDataSet lineDataSet = new LineDataSet(yData,"Pulse");
                                LineData data = new LineData(lineDataSet);
                                Temp_linechart.setData(data);
                                Temp_linechart.notifyDataSetChanged();
                                Temp_linechart.invalidate();

                                img.startAnimation(animBlink);
                                playBeat();

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return v;
    }

    @Override
    public void onPause()
    {
        beat.stop();
        super.onPause();
    }

    private void playBeat() {
        if(!beat.isPlaying()){
            beat.start();
        }else{
            beat.stop();
            beat.release();
            beat = MediaPlayer.create(getActivity(), R.raw.beat);
            beat.start();
        }
    }

}
