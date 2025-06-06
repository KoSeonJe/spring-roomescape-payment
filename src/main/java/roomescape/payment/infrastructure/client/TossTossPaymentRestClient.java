package roomescape.payment.infrastructure.client;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import roomescape.payment.model.TossPaymentProcessor;
import roomescape.payment.model.TossPaymentApproveInfo;

@RequiredArgsConstructor
public class TossTossPaymentRestClient implements TossPaymentProcessor {

    private final RestClient restClient;
    private final TossPaymentClientErrorHandler tossPaymentClientErrorHandler;

    @Override
    public void requestApprove(final TossPaymentApproveInfo tossPaymentApproveInfo) {
        try {
            restClient.post()
                    .uri("/v1/payments/confirm")
                    .contentType(APPLICATION_JSON)
                    .body(tossPaymentApproveInfo)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, tossPaymentClientErrorHandler::handleResponseError)
                    .toBodilessEntity();
        } catch (ResourceAccessException e) {
            tossPaymentClientErrorHandler.handleTimeoutError(e);
        }
    }
}
