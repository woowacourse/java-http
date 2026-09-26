# 3단계 - 리팩터링

WAS 기능, HTTP 요청/응답 처리, 비즈니스 로직이 Http11Processor 안에 모두 존재한다.

역할과 책임에 맞게 리팩터링을 해보자

## 기능 요구 사항
### 1. HttpRequest 구현
HTTP 요청을 담당하는 HttpRequest로 분리하자

### 2. HttpResponse 구현
HTTP 응답을 처리하는 데 책임을 가진 HttpResponse 객체로 분리하자

### 3. Controller 인터페이스 추가
HTTP 요청, 응답 책임 분리를 하고 나면 비즈니스 로직 if절 분기 처리가 남는다. 

if절 분기 처리를 리팩터링해보자

```java
public interface Controller {
    void service(HttpRequest request, HttpResponse response) throws Exception;
}
```

```java
public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        // http method 분기문
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception { /* NOOP */ }
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception { /* NOOP */ }
}
```
