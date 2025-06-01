package roomescape.payment.infrastructure.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import roomescape.global.exception.ClientFailException.PaymentClientFailException;

@Component
@RequiredArgsConstructor
public class PaymentClientErrorHandler {

    private final ObjectMapper objectMapper;

    public void handleError(HttpRequest request, ClientHttpResponse response) throws IOException {
        if (response.getStatusCode().is4xxClientError()) {
            handleClientError(response);
            return;
        }

        if (response.getStatusCode().is5xxServerError()) {
            handleServerError(response);
        }
    }

    private void handleClientError(ClientHttpResponse response) throws IOException {
        String errorBody = new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8);
        PaymentErrorResponse errorResponse = objectMapper.readValue(errorBody, PaymentErrorResponse.class);
        throw new PaymentClientFailException(errorResponse.message(), response.getStatusCode().value());
    }

    private void handleServerError(ClientHttpResponse response) throws IOException {
        throw new PaymentClientFailException(
                "내부 시스템처리 작업이 실패했습니다. 잠시 후 다시 시도해주세요.",
                response.getStatusCode().value()
        );
    }
}
