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

### 기능 요구사항

* [x] Controller를 유저 정의 컨트롤러와 표준 컨트롤러로 분리
   * [x] 표준 컨트롤러는 클래스패스의 정적 파일을 읽는 컨트롤러 
* [x] 피드백 요구 사항
  * [x] HttpRequest 내부 필드를 RequestLine 및 OtherLines로 분리
  * [x] AdjustHttpStatus를 HttpStatus 내부로 변경
* [x] HttpRequestHeader 내부 RequestLine 분리
* [x] HttpResponse 또한 내부를 응답라인 및 다른응답 헤더와 응답 본문 등으로 구분  
* [x] JaCoCo 설정 추가

## 3단계 - 쿠키, 세션 구현하기

### 기능 요구사항

* [x] HttpRequest 헤더에 들어있는 Key-Value 나열의 Cookie를 파싱해 저장한다.
* [x] 로그인이 정상 처리됬을 때 응답 헤더 Set-Cookie에 JSESSIONID를 넣어준다.
  * [x] JSESSIONID에 대응되는 HttpSession을 만들어둔다.
  * [x] 해당 HttpSession에 "user" Key 및 User 인스턴스 Value를 넣어둔다.
    * [x] 요청 클라이언트별로 상태를 관리한다.
* [x] 로그인 상태에서 /login 페이지 Http GET 요청시 index.html로 리다이렉트한다.
  * [x] 로그인 요청시 요청 헤더에 JSESSION 아이디가 있는지 확인한다.
    * [x] 해당 JSESSIONID에 대응되는 User가 존재하면 로그인으로 간주하고 리다이렉트한다.
  * [x] JSESSIONID가 없거나 있더라도 User가 없다면 로그인 아닌 것으로 간주한다.
