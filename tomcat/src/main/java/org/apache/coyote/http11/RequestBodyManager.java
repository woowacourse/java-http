package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

public class RequestBodyManager {

    List<String> body;

    public void read(BufferedReader br) throws IOException {
        String line;
        while ((line = br.readLine()) != null) {
            body.add(line);
        }
    }

    public List<String> getBody() {
        return body;
    }
}
