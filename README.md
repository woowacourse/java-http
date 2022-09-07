# 🐱 톰캣 구현하기 3단계 - 리팩터링

## 🚀 미션 설명

HTTP 서버를 구현한 코드의 복잡도가 높아졌다.

적절한 클래스를 추가하고 역할을 맡겨서 코드 복잡도를 낮춰보자.

## ⚙️ 기능 요구 사항

### 1. HttpRequest 클래스 구현하기

HTTP 요청을 처리하는 클래스를 추가한다.

HTTP 요청은 어떤 형태로 구성되어 있는가?

클래스로 HTTP 요청을 어떻게 구성하면 좋을까?

HTTP 요청 이미지를 참고해서 구현해보자

### 2. HttpResponse 클래스 구현하기

HTTP 응답을 처리하는 클래스를 추가한다.

HTTP 응답은 어떤 형태로 구성되어 있는가?

클라이언트에게 어떤 형태로 HTTP를 응답하면 좋을까?

### 3. Controller 인터페이스 추가하기

HTTP 요청, 응답을 다른 객체에게 역할을 맡기고 나니까 uri 경로에 따른 if절 분기 처리가 남는다.
if절 분기는 어떻게 리팩터링하는게 좋을까?
컨트롤러 인터페이스를 추가하고 각 분기에 있는 로직마다 AbstractController를 상속한 구현체로 만들어보자.



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

## 🖊 체크리스트

- [x] HTTP Request, HTTP Response 클래스로 나눠서 구현했다.
- [ ] Controller 인터페이스와 RequestMapping 클래스를 활용하여 if절을 제거했다.

## 🖥 기능 목록

- [x] HttpRequest 클래스 구현하기
- [x] HttpResponse 클래스 구현하기
- [ ] Controller 인터페이스 추가하기

## 🔥 리팩토링 목록

### ⚽️ ResponseEntity 실제 구조처럼 만들기

- [ ] builder 패턴 사용해 status Code 별 분리하기
- [ ] 각 상태코드 별 필요 헤더 추가할 수 있게 세팅하기
- [ ] 바디 값을 따로 넣을 수 있게 세팅하기
- [ ] 302 location 헤더 추가할 수 있도록 변경!!!

### 📝 피드백

#### 1차 피드백

