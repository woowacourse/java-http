package nextstep.jwp.http.response;

public class HeaderLocation implements ResponseHeaderable {
    private final String location;

    public HeaderLocation(String location) {
        this.location = location;
    }

    @Override
    public Boolean isEmpty() {
        return this.location == null;
    }

    @Override
    public String getHttpHeaderToString() {
        return "Location: " + location;
    }
}
