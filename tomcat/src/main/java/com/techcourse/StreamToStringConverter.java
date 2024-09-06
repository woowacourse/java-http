package com.techcourse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public abstract class StreamToStringConverter {
    public static List<String> convertToLines(InputStream inputStream) {
        List<String> ret = new ArrayList<>();
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String s;
            while ((s = bufferedReader.readLine()) != null && !s.isEmpty()) {
                ret.add(s);
            }
            StringBuilder sb = new StringBuilder();
            if (bufferedReader.ready()) {
                while (bufferedReader.ready()) {
                    sb.append(Character.toString(bufferedReader.read()));
                }
                ret.add(sb.toString());
            }
            System.out.println(ret);
        } catch (IOException e) {
            throw new RuntimeException("I/O error occurred.");
        }
        return ret;
    }
}
