//package edu.rsztandera.ubbgps;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//
//public class TestSinglePoint {
//    public int testId;
//    public long timestamp;
//    public String testCaseNo;
//    public int meter;
//    public ArrayList<Integer> rssiMeasures;
//
//    public String getRssiMeasuresRepr(){
//        JSONArray ja = new JSONArray(this.rssiMeasures);
//        return ja.toString();
//    }
//
//    public void setRssiMeasures(String measuresRepr) {
//        JSONArray ja = null;
//        try {
//            ja = new JSONArray(measuresRepr);
//        } catch (JSONException e) {
//            e.printStackTrace();
//            return;
//        }
//
//        this.rssiMeasures.clear();
//        for (int i=0; i < ja.length(); i++){
//            int measure = 0;
//            try {
//                measure = ja.getInt(i);
//                this.rssiMeasures.add(measure);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//
//    public TestSinglePoint() {}
//    public TestSinglePoint(int id, String studentname) {
//        this.studentID = id;
//        this.studentName = studentname;
//    }
//}
//
