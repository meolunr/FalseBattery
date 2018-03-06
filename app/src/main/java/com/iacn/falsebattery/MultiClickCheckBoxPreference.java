package com.iacn.falsebattery;

import android.content.Context;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import java.lang.reflect.Method;

/**
 * Created by iAcn on 2018/3/6
 * Email i@iacn.me
 */

public class MultiClickCheckBoxPreference extends Preference {

    private Method performClickMethod;

    public MultiClickCheckBoxPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setLayoutResource(R.layout.multiclick_checkbox_preference);

        try {
            performClickMethod = getClass().getMethod("performClick", PreferenceScreen.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public MultiClickCheckBoxPreference(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MultiClickCheckBoxPreference(Context context) {
        this(context, null);
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        View layout = super.onCreateView(parent);
        CheckBox checkboxView = layout.findViewById(android.R.id.checkbox);

        checkboxView.setChecked(getPersistedBoolean(false));

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (performClickMethod != null) {
                    try {
                        performClickMethod.invoke(MultiClickCheckBoxPreference.this, (Object) null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        checkboxView.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                persistBoolean(isChecked);
                callChangeListener(isChecked);
            }
        });

        return layout;
    }
}