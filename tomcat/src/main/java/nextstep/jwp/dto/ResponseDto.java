package nextstep.jwp.dto;

public class ResponseDto {

    private final String code;
    private final String location;

    public ResponseDto(String code, String location) {
        this.code = code;
        this.location = location;
    }

    public String code() {
        return code;
    }

    public String location() {
        return location;
    }
}
