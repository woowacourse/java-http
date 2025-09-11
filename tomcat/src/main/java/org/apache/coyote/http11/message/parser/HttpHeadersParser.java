package org.apache.coyote.http11.message.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.http11.message.HttpHeaders;

public class HttpHeadersParser implements Parser<HttpHeaders> {

    @Override
    public HttpHeaders parse(BufferedReader reader) throws IOException {
        List<String> lines = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null && !line.isBlank()) {
            lines.add(line);
        }
        return HttpHeaders.fromLines(lines);
    }
}
