package com.ifmo.ct.homework.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.ifmo.ct.homework.App;
import com.ifmo.ct.homework.R;
import com.ifmo.ct.homework.activities.SignUpActivity;
import com.ifmo.ct.homework.model.Student;
import com.ifmo.ct.homework.model.User;

import org.androidannotations.annotations.Background;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FirebaseHelper {

    // user references
    public static final String STUDENT = "student";
    public static final String TEACHER = "teacher";
    public static final String pointsADS = "pointsADS";
    public static final String pointsDM = "pointsDM";
    // sheet links references
    public static final String SHEET = "sheets";

    public static DatabaseReference getStudentRef() {
        return FirebaseDatabase.getInstance().getReference(STUDENT);
    }

    public static DatabaseReference getTeacherRef() {
        return FirebaseDatabase.getInstance().getReference(TEACHER);
    }

    public static DatabaseReference getSheetsRef() {
        return FirebaseDatabase.getInstance().getReference(SHEET);
    }

    public static <T> void saveToFirebase(final T obj, final String groupKey, final String key, final DatabaseReference table,
                                          final Callback<T> callback) {

        DatabaseReference finalRef;
        if (groupKey == null) {
            finalRef = table.child(key);
        } else {
            finalRef = table.child(groupKey).child(key);
        }
        finalRef.setValue(obj).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                try {
                    callback.onSuccess(obj);
                } catch (Exception e) {
                    e.printStackTrace();
                    callback.onFail(R.string.undefined_error_message);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback.onFail(R.string.connection_fail_message);
            }
        });
    }

    public static <T> void saveToFirebaseIfNotExist(final T obj, final String groupKey, final String key, final DatabaseReference table,
                                                    final Class<T> castClass, final Callback<T> callback) {


        table.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.child(groupKey).child(key).exists()) {
                    saveToFirebase(obj, groupKey, key, table, callback);
                } else {
                    try {
                        callback.onSuccess(dataSnapshot.child(groupKey).child(key).getValue(castClass));
                    } catch (Exception e) {
                        e.printStackTrace();
                        callback.onFail(R.string.undefined_error_message);
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onFail(R.string.connection_fail_message);
            }
        });
    }

    public static <T> void getFromFirebase(final String groupKey, final String key, final DatabaseReference table,
                                           GenericTypeIndicator<T> indicator, final Callback<T> callback) {

        table.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                T obj;
                if (groupKey == null && key != null) {
                    obj = dataSnapshot.child(key).getValue(indicator);
                } else if (groupKey != null && key == null) {
                    obj = dataSnapshot.child(groupKey).getValue(indicator);
                } else if (groupKey != null && key != null) {
                    obj = dataSnapshot.child(groupKey).child(key).getValue(indicator);
                } else {
                    obj = dataSnapshot.getValue(indicator);
                }
                if (obj == null) {
                    callback.onFail(R.string.firebase_error_message);
                } else {
                    try {
                        callback.onSuccess(obj);
                    } catch (Exception e) {
                        e.printStackTrace();
                        callback.onFail(R.string.undefined_error_message);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onFail(R.string.connection_fail_message);
            }
        });
    }

    public static <T> void getListFromFirebase(final String groupKey, final DatabaseReference table,
                                               final GenericTypeIndicator<T> indicator,
                                               final Callback<List<T>> callback) {


        table.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<T> items = new ArrayList<>();
                DataSnapshot finalRef = dataSnapshot;
                if (groupKey != null) {
                    finalRef = dataSnapshot.child(groupKey);
                }
                if (finalRef.exists()) {
                    for (DataSnapshot postSnapshot : finalRef.getChildren()) {
                        items.add(postSnapshot.getValue(indicator));
                    }
                    try {
                        callback.onSuccess(items);
                    } catch (Exception e) {
                        e.printStackTrace();
                        callback.onFail(R.string.undefined_error_message);
                    }
                } else {
                    callback.onFail(R.string.firebase_error_message);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onFail(R.string.connection_fail_message);
            }
        });
    }

}