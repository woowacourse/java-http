package nextstep.learning.http;

import org.junit.jupiter.api.Test;

public class test {
    @Test
    void wow() {
        String uri = "/login?account=gugu&password=password";
        int index = uri.indexOf("?");
        String path = uri.substring(0, index);
        String queryString = uri.substring(index + 1);
        System.out.println(path);
        System.out.println(queryString);
    }

    @Test
    void name() {
        String uri = "/login";
        int index = uri.indexOf("?");
        System.out.println(index);
    }
}
