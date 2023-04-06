package com.rz.bookapi.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class InputStreamUtils {

    public InputStreamUtils() {

    }
    public String getStringFromInputStream(InputStream inputStream) throws IOException {
        Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8);
        String result = scanner.useDelimiter("\\A").next();
        scanner.close();
        return result;
    }
}
