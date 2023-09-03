package kokodak;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StringReader {

    public static List<String> readAll(final BufferedReader bufferedReader) throws IOException {
        final List<String> result = new ArrayList<>();
        String readLine = bufferedReader.readLine();
        while (!"".equals(readLine)) {
            result.add(readLine);
            readLine = bufferedReader.readLine();
        }
        return result;
    }

    private StringReader() {
    }
}
