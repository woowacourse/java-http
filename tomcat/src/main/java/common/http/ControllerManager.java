package common.http;

public interface ControllerManager {

    void add(String path, Controller controller);

    void service(Request request, Response response);
}
