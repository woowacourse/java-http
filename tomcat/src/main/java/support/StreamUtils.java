package support;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StreamUtils {

    private StreamUtils() {
    }

    public static String readAllLines(InputStream inputStream) {
        final StringBuffer stringBuffer = new StringBuffer();
        try {
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            while (true) {
                if (!bufferedReader.ready()) {
                    break;
                }
                final String line = bufferedReader.readLine();
                stringBuffer.append(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return stringBuffer.toString();
    }
}
