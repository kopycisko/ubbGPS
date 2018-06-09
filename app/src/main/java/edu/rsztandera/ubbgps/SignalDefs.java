package edu.rsztandera.ubbgps;

public class SignalDefs {
    public static final float RSSI_NOT_KNOWN = 1;
    public static final float RSSI_IMMEDIATE = -85;
    public static final float RSSI_FAR = -95;
    public static final float RSSI_MAX_EPS = 2;
    public static final double LOWPASS_CUTOFF = 0.25;

    public static boolean isRssiValidValue(float rssi) {
        return rssi < 0;
    }
    public static boolean isRssiImmediate(float rssi) {
        return isRssiValidValue(rssi) && rssi > RSSI_IMMEDIATE;
    }
    public static boolean isRssiFar(float rssi) {
        return isRssiValidValue(rssi) && rssi < RSSI_FAR;
    }
    public static double getLowpassRssiValue(double currentRssi, double lastRssi) {
        if (lastRssi == RSSI_NOT_KNOWN)
            return currentRssi;
        return lastRssi + LOWPASS_CUTOFF*(currentRssi-lastRssi);
    }

}
