package br.com.nogueira.picpaydesafiobackend.notification;

public class NotificationException extends RuntimeException {
    public NotificationException(String formatted) {
        super(formatted);
    }
}
