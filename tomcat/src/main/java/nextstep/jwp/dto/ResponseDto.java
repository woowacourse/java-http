package nextstep.jwp.dto;

public class ResponseDto {

    private final String code;
    private final String location;
    private final String id;

    public ResponseDto(String code, String location, String id) {
        this.code = code;
        this.location = location;
        this.id = id;
    }

    public String code() {
        return code;
    }

    public String location() {
        return location;
    }

    public String id() {
        return id;
    }
}
