# HTTP 서버 구현하기

- [x] 서버를 실행시켜서 브라우저로 서버(`http://localhost:8080/index.html`)에 접속하면 index.html 페이지를 보여준다.
- [x] `http://localhost:8080/login`으로 접속하면 로그인 페이지(login.html)를 보여준다.
  - [x] Query String을 추가해서 로그인 페이지에 접속했을 때 아이디, 비밀번호가 일치하면 회원을 조회한다.  
    `http://localhost:8080/login?account=gugu&password=password`
- [x] 회원을 조회해서 로그인에 성공하면 /index.html로 리다이렉트한다. 
  - [x] `/login?account=gugu&password=password`로 접근해서 로그인 성공하면 응답 헤더에 http status code를 302로 반환한다. 
  - [x] 로그인에 실패하면 401.html로 리다이렉트한다.
- [x] http://localhost:8080/register으로 접속하면 회원가입 페이지(register.html)를 보여준다. 
  - [x] 회원가입 페이지를 보여줄 때는 GET을 사용한다. 
  - [x] 회원가입을 버튼을 누르면 HTTP method를 GET이 아닌 POST를 사용한다. 
  - [x] 회원가입을 완료하면 index.html로 리다이렉트한다. 
  - [x] 로그인도 버튼을 눌렀을 때 GET 방식에서 POST 방식으로 전송하도록 변경하자.
- [x] CSS 지원하기
- [x] 404.html 연결
- [x] favicon.ico 연결
- [ ] 테스트 코드 작성
  - [ ] 컨트롤러 테스트 코드
    - [x] LoginController 테스트 코드
    - [x] RegisterController 테스트 코드
    - [ ] StaticResourceController 테스트 코드
    - [ ] Controllers 테스트 코드
  - [x] HTTP Request 테스트 코드
    - [x] RequestLine 테스트 코드
      - [x] Method 테스트 코드
      - [x] RequestUri 테스트 코드
      - [x] HttpVersion 테스트 코드
    - [x] RequestHeaders 테스트 코드
    - [x] RequestBody 테스트 코드
  - [x] HTTP Response 테스트 코드
    - [x] StatusLine 테스트 코드
    - [x] ResponseHeaders 테스트 코드
    - [x] ResponseBody 테스트 코드
  - [x] User 테스트 코드
  - [x] Content, ContentType, StaticResource 테스트 코드
  - [x] Service 테스트 코드
  - [x] InMemoryUserRepository 테스트 코드
- [ ] 각 동작 로그 남기기

<br>

## 구현해볼 것
- [x] HTTP 1.1 외의 버전 요청이 올 경우 ERROR RESPONSE 반환
- [ ] 무제한 길이의 URI를 처리할 수 있어야만 한다.
    - [ ] URI의 길이가 처리할 수 있는 것보다 긴 경우 414 (Request-URI Too Long)를 응답한다.
- [ ] URL에 abs_path가 명시되어 있지 않으면 자원(5.1.2 절)을 위한 Request-URI로서 사용할 때 반드시 "/"가 주어져야 한다.
    - [x] 비어 있는 abs_path는 "/"인 abs_path와 동일하다.
- [ ] 비어 있거나 명시되지 않은 포트는 기본 포트 80번으로 정의한다
- [ ] 호스트 이름의 비교에는 반드시 대소문자를 구별하지 않는다.
- [ ] scheme 이름의 비교는 반드시 대소문자를 구별하지 않는다.
- [ ] 서버는 Request-Line이 있어야 할 곳에 빈 라인을 수신하면 이를 무시해야 한다. 다른 말로 표현 한다면, 만약 서버가 규약 스트림을 읽는 도중 메시지 처음에 CRLF를 수신하게 되면 이를 무시해야 한다.
- [x] HTTP 메세지 필드 이름(내용)은 대소문자를 구분하지 않는다.
- [ ] General-Header 필드를 맨 처음 나오고 Request-Header이나 Response-Header 필드가 뒤를 따르고 마지막에 Entity-Header 필드가 나오는 것이 관행이다. 그러나 헤더 필드의 정렬 순서가 강제되진 않는다.
    - [ ] 순서가 아닌 헤더 이름에 의존해서 파싱을 진행한다.
