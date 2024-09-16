package org.apache.coyote.http11.response;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import org.apache.coyote.http11.constant.HeaderKey;
import org.apache.coyote.http11.constant.MimeType;

public class ResponseHeaders {

    private static final String LINE_SEPARATOR = "\r\n";
    private static final String FORMAT_OF_DEFAULT_CONTENT_TYPE = "%s;charset=utf-8";

    private final List<ResponseHeader> headers = new ArrayList<>();

    public ResponseHeaders() {
    }

    public void addContentTypeByFileExtension(String filePath) {
        MimeType mimeType = MimeType.find(filePath);
        add(HeaderKey.CONTENT_TYPE, String.format(FORMAT_OF_DEFAULT_CONTENT_TYPE, mimeType.getType()));
    }

    public void addContentLength(int value) {
        add(HeaderKey.CONTENT_LENGTH, String.valueOf(value));
    }

    public void addLocation(String value) {
        add(HeaderKey.LOCATION, value);
    } // TODO: 테스트 방식 고민해보기

    private void add(HeaderKey key, String value) {
        headers.add(new ResponseHeader(key, value));
    }

    public void buildHttpMessage(StringJoiner messageJoiner) {
        messageJoiner.add(headers.stream()
                .map(ResponseHeader::buildHttpMessage)
                .collect(Collectors.joining(LINE_SEPARATOR)));
    }
}
