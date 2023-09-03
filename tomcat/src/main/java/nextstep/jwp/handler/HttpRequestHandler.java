package nextstep.jwp.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestHandler {
    public static void handleHttpRequest(Socket clientSocket) throws IOException {
        // 클라이언트 소켓으로부터 입력 스트림을 얻음
        InputStream inputStream = clientSocket.getInputStream();

        // 입력 스트림을 문자 스트림으로 변환하여 읽기
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        // Content-Length 헤더에서 본문의 길이 파악
        String contentLengthHeader = reader.readLine();
        int contentLength = Integer.parseInt(contentLengthHeader.substring("Content-Length: ".length()));

        // 본문을 읽어오기 위한 버퍼 생성
        char[] buffer = new char[contentLength];

        // 본문을 버퍼에 읽어옴
        reader.read(buffer, 0, contentLength);

        // 버퍼에서 문자열로 변환하여 반환
        System.out.println(new String(buffer));

    }
}
