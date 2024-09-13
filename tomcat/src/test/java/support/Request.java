package support;

import java.util.List;

public class Request {
    public static final List<String> validGetRequest = List.of(
            "GET / HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive "
    );
    public static final List<String> invalidGetRequest = List.of(
            "GETO /// HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive "
    );
}
