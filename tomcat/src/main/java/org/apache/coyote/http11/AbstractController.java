package org.apache.coyote.http11;

public abstract class AbstractController implements Controller{

    @Override
    public void service(final HttpRequest request,
                        final HttpResponse response) throws Exception{
        if("GET".equals(request.getMethod())){
            doGet(request, response);
            return;
        }

        if ("POST".equals(request.getMethod())) {
            doPost(request, response);
        }
    }

    protected void doGet(final HttpRequest request,
                         final HttpResponse response) throws Exception {
        // 필요한 Controller에서 재정의
    }

    protected void doPost(final HttpRequest request,
                          final HttpResponse response) throws Exception {
        // 필요한 Controller에서 재정의
    }
}
