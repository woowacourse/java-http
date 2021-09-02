# HTTP 서버 구현하기

## 미션 개요
### 내용
- HTTP 서버를 구현한다. 
- 소켓에서 HTTP 요청을 파싱하여 프로토콜 내용에 맞게 처리힌다.
- 소켓에 응답 내용을 적어서 반환한다. 

### 목적 
- 프레임워크에서 처리해주는 HTTP 요청/응답의 동작을 직접 구현하므로 동작 원리를 이해한다.

## 요구사항 

### HTTP 요청/응답 메세지 처리
- HTTP Request
    - [x] Socket InputStream에서 내용 읽기
    - [x] HTTP 요청 구조에 맞게 `Start Line`, `Header`, `Body`를 나눠서 파싱한다. [참고링크](https://developer.mozilla.org/en-US/docs/Web/HTTP/Messages)
        - [x] `Start Line` - `Http Method` `URL`, `Http Version`으로 나눈다.
          - [x] URL에 QueryString은 Map으로 관리한다. 
        - [x] `Headers` - Map으로 관리하며 헤더 값은 multi-value인 것을 고려하여 리스트로 관리한다.
        - [x] `Body` - Map으로 attribute를 관리한다.
            - [x] 헤더에 Content-Type 만큼의 Body 내용을 읽는다. 
            - [x] 기본값으로 빈 스트링으로 지정한다. 
    

- HTTP Response
    - [x] HTTP 응답 구조에 맞게 저장한 값을 assemble 하여 String으로 리턴한다. 
        - [x] `Status Line` - 응답 코드, 응답 메세지, 프로토콜 버전 
            - [x] 응답 코드 - ENUM으로 응답 메세지와 함께 관리힌다.
        - [x] `Header` - 응답 Content Type을 지정하여 보낸다. 
        - [x] `Body` - 기본값은 빈 스트링으로 지정한다. 

<br>

### 요청 Handler 매핑 및 처리
- Handler Mapping
    - [x] Controller들을 관리하는 Application Context를 구현한다. 
    - [x] 요청 target resource가 uri인지 정적 파일인지 구분하여 적합한 handler를 지정한다. 
        - [x] url이라면 맞는 Controller를 매핑한다.
        - [x] 정적 파일이라면 정적 파일 처리 handler를 매핑한다. 
    - [x] 예외 상황을 처리하는 Exception Handler를 구현한다. 


- Handler
    - 정적 파일 핸들러 
        - [x] 정적파일을 읽고 내용을 String으로 변환한다.  
        - [x] 예외 상황 : 요청하는 정적 파일이 없을 경우 예외 발생한다. 
    - Url 요청 핸들러 - Controller 매핑
        - [x] 컨트롤러에서 Http Method를 분석하여 `doGet` 또는 `doPost`로 요청을 처리한다.
        - [x] 인자로 request, response를 받아서 응답을 작성한다. 
    - Exception Handler
        - [x] 발생한 예외코드를 분석하여 해당하는 예외 페이지로 redirect 한다. 
    
### 요구 기능
- 루트 페이지 
    - [x] `/` 요청 시 `Hello World!`를 보여준다.
  
- index.html 페이지  
    - [x] `/index.html` GET 요청 시 대시보드 페이지를 보여준다.

- 로그인 페이지 
    - [x] `/login` GET 요청 시 `login.html`페이지를 보여준다. 
    - [x] `/login` POST 요청 시 존재하는 회원을 조회한다. 
        - [x] 조회에 성공하면 302 코드와 함께 `index.html`페이지로 리다이렉트 한다. 
        - [x] 조회 실패하면 `401.html`로 리다이렉트 한다.
    
- 회원가입 페이지 
    - [x] `/register` GET 요청 시 `register.html`페이지를 보여준다.
    - [x] `/register` POST 요청 시 회원가입을 한다. 
        - [x] 가입에 성공하면 302 코드와 함께 `index.html`페이지로 리다이렉트 한다.
        - [x] 가입 실패하면 예외 발생 + `500.html`로 리다이렉트 한다. (본래 400이 적합하지만 에러 페이지가 없으므로 500 반환)
    
- 다양한 컨텐츠 지원
    - [x] `.css` 등과 같은 정적파일에 알맞은 `Content-Type` 지정 로직을 구현한다. 

<br>

### 쿠키, 세션 구현 
- 쿠키
    - [x] HttpRequest에 포함되는 HttpCookie 구현
- 세션
    - [x] 클라이언트 세션들을 관리하는 HttpSessions 구현 
    - [x] 세션과 해당 세션에 저장된 속성들을 관리하는 HttpSession 구현
- 동작 기능 
    - [x] 클라이언트의 요청(현재는 로그인 요청시)에 `Cookie ` 필드에 세션 값이 없다면 
        - [x] `Set-Cookie`에 `JSESSIONID={}` 값을 추가해서 응답한다.
    - [x] 로그인 요청 시 세션 아이디가 있다면 로그인 된 상태이므로 바로 `index.html`로 리다이렉트 한다. 


## 추후 리팩토링
- [x] BufferReader, InputStreamReader `close()`
- [x] 도메인 ID 관리 로직 변경 
- [x] Request/Response에 헤더나 바디값이 없는 경우 기본값 지정 및 호출 시 예외처리 
- [x] 예외 발생 시점에 로그
- [ ] Header enum으로 관리
- [ ] ViewResolver 구현
- [ ] 요청 메세지 파싱 로직 개션
- [ ] 세션 만료기간 설정 로직 구현
- [ ] 컬렉션에서 Optional 제거 


<br>

## 학습내용
- 자바 I/O stream & File 
    - [링크](https://prolog.techcourse.co.kr/posts/1624)
