package nextstep.jwp.webserver;

public interface Controller {

    void service(HttpRequest request, HttpResponse response) throws Exception;
}
