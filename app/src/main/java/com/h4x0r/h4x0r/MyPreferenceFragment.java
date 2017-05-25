package com.h4x0r.h4x0r;

import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MyPreferenceFragment extends android.preference.PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            //getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
            addPreferencesFromResource(R.xml.preference);
        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = super.onCreateView(inflater, container, savedInstanceState);
            view.setBackgroundColor(Color.WHITE);

            return view;
        }
    }

