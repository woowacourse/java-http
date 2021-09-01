## 1단계 - HTTP 서버 구현하기

### 기능 요구사항

* [x] '/'로 접속하면 index.html 페이지를 보여준다.
* [x] '/lgoin'로 접속하면 login.html 페이지를 보여준다.
* [x] '/register'로 접속하면 login.html 페이지를 보여준다.
* [x] 그 외 .html, .css, .js로 GET 요청이 오면 정적 파일 응답을 보내준다.
* [x] /login, /register로 Body와 함께 요청을 보내면 그에 맞는 비즈니스 로직을 수행한다.
  * [x] 성공하면 메인 페이지로 302 Redirect한다.
  * [x] 실패하면 401 예외 페이지로 이동한다.
* [x] 그 외의 경로로 들어온 요청들은 전부 404 페이지를 응답한다.
* [x] 서버 예외가 발생하면 500 페이지를 응답한다.

## 2단계 - 리팩토링

* [] Controller를 유저 정의 컨트롤러와 표준 컨트롤러로 분리
   * [] 표준 컨트롤러는 클래스패스의 정적 파일을 읽는 컨트롤러 
* [] 피드백 요구 사항
  * [] HttpHeader 내 복잡한 파싱 기능 유틸 클래스 등으로 분리
  * [] AdjustHttpStatus를 HttpStatus 내부로 변경
* [] HttpRequestHeader 내부 RequestLine 분리
* [] JaCoCo 설정 추가
