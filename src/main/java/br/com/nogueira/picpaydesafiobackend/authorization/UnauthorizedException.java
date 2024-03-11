package br.com.nogueira.picpaydesafiobackend.authorization;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
