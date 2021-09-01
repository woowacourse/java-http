package nextstep.jwp.http.http_response;

import nextstep.jwp.exception.NotFoundContentTypeException;

import java.util.Arrays;
import java.util.function.Predicate;

public enum JwpContentType {
    HTML(uri -> uri.endsWith(".html"), "text/html"),
    CSS(uri -> uri.endsWith(".css"), "text/css"),
    JS(uri -> uri.endsWith(".js"), "application/javascript"),
    ;

    private final Predicate<String> condition;
    private final String resourceType;

    JwpContentType(Predicate<String> condition, String resourceType) {
        this.condition = condition;
        this.resourceType = resourceType;
    }

    public static String find(String resourceUri) {
        return Arrays.stream(values())
                .filter(type -> type.isSatisfied(resourceUri))
                .findAny()
                .orElseThrow(NotFoundContentTypeException::new)
                .resourceType + ";charset=utf-8";
    }

    private boolean isSatisfied(String resourceFile) {
        return this.condition.test(resourceFile);
    }

    public String getResourceType() {
        return resourceType;
    }
}
