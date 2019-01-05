package com.ifmo.ct.homework.utils;

public interface Callback<T> {

    void onSuccess(T obj) throws Exception;
    void onFail(int messageId);

}
