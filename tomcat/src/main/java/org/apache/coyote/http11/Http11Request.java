package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class Http11Request {

    private String method;
    private String uri;
    private String protocol;
    private Map<String, String> headers = new HashMap<String, String>();

    public Http11Request(final InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        readRequestLine(bufferedReader);
        readHeaders(bufferedReader);
    }

    private void readRequestLine(final BufferedReader bufferedReader) throws IOException {
        String requestLine = bufferedReader.readLine();
        String[] splitRequestLine = requestLine.split(" ");

        method = splitRequestLine[0];
        uri = splitRequestLine[1];
        protocol = splitRequestLine[2];
    }

    private void readHeaders(final BufferedReader bufferedReader) throws IOException {
        String line;
        while((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
            String[] split = line.split(":");
            String header = split[0].trim();
            String value = split[1].trim();

            headers.put(header, value);
        }
    }

    public String getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public String getProtocol() {
        return protocol;
    }
}
