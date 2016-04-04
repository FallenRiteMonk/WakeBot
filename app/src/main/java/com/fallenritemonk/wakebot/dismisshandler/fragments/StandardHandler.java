package com.fallenritemonk.wakebot.dismisshandler.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.fallenritemonk.wakebot.R;
import com.fallenritemonk.wakebot.dismisshandler.DismissHandler;

public class StandardHandler extends Fragment {
    private final String LOG_TAG = "StandardHandlerFragment";

    public StandardHandler() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_standard_handler, container, false);
        Button dismissButton = (Button) view.findViewById(R.id.standard_dismiss_button);
        dismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((DismissHandler) getActivity()).dismiss();
            }
        });
        return view;
    }
}