- [ ] HEAD 요구 method에 대한 모든 응답은 Entity-Header 필드가 포함한 것처럼 믿게 하여도 Message-Body 를 포함해서는 절대 안 된다. 모든 1xx (Informational), 204 (No Content) 및 304 (Not Modified) 응답은 Message-Body를 절대 포함해서는 안 된다. 
    - [ ] Entity-Header 필드의 존재 유무에 관계없이 헤더 필드 다음의 첫 빈 라인으로 항상 종료된다.
    - [ ] 다른 모든 응답은 비록 길이가 제로라 할지라도 Message-Body 를 포함한다.
    - [ ] Content-Length 또는 Transfer-Encoding 헤더 유무를 통해 Request Message-Body의 존재를 파악한다.
- [x] 요구가 Message-Body를 포함하고 있고 Content-Length가 주어지지 않았으면 서버는 메시지의 길이를 결정할 수 없을 때는 400(Bad Request)을 응답으로 보내고, 계속하여 유효한 Content-Length 수신을 기다리고자 할 때는 411(Length Required) 메시지를 반송하여야 한다.

<br>

## 추후 고려해볼 것
엔터티를 수신하는 모든 HTTP/1.1 애플리케이션은 반드시 "chunked" 전송 코딩(3.6 절)을 허용해야만 한다. 이렇게 함으로서 메시지의 길이를 미리 결정할 수 없을 때 이 메커니즘이 사용될 수 있도록 한다.
메시지는 Content-Length 헤더 필드 및 "chunked" 전송 코딩을 모두 포함해서는 안 된다. 만약 둘 다를 수신하였으면 Content-Length 는 반드시 무시해야 한다.
Message-Body 가 허용된 메시지에 Content-Length 가 주어졌을 때 그 필드 값은 반드시 Message-Body 의 OCTET 숫자와 정확하게 일치해야 한다. HTTP/1.1 사용자 에이전트는 유효하지 않은 길이를 수신했거나 탐지했을 때 반드시 사용자에게 이를 알려야 한다.


Request-URI 는 보편적인 자원 식별자(3.2 절)이며 요구를 적용할 자원을 식별한다. Request-URI = "*" | absoluteURI | abs_path
Request-URI의 세 가지 선택 사항은 요구의 성격에 달려 있다. 별표 "*"는 요구를 특별한 자원에 적용하지 않고 서버 자체에 적용한다는 것을 의미하며 사용된 method가 반드시 자원에 적용되는 것은 아닐 때 사용할 수 있다.
한 예를 보면;
OPTIONS * HTTP/1.1


가장 일반적인 형태의 Request-URI는 원서버나 게이트웨이의 자원을 식별하는 데 사용한다. 이 경우 URI 의 절대적 경로는 반드시 Request-URI 처럼 전송(3.2.1 절의 abs_path 참조)되어야 하며 URI 의 네트워크 위치는 반드시 Host 헤더 필드를 이용하여 전송되어야 한다. 예를 들어 원서버에서 직접 자원을 조회하고자 하는 클라이언트는 "www.w3.org" 호스트의 포트 80 으로 TCP 접속을 한 다음 아래의 라인을 전송할 것이다.
GET /pub/WWW/TheProject.html HTTP/1.1 Host: www.w3.org
위 내용 다음에 요구 메시지의 나머지 부분이 뒤따른다. 절대 경로는 절대 비어서는 안 된다. 원래 URI의 절대 경로가 비어있을 때에는 반드시 "/" (서버의 루트 디렉토리)를 추가한다.
프락시가 Request-URI에 아무런 경로가 없는 요구를 수신하고 명시된 method가 별표 모양의 요구를 지원할 수 있으면 응답 메시지의 전달 경로 상의 (Request chain) 마지막 프락시는 반드시 요구 메시지에 마지막 Request-URI 로서 "*"를 첨부하여 전송해야 한다.



1. Request-URI 가 absoluteURI이면 호스트는 Request-URI의 일부분이다. 요구의 어떠한 Host 헤더 필드 값도 반드시 무시해야 한다.
2. Request-URI 가 absoluteURI가 아니면 요구는 Host 헤더 필드를 포함한다. 호스트는 Host 헤더 필드 값으로 결정된다.
3. 규칙 1, 2에 의하여 지정된 호스트가 서버의 유효한 호스트가 아니면 응답은 반드시 400(Bad Request) 에러 메시지이어야 한다.

<br>

### 파일 내용을 먼저 읽어서 StaticResource 객체로 관리하는게 나을지?
아니면 파일 경로만 기억하고 있다가 Response로 돌려줄 때 파일 내용을 읽는게 나을지?

### 테스트 코드가 resources/static 에 의존하고 있다.
IO 테스트 위해선 어쩔 수 없을지?

