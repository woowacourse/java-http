package org.apache.coyote.http11.dto;

public class Http11Request {
    private final LoginQueryDataDto loginQueryDataDto;
    private final String path;

    public Http11Request(String path) {
        this(null, path);
    }

    public Http11Request(LoginQueryDataDto loginQueryDataDto, String path) {
        this.loginQueryDataDto = loginQueryDataDto;
        this.path = path;
    }

    public boolean existsData() {
        return loginQueryDataDto != null;
    }

    public LoginQueryDataDto getLoginQueryDataDto() {
        return loginQueryDataDto;
    }

    public String getPath() {
        return path;
    }
}
