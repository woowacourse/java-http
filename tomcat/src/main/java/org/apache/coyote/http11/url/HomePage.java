//package org.apache.coyote.http11.url;
//
//import org.apache.coyote.http11.request.HttpRequest;
//import org.apache.coyote.http11.request.HttpHeaders;
//import org.apache.coyote.http11.request.HttpMethod;
//import org.apache.coyote.http11.response.HttpResponse;
//import org.apache.coyote.http11.response.HttpStatus;
//import org.apache.coyote.http11.response.ResponseHeaders;
//import org.apache.coyote.http11.response.StatusLine;
//import org.apache.coyote.http11.utils.IOUtils;
//
//public class HomePage extends Url {
//
//    public HomePage(final HttpRequest request) {
//        super(request);
//    }
//
//    @Override
//    public HttpResponse handle(HttpHeaders httpHeaders, String requestBody) {
//        if (HttpMethod.GET.equals(request.getHttpMethod())) {
//            String resource = IOUtils.readResourceFile(getPath());
//            return new HttpResponse(new StatusLine(HttpStatus.OK), ResponseHeaders.create(getPath(), resource), resource);
//        }
//        if (HttpMethod.POST.equals(request.getHttpMethod())) {
//            throw new IllegalArgumentException("HomePage에는 POST요청이 들어올 수 없습니다.");
//        }
//        throw new IllegalArgumentException("HomePage에 해당하는 HTTP Method가 아닙니다. : " + request.getHttpMethod());
//    }
//}
