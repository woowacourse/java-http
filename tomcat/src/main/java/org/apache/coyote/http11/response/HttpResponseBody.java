package org.apache.coyote.http11.response;

class HttpResponseBody {

    private String body;

    HttpResponseBody() {
    }

    void addBody(String body) {
        this.body = body;
    }

    void validateLength(long length) {
        if (length != body.getBytes().length) {
            throw new IllegalArgumentException("Response Body의 길이가 유효하지 않습니다.");
        }
    }

    boolean exist() {
        return body != null;
    }

    public String getMessage() {
        return body;
    }
}
