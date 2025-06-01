package roomescape.payment.infrastructure.client;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;
import roomescape.global.exception.ClientFailException.PaymentClientFailException;
import roomescape.payment.model.PaymentClient;
import roomescape.reservation.model.vo.PaymentInfo;

@RequiredArgsConstructor
public class PaymentRestClient implements PaymentClient {

    private final RestClient restClient;
    private final PaymentClientErrorHandler paymentClientErrorHandler;

    @Override
    public void requestApprove(final PaymentInfo paymentInfo) {
        restClient.post()
                .uri("/v1/payments/confirm")
                .contentType(APPLICATION_JSON)
                .body(paymentInfo)
                .retrieve()
                .onStatus(HttpStatusCode::isError, paymentClientErrorHandler::handleError)
                .toBodilessEntity();
    }
}
