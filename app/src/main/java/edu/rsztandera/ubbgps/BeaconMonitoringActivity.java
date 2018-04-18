package edu.rsztandera.ubbgps;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;

public class BeaconMonitoringActivity extends Activity implements BeaconConsumer {
    protected static final String TAG = "MonitoringActivity";
    private BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);
    private boolean isRanging = false;
    private RangeNotifier rangeNotifier = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoring);
        enableLogScrol();
        updateBeaconMonitoringDisplay();

    }

    private void enableLogScrol() {
        EditText EtOne = (EditText) findViewById(R.id.beaconMonLog);
        EtOne.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (v.getId() == R.id.beaconMonLog) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_UP:
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                    }
                }
                return false;
            }
        });
    }

    public void onRangingClicked(View view) {
//        Intent myIntent = new Intent(this, BeaconRangingActivity.class);
//        this.startActivity(myIntent);
        if(isRanging) {
            beaconManager.removeRangeNotifier(rangeNotifier);
            beaconManager.unbind(this);
        }
        else
            beaconManager.bind(this);
        isRanging = !isRanging;
    }

    @Override
    public void onResume() {
        super.onResume();
        BeaconReferenceApplication app = (BeaconReferenceApplication) this.getApplicationContext();
        app.setMonitoringActivity(this);
        if (beaconManager.isBound(this)) beaconManager.setBackgroundMode(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        ((BeaconReferenceApplication) this.getApplicationContext()).setMonitoringActivity(null);
        if (beaconManager.isBound(this)) beaconManager.setBackgroundMode(true);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    @Override
    public void onBeaconServiceConnect() {
        final BeaconReferenceApplication app = (BeaconReferenceApplication) this.getApplicationContext();
        rangeNotifier = new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() == 0) {
                    return;
                }
                String beaconsFoundStr = "";
                for (Beacon firstBeacon : beacons) {
                    MtBeacon positionBeacon = app.getBeaconByLib(firstBeacon);
                    if (beaconsFoundStr != "")
                        beaconsFoundStr += "\n";
                    beaconsFoundStr += "Beacon " +firstBeacon.getBluetoothAddress()+ " (" + firstBeacon.getDistance() + " m)";
                }
                app.addBeaconLog(beaconsFoundStr);
                updateBeaconMonitoringDisplay();
            }
        };
        beaconManager.addRangeNotifier(rangeNotifier);

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {   }
    }

    public void updateBeaconMonitoringDisplay() {
        final BeaconReferenceApplication app = (BeaconReferenceApplication) this.getApplicationContext();
        runOnUiThread(new Runnable() {
            public void run() {
                EditText editText = (EditText)BeaconMonitoringActivity.this
                        .findViewById(R.id.beaconMonLog);
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
                String parsersIssued = ""; //TODO can be stored at the beggining; during the setup
                for (BeaconParser beaconParser: beaconManager.getBeaconParsers()) {
                    if (parsersIssued != "")
                        parsersIssued += "\n";
                    parsersIssued += beaconParser.getLayout();
                }
                tv.setText(parsersIssued);
                tv = (TextView) BeaconMonitoringActivity.this.findViewById(R.id.beaconConfPersist);
                tv.setText(beaconManager.isRegionStatePersistenceEnabled()? "Enabled" : "Not enabled");
            }
        });
    }

}
