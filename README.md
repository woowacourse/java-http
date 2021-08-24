# HTTP 서버 구현하기

## 1단계 - HTTP 서버 구현하기

1. GET /index.html 응답하기
- BufferedReader.lines() 메소드를 사용하려 했으나, lazy한 특성 때문인지 계속 input 값을 기다리는 것으로 유추됨.
- lines를 넘겨 HttpRequest를 별도 클래스로 진행하는 방식을 사용하려 했으나, lines() 메소드의 사용이 불가해짐에 따라 우선은 주먹구구식으로 구현 (리팩토링 필요)
2. Query String 파싱
3. HTTP Status Code 302
4. POST 방식으로 회원가입
5. CSS 지원하기
