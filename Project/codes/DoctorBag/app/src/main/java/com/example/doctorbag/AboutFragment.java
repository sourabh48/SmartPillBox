package com.example.doctorbag;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class AboutFragment extends Fragment {

    View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_about, container, false);

        Element versionElement = new Element();
        versionElement.setTitle("Version 1.0");

        Element adsElement = new Element();
        adsElement.setTitle("Advertise with us");

        View aboutPage = new AboutPage(getActivity())

                .isRTL(false)

                .setDescription("This app is the UI of IOT device. We are here to help you to do things with ease.\n" +
                        " So no more worries we are here to remind you of your daily pills.\n" +
                        " 4rd year project by..\n" +
                        " Sourabh Sarkar (1515048) \n" +
                        " Srijita Gayen (1515049) \n" +
                        " Prerna Singh (1515032) \n" )

                .setImage(R.drawable.about)
                .addItem(new Element().setTitle("Version 1.0"))
                .addGroup("Connect with us")
                .addEmail("sourabh.654321@outlook.com")
                .addWebsite("https://github.com/sourabh48")
                .addFacebook("sourabh.sarkar.750")
                .create();

        return aboutPage;
    }

    public AboutFragment() {
        // Required empty public constructor
    }

}