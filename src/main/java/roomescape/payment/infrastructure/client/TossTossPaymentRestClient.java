package roomescape.payment.infrastructure.client;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import roomescape.payment.model.TossPaymentApprovalInfo;
import roomescape.payment.model.TossPaymentGateway;

@RequiredArgsConstructor
public class TossTossPaymentRestClient implements TossPaymentGateway {

    private final RestClient restClient;
    private final TossPaymentClientErrorHandler tossPaymentClientErrorHandler;

    @Override
    public void requestApprove(final TossPaymentApprovalInfo tossPaymentApprovalInfo) {
        try {
            restClient.post()
                    .uri("/v1/payments/confirm")
                    .contentType(APPLICATION_JSON)
                    .body(tossPaymentApprovalInfo)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, tossPaymentClientErrorHandler::handleResponseError)
                    .toBodilessEntity();
        } catch (ResourceAccessException e) {
            tossPaymentClientErrorHandler.handleTimeoutError(e);
        }
    }
}
