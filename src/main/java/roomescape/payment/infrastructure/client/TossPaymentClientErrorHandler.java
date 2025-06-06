package roomescape.payment.infrastructure.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import roomescape.global.exception.ClientFailException.PaymentClientFailException;
import roomescape.global.exception.ClientTimeoutException.PaymentConnectionTimeoutException;
import roomescape.global.exception.ClientTimeoutException.PaymentReadTimeoutException;
import roomescape.global.exception.ClientTimeoutException.PaymentTimeoutException;

@Component
@RequiredArgsConstructor
public class TossPaymentClientErrorHandler {

    private final ObjectMapper objectMapper;

    //TODO : 어떤 결제 API를 수행하다 타임아웃 발생했는지, 로그로 남기기
    public void handleTimeoutError(ResourceAccessException e) {
        Throwable cause = e.getCause();

        if (cause instanceof ConnectException) {
            throw new PaymentConnectionTimeoutException(cause);
        }

        if (cause instanceof SocketTimeoutException) {
            throw new PaymentReadTimeoutException(cause);
        }

        throw new PaymentTimeoutException(cause);
    }

    public void handleResponseError(HttpRequest request, ClientHttpResponse response) throws IOException {
        if (response.getStatusCode().is4xxClientError()) {
            handleClientError(response);
            return;
        }

        if (response.getStatusCode().is5xxServerError()) {
            handleServerError(response);
        }
    }

    private void handleClientError(ClientHttpResponse response) throws IOException {
        TossPaymentErrorResponse errorResponse = objectMapper.readValue(response.getBody(), TossPaymentErrorResponse.class);
        throw new PaymentClientFailException(errorResponse.message(), response.getStatusCode().value());
    }

    private void handleServerError(ClientHttpResponse response) throws IOException {
        throw new PaymentClientFailException(
                "내부 시스템처리 작업이 실패했습니다. 잠시 후 다시 시도해주세요.",
                response.getStatusCode().value()
        );
    }
}
