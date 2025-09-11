package org.apache.coyote.error;

import java.util.Arrays;
import org.apache.coyote.httpResponse.StatusCode;

public enum ErrorPage {

    PAGE_401("/401.html", StatusCode.UNAUTHORIZED),
    PAGE_404("/404.html", StatusCode.NOT_FOUND),
    PAGE_405("/405.html", StatusCode.NOT_ALLOW_METHOD),
    PAGE_415("/415.html", StatusCode.NOT_SUPPORTED_MEDIA_TYPE),
    PAGE_500("/500.html", StatusCode.INTERNAL_SERVER_ERROR);

    private final String page;
    private final StatusCode statusCode;

    ErrorPage(
            final String page,
            final StatusCode statusCode
    ) {
        this.page = page;
        this.statusCode = statusCode;
    }

    public static String findErrorPage(final StatusCode statusCode) {
        return Arrays.stream(ErrorPage.values())
                .filter(errorPage -> errorPage.statusCode.equals(statusCode))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 에러 코드에 대한 에러 페이지가 없습니다."))
                .page;
    }

    public static StatusCode findStatusCode(final String path) {
        return Arrays.stream(ErrorPage.values())
                .filter(errorPage -> errorPage.page.equals(path))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 페이지에 대한 에러 코드가 없습니다."))
                .statusCode;
    }

    public String getPage() {
        return page;
    }
}
