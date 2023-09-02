package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class InputMapper {
    private final List<String> header;

    public InputMapper(List<String> header) {
        this.header = header;
    }

    public static InputMapper of(BufferedReader bufferedReader) throws IOException {
            String line = bufferedReader.readLine();
            valid(line);
            final List<String> lines = new ArrayList<>();
            while(!"".equals(line)){
                lines.add(line);
                line = bufferedReader.readLine();
            }
            return new InputMapper(lines);
    }

    private static void valid(String line){
        if(line == null){
            throw new UncheckedIOException(new IOException());
        }
    }

    public String getResponseBody(){
        final String url = header.get(0).split(" ")[1];
        if(url.equals("/")){
            return "Hello world!";
        }
        if(url.equals("/favicon.ico")){
            return "";
        }
        try{
            final var fileUrl = getClass().getClassLoader().getResource("static/"+url);
            final var fileBytes = Files.readAllBytes(new File(fileUrl.getFile()).toPath());
            return new String(fileBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
