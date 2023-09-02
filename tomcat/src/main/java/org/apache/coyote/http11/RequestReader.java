package org.apache.coyote.http11;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class RequestReader {

    private static final String SPACE = " ";
    private static final String ACCEPT = "Accept";

    private final Map<String, String> headers = new HashMap<>();
    private String method;
    private String resource;
    private String protocol;

    public final InputStream inputStream;

    public RequestReader(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public String read() throws IOException {
        setRequestLine();
        setHeaders();
        return resource;
    }

    private void setRequestLine() throws IOException {
        StringBuilder stringBuilder = new StringBuilder();

        int c;
        while ((c = inputStream.read()) != -1) {
            stringBuilder.append((char) c);
            if (stringBuilder.toString().endsWith("\r\n")) {
                break;
            }
        }
        String[] requestLine = stringBuilder.toString().split(SPACE);
        method = requestLine[0];
        resource = requestLine[1];
        protocol = requestLine[2];
    }

    private void setHeaders() throws IOException {
        StringBuilder stringBuilder = new StringBuilder();

        int c;
        while ((c = inputStream.read()) != -1) {
            stringBuilder.append((char) c);
            if (stringBuilder.toString().endsWith("\r\n\r\n")) {
                break;
            }
        }

        String[] string = stringBuilder.toString().split("\r\n");
        for (String s : string) {
            String[] header = s.split(" ");
            headers.put(header[0].substring(0, header[0].length() - 1), header[1]);
        }

    }

    public String getAccept() {
        return headers.getOrDefault(ACCEPT, "text/html");
    }
}
