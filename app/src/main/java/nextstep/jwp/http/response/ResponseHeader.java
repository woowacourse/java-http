package nextstep.jwp.http.response;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import nextstep.jwp.http.SupportedContentType;

public class ResponseHeader {
    private final HeaderLocation location;
    private final SupportedContentType contentType;
    private final HeaderContentLength contentLength;
    private final String setCookie;

    private ResponseHeader(ResponseHeaderBuilder responseHeaderBuilder) {
        this.location = new HeaderLocation(responseHeaderBuilder.location);
        this.contentType = responseHeaderBuilder.contentType;
        this.contentLength = new HeaderContentLength(responseHeaderBuilder.contentLength);
        this.setCookie = responseHeaderBuilder.cookie;
    }

    public String getResponseHeader() {
        List<String> allHeaders = getAllHeaderableHeaders().stream()
                .filter(it -> !it.isEmpty())
                .map(ResponseHeaderable::getContentType)
                .collect(Collectors.toList());
        if (setCookie != null) {
            allHeaders.add("Set-Cookie: JSESSIONID=" + setCookie);
        }
        allHeaders.add("");
        return String.join(" \r\n", allHeaders);
    }

    private List<ResponseHeaderable> getAllHeaderableHeaders() {
        return Arrays.asList(location, contentType, contentLength);
    }

    public static class ResponseHeaderBuilder {
        private final SupportedContentType contentType;
        private final int contentLength;
        private String location = null;
        private String cookie = null;

        public ResponseHeaderBuilder(SupportedContentType contentType, int contentLength) {
            this.contentType = contentType;
            this.contentLength = contentLength;
        }

        public ResponseHeaderBuilder location(String location) {
            this.location = location;
            return this;
        }

        public ResponseHeaderBuilder cookie(String cookie) {
            this.cookie = cookie;
            return this;
        }

        public ResponseHeader build() {
            return new ResponseHeader(this);
        }
    }

}
