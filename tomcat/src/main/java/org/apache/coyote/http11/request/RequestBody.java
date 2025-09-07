package org.apache.coyote.http11.request;


public record RequestBody(byte[] body) {

    public static RequestBody parse(byte[] body) {
        //Todo: 리퀘스트 바디 파싱 추가 [2025-09-08 03:17:15]
        return new RequestBody(body);
    }
}
