package org.apache.coyote.http11.response;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class ResponseHeaders {

    private static final String LINE_SEPARATOR = "\r\n";
    private static final String FORMAT_OF_DEFAULT_CONTENT_TYPE = "%s;charset=utf-8";

    private final List<ResponseHeader> headers = new ArrayList<>();

    public ResponseHeaders() {
    }

    public void addContentTypeByFileExtension(String filePath) {
        MimeType mimeType = MimeType.find(filePath);
        add("Content-Type", String.format(FORMAT_OF_DEFAULT_CONTENT_TYPE, mimeType.getType()));
    }

    public void addContentLength(int value) {
        add("Content-Length", String.valueOf(value));
    }

    public void addLocation(String value) {
        add("Location", value);
    }

    private void add(String key, String value) {
        headers.add(new ResponseHeader(key, value));
    }

    public void buildHttpMessage(StringJoiner messageJoiner) {
        messageJoiner.add(headers.stream()
                .map(ResponseHeader::buildHttpMessage)
                .collect(Collectors.joining(LINE_SEPARATOR)));
    }
}
