## 책임 분리 기준

## Web Application Server vs Web Server

공통점: Application Layer Protocol을 처리한다. (HTTP, TLS, SMTP ..)
차이점:

- Web Server는 이미 존재하는 리소스를 제공하거나 전달한다.
- WAS는 애플리케이션 코드를 실행해서 요청을 가공하여 응답한다.

WAS는 Application Layer Protocol을 처리하는 기능과 더불어, 애플리케이션 자체 로직이 수행되어야 한다.
현재 패키지가 분리된 구조로 그 책임 경계를 구분할 수 있다.
(HTTP 처리는 org.apache.coyote, 유저 로직 처리는 com.techcourse)
