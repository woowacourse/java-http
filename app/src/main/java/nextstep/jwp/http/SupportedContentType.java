package nextstep.jwp.http;

import java.util.Arrays;
import java.util.Optional;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.ResponseHeaderable;

public enum SupportedContentType implements ResponseHeaderable {
    HTML("Content-Type: text/html;charset=utf-8", ".html"),
    CSS("Content-Type: text/css;charset=utf-8", ".css"),
    JS("Content-Type: application/js;charset=utf-8", ".js"),
    NOTFOUND("NotFound", "NotFound");

    private final String contentType;
    private final String extention;

    SupportedContentType(String contentType, String extention) {
        this.contentType = contentType;
        this.extention = extention;
    }

    public static SupportedContentType extractContentTypeFromRequest(HttpRequest request) {
        String uri = request.getUri();

        Optional<SupportedContentType> matchedContentType = Arrays.stream(SupportedContentType.values())
                .filter(it -> {
                    return uri.endsWith(it.extention);
                }).findFirst();

        return matchedContentType.orElse(SupportedContentType.NOTFOUND);
    }

    @Override
    public Boolean isEmpty() {
        return this.contentType == null;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    public String getExtention() {
        return extention;
    }
}
