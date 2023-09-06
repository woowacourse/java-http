package org.apache.catalina.servlet;

import nextstep.jwp.exception.UncheckedServletException;
import org.apache.catalina.util.ResourceFileReader;
import org.apache.coyote.http.HttpHeader;
import org.apache.coyote.http.HttpMethod;
import org.apache.coyote.http.HttpStatus;
import org.apache.coyote.http.SupportFile;
import org.apache.coyote.http.vo.HttpHeaders;
import org.apache.coyote.http.vo.HttpRequest;
import org.apache.coyote.http.vo.HttpResponse;
import org.apache.coyote.http.vo.Url;

public class DefaultServlet implements HttpServlet {

    @Override
    public HttpResponse service(final HttpRequest httpRequest) {
        try {
            final Url url = httpRequest.getUrl();
            final HttpHeaders headers = HttpHeaders.getEmptyHeaders();
            final String body = ResourceFileReader.readFile(url.getUrlPath());

            headers.put(HttpHeader.CONTENT_TYPE, SupportFile.getContentType(httpRequest));

            return new HttpResponse.Builder()
                    .status(HttpStatus.OK)
                    .headers(headers)
                    .body(body)
                    .build();

        } catch (UncheckedServletException e) {
            final HttpHeaders headers = HttpHeaders.getEmptyHeaders();
            headers.put(HttpHeader.LOCATION, "/404.html");

            return new HttpResponse.Builder()
                    .status(HttpStatus.REDIRECT)
                    .headers(headers)
                    .build();
        }
    }

    @Override
    public boolean isSupported(final HttpRequest httpRequest) {
        return httpRequest.isRequestMethodOf(HttpMethod.GET) &&
                SupportFile.isSupportFileExtension(httpRequest);
    }
}
