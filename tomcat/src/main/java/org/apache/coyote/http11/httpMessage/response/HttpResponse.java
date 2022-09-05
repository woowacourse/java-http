package org.apache.coyote.http11.httpmessage.response;

import java.util.LinkedHashMap;
import java.util.Map;
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
        Headers headers = modelAndView.getHeaders();

        Map<String, String> addHeaders = new LinkedHashMap<>();
        addHeaders.put("Content-Type", modelAndView.getContentType().getValue() + ";charset=utf-8 ");
        addHeaders.put("Content-Length", modelAndView.getView().getBytes().length + " ");

        headers.putAll(new Headers(addHeaders));

        return new HttpResponse(
                new StatusLine(Http11Version.HTTP_11_VERSION, modelAndView.getHttpStatus()),
                headers,
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
