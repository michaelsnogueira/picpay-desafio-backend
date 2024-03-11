package br.com.nogueira.picpaydesafiobackend.notification;

import br.com.nogueira.picpaydesafiobackend.transaction.Transaction;
import org.slf4j.Logger;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class NotificationConsumer {

    private RestClient restClient;
    private static Logger LOGGER = org.slf4j.LoggerFactory.getLogger(NotificationConsumer.class);

    public NotificationConsumer(RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl("https://run.mocky.io/v3/54dc2cf1-3add-45b5-b5a9-6bf7e7f1f4a6")
                .build();
    }
    @KafkaListener(topics = "transaction-notification", groupId = "picpay-desafio-backend")
    public void recieveNotification(Transaction transaction) {
        LOGGER.info("Sending notification - {}", transaction.id());
        var response = restClient.get()
                .retrieve()
                .toEntity(Notification.class);
        if (response.getStatusCode().isError() || !response.getBody().message()) {
            throw new NotificationException("Notification failed - %s - ".formatted(transaction.id()));
        }
        LOGGER.info("Notification sent - {}", transaction.id());
    }
}
