package nextstep.jwp.presentation;

import org.apache.coyote.http11.http.Location;

public enum ResourceLocation {

    BAD_REQUEST(Location.from("/400.html")),
    UNAUTHORIZED(Location.from("/401.html")),
    NOT_FOUND(Location.from("/404.html")),
    ROOT(Location.from("/index.html"));

    private final Location location;

    ResourceLocation(final Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }
}
