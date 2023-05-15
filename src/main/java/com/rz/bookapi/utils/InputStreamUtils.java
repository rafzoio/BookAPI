package com.rz.bookapi.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class InputStreamUtils {

    public InputStreamUtils() {
    }

    /**
     * Convert input stream to string
     * @param inputStream inputStream from request body
     * @return data as a string
     */
    public String getStringFromInputStream(InputStream inputStream) throws IOException {
        Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8);
        String result = scanner.useDelimiter("\\A").next();
        scanner.close();
        return result;
    }
}
