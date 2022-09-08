//package org.apache.coyote.http11.url;
//
//import nextstep.jwp.db.InMemoryUserRepository;
//import nextstep.jwp.model.User;
//import org.apache.coyote.http11.request.HttpRequest;
//import org.apache.coyote.http11.request.HttpDataRequest;
//import org.apache.coyote.http11.request.HttpHeaders;
//import org.apache.coyote.http11.request.HttpMethod;
//import org.apache.coyote.http11.response.HttpResponse;
//import org.apache.coyote.http11.response.HttpStatus;
//import org.apache.coyote.http11.response.ResponseHeaders;
//import org.apache.coyote.http11.response.StatusLine;
//import org.apache.coyote.http11.utils.IOUtils;
//
//public class Register extends Url {
//    public Register(HttpRequest request) {
//        super(request);
//    }
//
//    @Override
//    public HttpResponse handle(HttpHeaders httpHeaders, String requestBody) {
//        if (HttpMethod.GET.equals(request.getHttpMethod())) {
//            String resource = IOUtils.readResourceFile(getPath());
//            return new HttpResponse(new StatusLine(HttpStatus.OK), ResponseHeaders.create(getPath(), resource), resource);
//        }
//
//        throw new IllegalArgumentException("Register에 해당하는 HTTP Method가 아닙니다. : " + request.getHttpMethod());
//    }
//}
