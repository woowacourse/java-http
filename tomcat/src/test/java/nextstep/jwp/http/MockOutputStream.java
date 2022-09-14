package nextstep.jwp.http;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class MockOutputStream extends OutputStream {

    private final List<Byte> cache = new ArrayList<>();

    @Override
    public void write(final int b) {
        cache.add((byte) b);
    }

    public String toString() {
        byte[] bytes = new byte[cache.size()];
        for (int i = 0; i < cache.size(); i++) {
            bytes[i] = cache.get(i);
        }
        return new String(bytes);
    }
}
