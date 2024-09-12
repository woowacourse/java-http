package org.apache.coyote.http11.response;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.StringJoiner;
import org.apache.coyote.http11.helper.FileReader;

public class ResponseBody {

    private static final FileReader FILE_READER = new FileReader();

    private String value;

    public ResponseBody() {
    }

    public void addFile(String filePath) throws URISyntaxException, IOException {
        try {
            this.value = FILE_READER.readResourceFile(filePath);
        } catch (IllegalArgumentException e) {
            this.value = "";
        }
    }

    public int getLength() {
        if (value == null) {
            return 0;
        }
        return value.getBytes().length;
    }

    public void buildHttpMessage(StringJoiner messageJoiner) {
        messageJoiner.add(value);
    }
}
