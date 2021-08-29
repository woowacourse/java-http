package nextstep.jwp.framework.http;

public interface ResponseTemplate {
    default HttpResponse template(HttpStatus status, HttpHeaders httpHeaders, String returnValue) {
        final HttpResponse.Builder builder = HttpResponse.status(status);

        while (!httpHeaders.isEmpty()) {
            builder.header(httpHeaders.poll());
        }

        return builder.body(returnValue).build();
    }
}
