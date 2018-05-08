package edu.rsztandera.ubbgps;

import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

public class BeaconReferenceApplication extends Application implements BootstrapNotifier {
    private static final String TAG = "BeaconApp";
    private RegionBootstrap regionBootstrap;
    private BackgroundPowerSaver backgroundPowerSaver;
    private boolean haveDetectedBeaconsSinceBoot = false;
    private BeaconMonitoringActivity monitoringActivity = null;
    public List<String> allBeaconLogs = new LinkedList<String>();
    public static boolean ifBeaconsAvailable = false;
    public Collection<MtBeacon> allBeacons = new ArrayList<>();


    public void onCreate() {
        super.onCreate();
        BeaconManager beaconManager = org.altbeacon.beacon.BeaconManager.getInstanceForApplication(this);

        // layout expression for other beacon types, do a web search for "setBeaconLayout"
        beaconManager.getBeaconParsers().clear();
        // EDDYSTONE TLM
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("x,s:0-1=feaa,m:2-2=20,d:3-3,d:4-5,d:6-7,d:8-11,d:12-15"));
        // EDDYSTONE UID
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("s:0-1=feaa,m:2-2=00,p:3-3:-41,i:4-13,i:14-19"));
        // EDDYSTONE URL
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("s:0-1=feaa,m:2-2=10,p:3-3:-41,i:4-20v"));
        // iBeacon
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        // altBeacon
        //setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));

        addBeaconLog ("setting up background monitoring for beacons and power saving");
        // wake up the app when a beacon is seen
        Region region = new Region("backgroundRegion",
                null, null, null);
        regionBootstrap = new RegionBootstrap(this, region);
        backgroundPowerSaver = new BackgroundPowerSaver(this);
        addAvaliableBeacons();
    }

    @Override
    public void didEnterRegion(Region arg0) {
        // In this example, this class sends a notification to the user whenever a Beacon
        // matching a Region (defined above) are first seen.
        addBeaconLog( "did enter region.");
        if (!haveDetectedBeaconsSinceBoot) {
            addBeaconLog( "auto launching GeneralPositionActivity");

            // The very first time since boot that we detect an beacon, we launch the
            // GeneralPositionActivity
            Intent intent = new Intent(this, BeaconMonitoringActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivity(intent);
            haveDetectedBeaconsSinceBoot = true;
        } else {
            if (monitoringActivity != null) {
                // If the Monitoring Activity is visible, we log info about the beacons we have
                // seen on its display
                addBeaconLog("I see a beacon again" );
                monitoringActivity.updateBeaconMonitoringDisplay();
            } else {
                // If we have already seen beacons before, but the monitoring activity is not in
                // the foreground, we send a notification to the user on subsequent detections.
                addBeaconLog( "Sending notification.");
                sendNotification();
            }
        }
    }

    @Override
    public void didExitRegion(Region region) {
        addBeaconLog("I no longer see a beacon.");
        if (monitoringActivity != null) {
            monitoringActivity.updateBeaconMonitoringDisplay();
        }
    }

    @Override
    public void didDetermineStateForRegion(int state, Region region) {
        ifBeaconsAvailable = state==1;
        addBeaconLog("I have just switched from seeing/not seeing beacons: " + state);
        if (monitoringActivity != null) {
            monitoringActivity.updateBeaconMonitoringDisplay();
        }
    }

    private void sendNotification() {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setContentTitle("Beacon Reference Application")
                        .setContentText("An beacon is nearby.")
                        .setSmallIcon(R.mipmap.ic_launcher);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntent(new Intent(this, BeaconMonitoringActivity.class));
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        builder.setContentIntent(resultPendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }

    public void setMonitoringActivity(BeaconMonitoringActivity activity) {
        this.monitoringActivity = activity;
    }

    private void addAvaliableBeacons(){
        final String confFilePath = "beaconsPlacement.conf";
        List<String> mLines = new ArrayList<>();
        AssetManager am = this.getAssets();
        try {
            InputStream is = am.open(confFilePath);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;

            while ((line = reader.readLine()) != null)
                mLines.add(line);
        } catch (IOException e) {
            e.printStackTrace();
        }

//        this.allBeacons.add(new MtBeacon())
    }

    public MtBeacon getBeaconByLib(Beacon observedBeacon) {
        for ( MtBeacon b : this.allBeacons) {
            if(b.equals(observedBeacon))
                return b;
        }
        return null;
    }

    public void addBeaconLog(String line){
        allBeaconLogs.add(ParseTools.getCurrentDateTimeStr() + "> " + line);
        Log.d(TAG, line);
    }

}