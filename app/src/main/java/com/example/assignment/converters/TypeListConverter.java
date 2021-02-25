package com.example.assignment.converters;

import android.text.TextUtils;

import androidx.room.TypeConverter;

import java.util.Arrays;
import java.util.List;

public class TypeListConverter {
    @TypeConverter
    public static List<String> stringToList(String stringList) {
        return Arrays.asList(stringList.split(","));
    }

    @TypeConverter
    public static String listToString(List<String> listString) {
        return TextUtils.join(",", listString);
    }
}
