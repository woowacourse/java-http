package nextstep.jwp;

public class RequestMapping {

    public Controller getController(HttpRequest httpRequest) {
        String path = httpRequest.getPath();
        if ("/".equals(path)) {
            return new DefaultController();
        }
        if ("/login".equals(path)) {
            return new LoginController();
        }
        if ("/register".equals(path)) {
            return new RegisterController();
        }
        return new ViewController();
    }

}
