package org.apache.coyote.http11;

import java.util.List;

public class AcceptableRequest {

    private static final List<String> requests = List.of("GET", "POST", "DELETE");

    public static boolean isGet(String method) {
        return method.equals("GET");
    }

    public static boolean isPost(String method) {
        return method.equals("POST");
    }

    public static boolean isDelete(String method) {
        return method.equals("DELETE");
    }

}
