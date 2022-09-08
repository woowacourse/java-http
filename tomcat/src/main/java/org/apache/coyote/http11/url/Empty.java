//package org.apache.coyote.http11.url;
//
//import org.apache.coyote.http11.request.HttpHeaders;
//import org.apache.coyote.http11.request.HttpMethod;
//import org.apache.coyote.http11.request.HttpRequest;
//import org.apache.coyote.http11.response.HttpResponse;
//import org.apache.coyote.http11.response.HttpStatus;
//import org.apache.coyote.http11.response.ResponseHeaders;
//import org.apache.coyote.http11.response.StatusLine;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//public class Empty extends Url {
//    private static final Logger log = LoggerFactory.getLogger(Empty.class);
//
//    public Empty(final HttpRequest request) {
//        super(request);
//    }
//
//    @Override
//    public HttpResponse handle(HttpHeaders httpHeaders, String requestBody) {
//        if (HttpMethod.GET.equals(request.getHttpMethod())) {
//            log.info("path : {} ", getPath());
//            return new HttpResponse(new StatusLine(HttpStatus.OK), ResponseHeaders.create(getPath(), "Hello world!"),
//                    "Hello world!");
//        }
//        if (HttpMethod.POST.equals(request.getHttpMethod())) {
//            throw new IllegalArgumentException("EMPTY url은 POST로 요청이 들어올수 없습니다.");
//        }
//        throw new IllegalArgumentException("Empty page에 해당하는 HTTP Method가 아닙니다. : " + request.getHttpMethod());
//    }
//}
