package com.rz.bookapi.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class InputStreamUtils {

    public InputStreamUtils() {

    }

    public String getStringFromInputStream(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder requestDataBuilder = new StringBuilder();

        String line;

        while ((line = reader.readLine()) != null) {
            requestDataBuilder.append(line);
        }

        return requestDataBuilder.toString();
    }
}
