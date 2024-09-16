package org.apache.coyote.http11.response;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.StringJoiner;
import org.apache.coyote.http11.helper.FileReader;

public class ResponseBody {

    private static final String EMPTY_BODY = "";

    private final FileReader fileReader;
    private String value;

    public ResponseBody() {
        fileReader = FileReader.getInstance();
    }

    public void addFile(String filePath) throws URISyntaxException, IOException {
        try {
            this.value = fileReader.readResourceFile(filePath);
        } catch (IllegalArgumentException e) {
            this.value = EMPTY_BODY;
        }
    }

    public int getLength() {
        if (value == null) {
            return EMPTY_BODY.length();
        }
        return value.getBytes().length;
    }

    public void buildHttpMessage(StringJoiner messageJoiner) {
        messageJoiner.add(value);
    }
}
