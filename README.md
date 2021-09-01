# HTTP 서버 구현하기

### 0. 이후 개선 예정 사항 / 이슈 목록
- 세션과 쿠키를 구현한다.   
- View가 Model을 렌더링하는 로직을 수정한다.
- html 코드의 동일을 비교하는 등 view에 의존적인 테스트가 많다.
- 전체 흐름 구조 / 구현 사항을 정리한다.
- 스레드 풀을 적용한다. (스레드 풀의 기본 스레드 개수 고민)
- Exception Handler 를 구현한다.
- ResponseEntity를 구현하고 Dispatcher - ViewResolver 구조를 수정한다.

### 1. Http Request Message 구조

GET /test.html HTTP/1.1        // Request Line
Host: localhost:8000           // Request Headers
Connection: keep-alive
Upgrade-Insecure-Request: 1
Content-Type: text/html
Content-Length: 345

something1=123&something2=123   // Request Message Body

### 2. Http Response Message 구조

Http/1.1 200 OK                       // Status Line
Date: Thu, 20 May 2005 21:12:24 GMT   // General Headers 
Connection: close                      
Server: Apache/1.3.22                 // Response Headers
Accept-Ranges: bytes            
Content-Type: text/html               // Entity Headers
Content-Length: 170
last-Modified: Tue, 14 May 2004 10:13:35 GMT

<html><head>..</head></html>           // Message Body