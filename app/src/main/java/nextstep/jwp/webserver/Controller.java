package nextstep.jwp.webserver;

public interface Controller {

    HttpResponse handle(HttpRequest request);
}
