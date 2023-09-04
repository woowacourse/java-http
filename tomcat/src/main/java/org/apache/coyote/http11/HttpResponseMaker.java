package org.apache.coyote.http11;

public class HttpResponseMaker {

    public HttpResponse make(final ResponseEntity<?> responseEntity) {
        final HttpStatusCode httpStatus = responseEntity.getStatusCode();
        final String responseBody = (String) responseEntity.getResponseBody();
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Length", String.valueOf(responseBody.getBytes().length));

        return new HttpResponse(httpStatus, httpHeaders, responseBody);
    }
}
