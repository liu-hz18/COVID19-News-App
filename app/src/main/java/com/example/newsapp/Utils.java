package com.example.newsapp;

import android.os.Build;

import androidx.annotation.RequiresApi;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Utils {
}

class Converter {
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static List<Integer> StringListToList(@NotNull String listString, final String splitKey, final int topk) {
        String str = listString.toString().replace("[","").replace("]","");
        List<String> numbers = Arrays.asList(Arrays.copyOf(str.split(splitKey), topk));
        //Log.d("Converter", numbers.toString());
        return numbers.stream()
                .map(Converter::NullToInt)
                .collect(Collectors.toList());
    }

    @NotNull
    private static Integer NullToInt(@NotNull String numberlike) {
        return numberlike.equals("null") ? 0 : Integer.parseInt(numberlike);
    }
}
