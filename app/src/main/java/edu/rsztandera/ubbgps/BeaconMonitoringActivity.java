package edu.rsztandera.ubbgps;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.altbeacon.beacon.BeaconManager;

public class BeaconMonitoringActivity extends Activity {
    protected static final String TAG = "MonitoringActivity";
    private BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoring);
        updateBeaconMonitoringDisplay();
    }

    public void onRangingClicked(View view) {
        Intent myIntent = new Intent(this, BeaconRangingActivity.class);
        this.startActivity(myIntent);
    }

    @Override
    public void onResume() {
        super.onResume();
        BeaconReferenceApplication app = (BeaconReferenceApplication) this.getApplicationContext();
        app.setMonitoringActivity(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        ((BeaconReferenceApplication) this.getApplicationContext()).setMonitoringActivity(null);
    }


    public void updateBeaconMonitoringDisplay() {
        final BeaconReferenceApplication app = (BeaconReferenceApplication) this.getApplicationContext();
        runOnUiThread(new Runnable() {
            public void run() {
                EditText editText = (EditText)BeaconMonitoringActivity.this
                        .findViewById(R.id.monitoringText);
                editText.setText("");
                for (String item: app.allBeaconLogs) {
                    editText.append(item+"\n");
                }

                TextView tv = (TextView) BeaconMonitoringActivity.this.findViewById(R.id.beaconMonStatus);
                tv.setText(app.ifBeaconsAvailable? "Beacons detected." : "No beacons in range.");
                tv = (TextView) BeaconMonitoringActivity.this.findViewById(R.id.beaconConfBgBetwScanPeriod);
                tv.setText(Long.toString(beaconManager.getBackgroundBetweenScanPeriod()));
                tv = (TextView) BeaconMonitoringActivity.this.findViewById(R.id.beaconConfBgScanPeriod);
                tv.setText(Long.toString(beaconManager.getBackgroundScanPeriod()));
                tv = (TextView) BeaconMonitoringActivity.this.findViewById(R.id.beaconConfFgBetwScanPeriod);
                tv.setText(Long.toString(beaconManager.getForegroundBetweenScanPeriod()));
                tv = (TextView) BeaconMonitoringActivity.this.findViewById(R.id.beaconConfFgScanPeriod);
                tv.setText(Long.toString(beaconManager.getForegroundScanPeriod()));
                tv = (TextView) BeaconMonitoringActivity.this.findViewById(R.id.beaconConfParsers);
                tv.setText(beaconManager.getBeaconParsers().get(0).getLayout());
                tv = (TextView) BeaconMonitoringActivity.this.findViewById(R.id.beaconConfPersist);
                tv.setText(beaconManager.isRegionStatePersistenceEnabled()? "Enabled" : "Not enabled");
            }
        });
    }

}
