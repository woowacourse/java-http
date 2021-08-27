# HTTP 서버 구현하기

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

<html>                                 // Message Body
<head>
..
</head>>
</html>