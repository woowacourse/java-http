package support;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class BufferedReaderFactory {

    public static BufferedReader getBufferedReader(final String value) {
        InputStream inputStream = new ByteArrayInputStream(value.getBytes());
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        return new BufferedReader(inputStreamReader);
    }
}
