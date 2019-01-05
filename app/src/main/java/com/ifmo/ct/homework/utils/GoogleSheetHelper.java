package com.ifmo.ct.homework.utils;

import android.support.annotation.NonNull;

import com.google.gson.internal.LinkedHashTreeMap;
import com.ifmo.ct.homework.model.GoogleSheet;
import com.ifmo.ct.homework.model.Row;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class GoogleSheetHelper {

    public static GoogleSheet stringToGoogleSheet(@NonNull String string) throws IOException {
        String data = string.substring(string.indexOf("(") + 1);
        data = data.substring(0, data.length() - 2);
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<GoogleSheet> jsonAdapter = moshi.adapter(GoogleSheet.class);
        GoogleSheet googleSheet = jsonAdapter.fromJson(data);
        return googleSheet;
    }

    public static String findStudent(GoogleSheet googleSheet, String pattern, String groupNum) {
        List<Row> rows = googleSheet.getTable().getRows();
        int countPossibles = 0;
        String nameFound = null;
        for (int i = 1; i < rows.size(); i++) {
            List<Object> data = rows.get(i).getC();
            try {

                String studentName = ((Map<String, String>) data.get(0)).get("v");
                String studentGroup = ((Map<String, String>) data.get(1)).get("v").toLowerCase().trim();

                if (studentGroup.contains(groupNum) && studentName.toLowerCase().trim().contains(pattern)) {
                    countPossibles++;
                    nameFound = studentName;
                }
            }
            catch (Exception ignored){
            }
        }
        if (countPossibles != 1) {
            return null;
        }
        return nameFound;
    }

    public static int getPoints(GoogleSheet googleSheet, String name) {
        List<Row> rows = googleSheet.getTable().getRows();
        for (int i = 1; i < rows.size(); i++) {
            List<Object> data = rows.get(i).getC();
            try {

                String studentName = ((Map<String, String>) data.get(0)).get("v");
                int studentPoints=   ((Map<String, Double>) data.get(6)).get("v").intValue();

                if (studentName.equals(name) ) {
                    return studentPoints;
                }
            }
            catch (Exception ignored){
            }
        }
        return -1;
    }

}
