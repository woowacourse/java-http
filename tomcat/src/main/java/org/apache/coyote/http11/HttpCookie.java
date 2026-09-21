package org.apache.coyote.http11;

public class HttpCookie {

    private final String line;

    public HttpCookie(final String line) {
        this.line = line;
    }

    public boolean hasJsessionId() {
        String[] cookies = line.split(";");
        for(String cookie : cookies) {
            if (cookie.split("=")[0].trim().equals("JSESSIONID")) {
                return true;
            }
        }
        return false;
    }
}
