package com.materialnotes.widget;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.materialnotes.R;

import roboguice.inject.InjectView;

public class AboutNoticeDialog extends DialogFragment {

    private static final String TAG = AboutNoticeDialog.class.getSimpleName();

    @InjectView(R.id.version_text) private TextView versionText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_about_notice, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
    }
}