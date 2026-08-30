package hello.core.lifecycle;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

//public class NetworkClient implements InitializingBean, DisposableBean {
//    private String url;
//
//    public NetworkClient() {
//        System.out.println("생성자 호출, url = " + url);
//    }
//
//    public void setUrl(String url) {
//        this.url = url;
//    }
//
//    //서비스 시작시 호출
//    public void connect() {
//        System.out.println("connect: " + url);
//    }
//
//    public void call(String message) {
//        System.out.println("call: " + url + " message = " + message);
//    }
//
//    //서비스 종료시 호출
//    public void disConnect() {
//        System.out.println("close + " + url);
//    }
//
//    @Override
//    public void afterPropertiesSet() throws Exception {
//        connect();
//        call("초기화 연결 메시지");
//    }
//
//    @Override
//    public void destroy() throws Exception {
//        disConnect();
//    }
//}

//public class NetworkClient {
//    private String url;
//
//    public NetworkClient() {
//        System.out.println("생성자 호출, url = " + url);
//    }
//
//    public void setUrl(String url) {
//        this.url = url;
//    }
//
//    //서비스 시작시 호출
//    public void connect() {
//        System.out.println("connect: " + url);
//    }
//
//    public void call(String message) {
//        System.out.println("call: " + url + " message = " + message);
//    }
//
//    //서비스 종료시 호출
//    public void disConnect() {
//        System.out.println("close + " + url);
//    }
//
//    public void init() {
//        System.out.println("NetworkClient.init");
//        connect();
//        call("초기화 연결 메시지");
//    }
//
//    public void close() {
//        System.out.println("NetworkClient.close");
//        disConnect();
//    }
//}

public class NetworkClient {
    private String url;

    public NetworkClient() {
        System.out.println("생성자 호출, url = " + url);
    }

    public void setUrl(String url) {
        this.url = url;
    }

    //서비스 시작시 호출
    public void connect() {
        System.out.println("connect: " + url);
    }

    public void call(String message) {
        System.out.println("call: " + url + " message = " + message);
    }

    //서비스 종료시 호출
    public void disConnect() {
        System.out.println("close + " + url);
    }

    @PostConstruct
    public void init() {
        System.out.println("NetworkClient.init");
        connect();
        call("초기화 연결 메시지");
    }

    @PreDestroy
    public void close() {
        System.out.println("NetworkClient.close");
        disConnect();
    }
}