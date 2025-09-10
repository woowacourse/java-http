package org.apache.coyote.error;

import java.io.IOException;
import org.apache.coyote.controller.error.ErrorPageController;
import org.apache.coyote.httpRequest.HttpRequest;
import org.apache.coyote.httpResponse.HttpResponse;
import org.apache.coyote.httpResponse.StatusCode;

public class ErrorHandler {

    private static ErrorPageController errorPageController = new ErrorPageController();

    public static void handleError(
            final HttpException httpException,
            final HttpResponse httpResponse
    ) throws IOException {
        final StatusCode statusCode = httpException.getStatusCode();
        final String errorPage = ErrorPage.findErrorPage(statusCode);
        final HttpRequest httpErrorRequest = HttpRequest.createErrorRequest(errorPage);
        errorPageController.service(httpErrorRequest, httpResponse);
    }

    public static void handleServerError(
            final HttpResponse httpResponse
    ) throws IOException {
        HttpRequest httpRequest = HttpRequest.createErrorRequest(ErrorPage.PAGE_500.getPage());
        errorPageController.service(httpRequest, httpResponse);
    }
}
