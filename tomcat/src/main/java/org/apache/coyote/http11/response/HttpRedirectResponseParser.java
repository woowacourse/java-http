package org.apache.coyote.http11.response;

public class HttpRedirectResponseParser extends HttpResponseParser {
    public static byte[] parseToBytes(final HttpRedirectResponse httpResponse) {
        final StringBuilder response = new StringBuilder();

        responseStatus(httpResponse, response);
        cookies(httpResponse, response);
        header("Location", httpResponse.redirectUri, response);

        return response.toString().getBytes();
    }
}
