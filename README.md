# HTTP 서버 구현하기

### 이후 개선 예정 사항 / 이슈 목록
- 스레드 풀로 스레드를 관리한다. (스레드 풀의 기본 스레드 개수 고민)
- ExceptionHandler 를 구현한다.
- ResponseEntity를 구현하고 Dispatcher - ViewResolver 구조를 수정한다.   

### Step2. 쿠키와 세션 구현   
- HttpRequest와 HttpResponse가 SessionStorage에 의존하지 않도록 한다.   
- 세션을 발급하고 저장한다.
  - 발급과 저장은 세션에 정보를 저장시에만 한다. 조회시에는 발급하지 않는다.   
  - 세션을 저장할 때 세션이 없으면 세션을 발급하고, HttpResponse에 Set-Cookie:JESSIONID 헤더를 추가한다.   
- 컨트롤러의 세션 처리
  - 로그인, 회원 가입이 성공하면 유저 정보를 세션에 저장한다.
  - 세션에 유저 정보가 있다면 로그인, 회원 가입 페이지를 메인 페이지로 리다이렉트한다.   
  - 세션에 유저 정보가 있다면 메인 페이지에 유저의 정보가 렌더링된다.   

### Step1. 요청 처리
- 컨테이너에 요청 처리를 위한 컴포넌트들이 인스턴스된다. (서버 설정, 요청-컨트롤러 매핑 로드, 자원 루트 경로 지정)   
- 스레드를 생성하고 대기한다. 요청시 RequestHandler가 아래 시나리오로 요청을 처리한다.    
    - HttpRequest, HttpResponse를 생성한다.      
    - HandlerMapper가 요청을 처리할 핸들러를 검색, 반환한다. (HandlerMapper : ControllerMapper, ResourceMapper)   
    - Handler가 요청을 처리한다. (Handler : LoginController, MainController, RegisterController, ResourceHandler)   
    - ModelAndView에 처리 결과를 담는다. (ModelAndView : 화면에 출력될 정보, 응답 파일 이름, HttpStatus)   
    - ViewResolver가 View 객체를 ViewContainer에서 검색한다. 존재하지 않으면 파일을 읽어 생성, 저장한다.      
    - View가 모델 정보를 받아 렌더링한다. (ViewRenderer : 응답 파일에서 모델의 키워드를 찾아 해당 위치에 값을 주입한다.)    
    - HttpResponse에 상태, 헤더, 메시지 바디를 담는다.    
    - 바이트로 변환하여 출력한다.    

### Http Request Message 구조
```
GET /test.html HTTP/1.1        // Request Line
Host: localhost:8000           // Request Headers
Connection: keep-alive
Upgrade-Insecure-Request: 1
Content-Type: text/html
Content-Length: 345

something1=123&something2=123   // Request Message Body
```

### Http Response Message 구조
```
Http/1.1 200 OK                       // Status Line
Date: Thu, 20 May 2005 21:12:24 GMT   // General Headers 
Connection: close                      
Server: Apache/1.3.22                 // Response Headers
Accept-Ranges: bytes            
Content-Type: text/html               // Entity Headers
Content-Length: 170
last-Modified: Tue, 14 May 2004 10:13:35 GMT

<html><head>..</head></html>           // Message Body
```