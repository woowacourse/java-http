package nextstep.jwp.util;

import java.util.Arrays;
import nextstep.jwp.http.request.HttpRequest;

public enum StaticResources {
    JS("js", "application/javascript"),
    CSS("css", "text/css"),
    HTML("html", "text/html;charset=utf-8"),
    IMAGE(".svg", "image/svg+xml"),
    FAVICON("ico", "image/x-cion");

    private final String resource;
    private final String dataType;

    StaticResources(String resource, String dataType) {
        this.resource = resource;
        this.dataType = dataType;
    }

    public static boolean matchFromHeader(HttpRequest httpRequest) {
        if (!httpRequest.isResource()) {
            return false;
        }
        String fileFormat = httpRequest.resourceType();

        return isExistedType(fileFormat);
    }

    public static boolean isExistedType(String fileFormat) {
        return Arrays.stream(StaticResources.values())
            .anyMatch(element -> element.isSameFileFormat(fileFormat));
    }

    public static StaticResources translateResourceFromHeader(HttpRequest httpRequest) {
        String fileFormat = httpRequest.resourceType();
        return Arrays.stream(StaticResources.values())
            .filter(element -> element.isSameFileFormat(fileFormat))
            .findAny()
            .orElseThrow(() ->
                new IllegalArgumentException("[ERROR] 해당하는 static 타입을 찾을 수 없습니다."));
    }

    public static StaticResources basicType() {
        return StaticResources.HTML;
    }

    public String resource() {
        return this.resource;
    }

    public String type() {
        return this.dataType;
    }

    private boolean isSameFileFormat(String fileFormat) {
        return this.resource.equals(fileFormat);
    }
}
