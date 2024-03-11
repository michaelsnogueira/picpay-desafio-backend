package br.com.nogueira.picpaydesafiobackend.authorization;

import br.com.nogueira.picpaydesafiobackend.transaction.Transaction;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class AuthorizerService {

    private RestClient restClient;
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(AuthorizerService.class);

    public AuthorizerService(RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl("https://run.mocky.io/v3/5794d450-d2e2-4412-8131-73d0293ac1cc")
                .build();
    }

    public void authorize(Transaction transaction) {
        LOGGER.info("Authorizing transaction - {}", transaction.id());
        var response = restClient.get()
                .retrieve()
                .toEntity(Authorization.class);
        if (response.getStatusCode().isError() || !response.getBody().isAuthorized()) {
            throw new UnauthorizedException("Unauthorized transaction - %s - ".formatted(transaction.id()));
        }
        LOGGER.info("Transaction authorized - {}", transaction.id());
    }
}
