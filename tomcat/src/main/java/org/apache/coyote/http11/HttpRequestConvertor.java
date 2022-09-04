package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

class HttpRequestConvertor {

    public HttpRequest convert(final InputStream inputStream) throws IOException {
        final List<String> lines = readRequestLines(inputStream);
        final String httpRequest = String.join("\r\n", lines);
        return HttpRequest.from(httpRequest);
    }

    private List<String> readRequestLines(final InputStream inputStream) throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        final List<String> lines = new ArrayList<String>();

        String line = reader.readLine();
        while (!line.equals("")) {
            lines.add(line);
            line = reader.readLine();
        }
        return lines;
    }
}
