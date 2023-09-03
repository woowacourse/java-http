package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import static org.apache.coyote.http11.ContentType.TEXT_HTML;
import static org.apache.coyote.http11.Header.ACCEPT;
import static org.apache.coyote.http11.QueryParser.parse;

public class RequestReader {

    private static final String SPACE = " ";
    private static final String CRLF = "\r\n";
    private static final String EOF = "\r\n\r\n";
    private static final String QUESTION_MARK = "?";
    private static final String PERIOD = ".";
    private static final String BLANK = "";

    private final Map<String, String> headers = new HashMap<>();
    private final InputStream inputStream;
    private String[] requestLine;

    public RequestReader(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public String read() throws IOException {
        setRequestLine();
        setHeaders();
        return getUri();
    }

    private void setRequestLine() throws IOException {
        StringBuilder stringBuilder = new StringBuilder();

        int c;
        while ((c = inputStream.read()) != -1) {
            stringBuilder.append((char) c);
            if (stringBuilder.toString().endsWith(CRLF)) {
                break;
            }
        }
        requestLine = stringBuilder.toString().split(SPACE);
    }

    private void setHeaders() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String[] header = getHeader(bufferedReader);

        for (String headerLine : header) {
            String[] headerKV = headerLine.split(SPACE);
            headers.put(headerKV[0].substring(0, headerKV[0].length() - 1), headerKV[1]);
        }
    }

    private String[] getHeader(BufferedReader bufferedReader) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        String line;

        while (!(line = bufferedReader.readLine()).isBlank()) {
            stringBuilder.append(line).append(CRLF);
            if (stringBuilder.toString().endsWith(EOF)) {
                break;
            }
        }
        return stringBuilder.toString().split(CRLF);
    }

    public String getAccept() {
        return headers.getOrDefault(ACCEPT.getName(), TEXT_HTML.getValue());
    }

    public String getUri() {
        String path = requestLine[1];

        if (path.equals("/")) {
            return path;
        }
        if (path.contains(QUESTION_MARK)) {
            int index = path.indexOf(QUESTION_MARK);
            parse(path, index);
            path = path.substring(0, index);
        }
        if (!path.contains(PERIOD)) {
            path += ".html";
        }
        return path;
    }

    public String getProtocol() {
        return requestLine[2].replace(CRLF, BLANK);
    }
}
