package com.ifmo.ct.homework.utils;

import org.androidannotations.annotations.sharedpreferences.DefaultString;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

@SharedPref
public interface SheetPrefs {

    @DefaultString("")
    String DM();

    @DefaultString("")
    String ADS();
}
