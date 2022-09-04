package org.apache.coyote.http11.httpmessage.response;

import java.util.List;
import org.apache.coyote.http11.httpmessage.request.Headers;
import org.apache.coyote.http11.httpmessage.request.Http11Version;
import org.apache.coyote.http11.view.ModelAndView;

public class HttpResponse {

    private final StatusLine statusLine;
    private final Headers headers;
    private final String responseBody;

    private HttpResponse(StatusLine statusLine, Headers headers, String responseBody) {
        this.statusLine = statusLine;
        this.headers = headers;
        this.responseBody = responseBody;
    }

    public static HttpResponse of(ModelAndView modelAndView) {
        return new HttpResponse(
                new StatusLine(Http11Version.HTTP_11_VERSION, modelAndView.getHttpStatus()),
                Headers.of(List.of("Content-Type: " + modelAndView.getContentType().getValue() + ";charset=utf-8 ",
                        "Content-Length: " + modelAndView.getView().getBytes().length + " ")),
                modelAndView.getView()
        );
    }

    @Override
    public String toString() {
        return String.join("\r\n",
                statusLine.toString(),
                headers.toString(),
                "",
                responseBody
        );
    }
}
