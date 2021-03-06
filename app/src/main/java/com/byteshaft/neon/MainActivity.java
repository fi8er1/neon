package com.byteshaft.neon;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

@SuppressWarnings("deprecation")
public class MainActivity extends Activity implements View.OnClickListener {

    private static MainActivity instance = null;
    static Button mSwitcher;
    private Helpers mHelpers;
    private RemoteUpdateUiHelpers mRemoteUi;

    public static MainActivity getContext() {
        return instance;
    }

    public static void stopApp() {
        if (instance != null) {
            instance.finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Flashlight.activityRunning = true;
        instance = this;
        initializeXmlReferences();
        initializeClasses();
        mHelpers.checkFlashlightAvailability();
        mSwitcher.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (Flashlight.isOn()) {
            stopService(getFlashlightServiceIntent());
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Helpers.isCameraInUse() && !Flashlight.isOn()) {
            Helpers.showFlashlightBusyDialog(this);
        }
        if (Flashlight.isOn()) {
            mRemoteUi.setUiButtonsOn(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Flashlight.isOn()) {
            stopService(getFlashlightServiceIntent());
        }
    }

    @Override
    public void onClick(View view) {
        // If for any reason, flashlight is in the process
        // of being ON or OFF, just eat the toggle to avoid
        // any delayed toggle loop.
        if (Flashlight.isToggleInProgress()) {
            return;
        }
        switch (view.getId()) {
            case R.id.switcher:
                if (!Flashlight.isOn()) {
                    mRemoteUi.setUiButtonsOn(true);
                    startService(getFlashlightServiceIntent());
                    Flashlight.setIsRunningFromWidget(false);
                } else {
                    mRemoteUi.setUiButtonsOn(false);
                    stopService(getFlashlightServiceIntent());
                    Flashlight.setBusyByWidget(false);
                }
        }
    }

    private Intent getFlashlightServiceIntent() {
        return new Intent(MainActivity.this, FlashlightService.class);
    }

    private void initializeClasses() {
        mHelpers = new Helpers(MainActivity.this);
        mRemoteUi = new RemoteUpdateUiHelpers(MainActivity.this);
    }

    private void initializeXmlReferences() {
        mSwitcher = (Button) findViewById(R.id.switcher);
    }
}
