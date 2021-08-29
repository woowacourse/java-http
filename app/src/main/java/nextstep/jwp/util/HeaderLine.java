package nextstep.jwp.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

public class HeaderLine {

    private List<String> headerLines;

    public HeaderLine(List<String> headerLines) {
        this.headerLines = headerLines;
    }

    public static HeaderLine readFromInputStream(InputStream inputStream) throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        final List<String> headerLine = new LinkedList<>();
        while (reader.ready()) {
            headerLine.add(reader.readLine());
        }
        return new HeaderLine(headerLine);
    }

    public String getRequestURL() {
        String firstLine = headerLines.get(0);
        String[] splitFirstLine = firstLine.split(" ");
        return splitFirstLine[1];
    }
}
