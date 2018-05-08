package edu.rsztandera.ubbgps;

public class Definitions{
    public static final float RSSI_NOT_KNOWN = 1;
    public static final float RSSI_IMMEDIATE = -85;
    public static final float RSSI_FAR = -95;
    public static final float RSSI_MAX_EPS = 2;

    public static boolean isRssiValidValue(float rssi) {
        return rssi < 0;
    }
    public static boolean isRssiImmediate(float rssi) {
        return isRssiValidValue(rssi) && rssi > RSSI_IMMEDIATE;
    }
    public static boolean isRssiFar(float rssi) {
        return isRssiValidValue(rssi) && rssi < RSSI_FAR;
    }

}
