package com.example.androidfinal;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class SettingsFragment extends Fragment {
    View inflatedView;
    EditText ed_username, ed_password;
    Button bt_logout, btn_changeUser, btn_changePass, btn_adminPanel;
    SwitchCompat notification_switch;
    Spinner spinner_currency;
    FirebaseAuth mFirebaseAuth;
    SettingsSingleton singleton;
    DatabaseReference databaseReferenceUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        inflatedView = inflater.inflate(R.layout.fragment_settings, container, false);

        castVar();
        initializeWidgets();

        setListeners();

        return inflatedView;
    }

    public void castVar()
    {
        mFirebaseAuth = FirebaseAuth.getInstance();

        ed_username = (EditText)inflatedView.findViewById(R.id.ed_username);
        ed_password = (EditText)inflatedView.findViewById(R.id.ed_password);

        bt_logout = (Button)inflatedView.findViewById(R.id.bt_logout);
        btn_changeUser = (Button)inflatedView.findViewById(R.id.btn_changeUser);
        btn_changePass = (Button)inflatedView.findViewById(R.id.btn_changePass);
        btn_adminPanel = (Button)inflatedView.findViewById(R.id.bt_adminPanel);

        notification_switch = (SwitchCompat)inflatedView.findViewById(R.id.notification_switch);
        spinner_currency = (Spinner)inflatedView.findViewById(R.id.currency_spinner);

        singleton = SettingsSingleton.getInstance();

        databaseReferenceUser = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getUid());
    }

    public void setListeners()
    {
        // ---------- Logout Button -------------------------------------
        bt_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getActivity() != null)
                {

                    mFirebaseAuth.signOut();
                    Intent intent = new Intent(SettingsFragment.this.getContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    getActivity().finish();
                    getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
                else
                    Toast.makeText(getActivity(), "Error Logging Out", Toast.LENGTH_SHORT).show();
            }
        });

        // ---------- Admin Panel Button -------------------------------------

        btn_adminPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), AdminPanel.class);
                startActivity(i);
            }
        });

        // --------------- Change username Button ---------------------------
        btn_changeUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ed_username.getText().toString().length() > 15)
                {
                    ed_username.setError("Username too long");
                }
                else
                {
                    databaseReferenceUser.child("name").setValue(ed_username.getText().toString());
                    singleton.setCurUsername(ed_username.getText().toString());

                    Toast.makeText(getActivity(), "Username Changed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // --------------- Notification Switch -------------------------------
        notification_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(singleton.isNotificationStatus())
                    singleton.setNotificationStatus(false);
                else
                    singleton.setNotificationStatus(true);
            }
        });

        // --------------- Currency Spinner -----------------------------------
        spinner_currency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                singleton.setCurrency(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //Do nothing
            }
        });
    }

    public void initializeWidgets()
    {
        ed_username.setText(singleton.getCurUsername());

        if(mFirebaseAuth.getCurrentUser().getEmail().equals("vextorn.soung@gmail.com"))
        {
            btn_adminPanel.setVisibility(View.VISIBLE);
        }

        if(singleton.getCurrency() == 1)
            spinner_currency.setSelection(1);

        if(singleton.isNotificationStatus())
            notification_switch.setChecked(true);
        else
            notification_switch.setChecked(false);
    }

    /* FUNCTION USED TO FIX LOGOUT PROBLEMS, found a better fix so commented
    public static void triggerRebirth(Context context) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(context.getPackageName());
        ComponentName componentName = intent.getComponent();
        Intent mainIntent = Intent.makeRestartActivityTask(componentName);
        context.startActivity(mainIntent);
        Runtime.getRuntime().exit(0);
    }
     */

}
