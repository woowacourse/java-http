package nextstep.jwp.http.response;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import nextstep.jwp.http.SupportedContentType;

public class ResponseHeader {
    private final HeaderLocation location;
    private final SupportedContentType contentType;
    private final HeaderContentLength contentLength;


    public ResponseHeader(ResponseHeaderBuilder responseHeaderBuilder) {

        this.location = new HeaderLocation(responseHeaderBuilder.location);
        this.contentType = responseHeaderBuilder.contentType;
        this.contentLength = new HeaderContentLength(responseHeaderBuilder.contentLength);
    }

    public String getResponseHeader() {
        List<String> allHeaders = getAllHeaders().stream()
                .filter(it -> !it.isEmpty())
                .map(ResponseHeaderable::getHttpHeaderToString)
                .collect(Collectors.toList());
        allHeaders.add("");
        return String.join(" \r\n", allHeaders);
    }

    private List<ResponseHeaderable> getAllHeaders() {
        return Arrays.asList(location, contentType, contentLength);
    }

    public static class ResponseHeaderBuilder {
        private final SupportedContentType contentType;
        private final int contentLength;
        private String location = null;

        public ResponseHeaderBuilder(SupportedContentType contentType, int contentLength) {
            this.contentType = contentType;
            this.contentLength = contentLength;
        }

        public ResponseHeaderBuilder location(String location) {
            this.location = location;
            return this;
        }

        public ResponseHeader build() {
            return new ResponseHeader(this);
        }
    }

}
