package org.apache.coyote.http11.message.response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.coyote.http11.StaticFile;
import org.apache.coyote.http11.message.ContentType;
import org.apache.coyote.http11.message.HttpStatus;
import org.apache.coyote.http11.message.request.RequestURI;

public class Response {
    public static final Response DEFAULT = create();
    private String httpVersion;
    private HttpStatus httpStatus;
    private ResponseHeaders headers;
    private ResponseBody responseBody;

    public Response(
            final String httpVersion,
            final HttpStatus httpStatus,
            final ResponseHeaders headers,
            final ResponseBody responseBody
    ) {
        this.httpVersion = httpVersion;
        this.httpStatus = httpStatus;
        this.headers = headers;
        this.responseBody = responseBody;
    }

    private static Response create() {
        String httpVersion = "HTTP/1.1";
        HttpStatus httpStatus = HttpStatus.OK;
        ResponseHeaders responseHeaders = new ResponseHeaders();
        ResponseBody responseBody = ResponseBody.DEFAULT;
        return new Response(httpVersion, httpStatus, responseHeaders, responseBody);
    }

    public static Response createByTemplate(
            final HttpStatus httpStatus,
            final String templatePath,
            final Map<String, String> headers
    ) {
        final ResponseHeaders responseHeaders = new ResponseHeaders();
        final ContentType contentType = ContentType.findByFileName(templatePath);
        final ResponseBody responseBody = getTemplateBody(templatePath);
        responseHeaders.add("Content-Type", contentType.getHeaderValue() + ";charset=utf-8");
        responseHeaders.add("Content-Length", responseBody.getContentLength());
        responseHeaders.addAll(headers);
        return new Response("HTTP/1.1", httpStatus, responseHeaders, responseBody);
    }

    private static ResponseBody getTemplateBody(final String templatePath) {
        final StaticFile staticFile = new StaticFile(templatePath);
        return new ResponseBody(staticFile.getBody(), ContentType.findByExtension(staticFile.getExtension()));
    }

    public static Response createByTemplate(final HttpStatus httpStatus, final String templatePath) {
        return createByTemplate(httpStatus, templatePath, new HashMap<>());
    }

    public static Response createByTemplate(final RequestURI requestURI) {
        final String templatePath = requestURI.getPath();
        final ResponseBody responseBody = getTemplateBody(templatePath);
        final ResponseHeaders responseHeaders = new ResponseHeaders();
        final ContentType contentType = ContentType.findByFileName(requestURI.getPath());
        responseHeaders.add("Content-Type", contentType.getHeaderValue() + ";charset=utf-8");
        responseHeaders.add("Content-Length", responseBody.getContentLength());
        return new Response("HTTP/1.1", HttpStatus.OK, responseHeaders, responseBody);
    }

    public static Response createByResponseBody(final HttpStatus httpStatus, final ResponseBody responseBody) {
        final ResponseHeaders responseHeaders = new ResponseHeaders();
        responseHeaders.add("Content-Type", "text/html;charset=utf-8");
        responseHeaders.add("Content-Length", responseBody.getContentLength());
        return new Response("HTTP/1.1", httpStatus, responseHeaders, responseBody);
    }

    public void location(final String url) {
        this.headers.add("Location", url);
    }

    public void status(final HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public void addHeader(final String key, final String value) {
        this.headers.add(key, value);
    }

    public void setBy(final Response response) {
        this.httpVersion = response.httpVersion;
        this.httpStatus = response.httpStatus;
        this.headers = response.headers;
        this.responseBody = response.responseBody;
    }

    public String getResponse() {
        final List<String> responseData = new ArrayList<>();
        final String responseLine = httpVersion + " " + httpStatus.getCode() + " " + httpStatus.getMessage() + " ";
        responseData.add(responseLine);

        final List<String> responseHeaderLines = headers.getHeaderLines();
        responseData.addAll(responseHeaderLines);
        responseData.add("");

        responseData.add(responseBody.getBody());
        return String.join("\r\n", responseData);
    }
}
