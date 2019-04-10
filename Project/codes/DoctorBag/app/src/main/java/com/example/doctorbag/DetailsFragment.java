package com.example.doctorbag;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DetailsFragment extends Fragment {

    View v;
    private TextView slot1,slot2;
    private DatabaseReference mUserDatabase;
    private DatabaseReference mDevicesDatabase;
    private FirebaseUser mCurrentUser;

    public DetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_details, container, false);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = mCurrentUser.getUid();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
        mDevicesDatabase = FirebaseDatabase.getInstance().getReference().child("Devices");
        mUserDatabase.keepSynced(true);
        mDevicesDatabase.keepSynced(true);

        slot1 = (TextView)v.findViewById(R.id.slot1status);
        slot2 = (TextView)v.findViewById(R.id.slot2status);

        mUserDatabase.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                final String deviceid = dataSnapshot.child("deviceid").getValue().toString();
                mDevicesDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        String Slot1 = dataSnapshot.child(deviceid).child("Slots").child("Slot1").getValue().toString();
                        String Slot2 = dataSnapshot.child(deviceid).child("Slots").child("Slot2").getValue().toString();

                        if(Slot1.equals("true"))
                        {
                            slot1.setText("Present");
                        }
                        else
                        {
                            slot1.setText("Absent");
                        }

                        if(Slot2.equals("true"))
                        {
                            slot2.setText("Present");
                        }
                        else
                        {
                            slot2.setText("Absent");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError)
                    {}
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        return v;
    }
}
