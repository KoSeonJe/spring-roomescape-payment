package roomescape.payment.infrastructure.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import roomescape.global.exception.ClientFailException.PaymentClientFailException;

@ExtendWith(MockitoExtension.class)
class TossPaymentClientErrorHandlerTest {

    @Mock
    private HttpRequest httpRequest;

    @Mock
    private ClientHttpResponse clientHttpResponse;

    private TossPaymentClientErrorHandler tossPaymentClientErrorHandler;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        tossPaymentClientErrorHandler = new TossPaymentClientErrorHandler(objectMapper);
    }

    @Test
    @DisplayName("4xx 클라이언트 에러를 처리한다")
    void handleError_ClientResponseError() throws IOException {
        // given
        String errorJson = """
                {
                    "message": "잘못된 결제 키입니다",
                    "code": "INVALID_PAYMENT_KEY"
                }
                """;
        InputStream inputStream = new ByteArrayInputStream(errorJson.getBytes(StandardCharsets.UTF_8));

        when(clientHttpResponse.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);
        when(clientHttpResponse.getBody()).thenReturn(inputStream);

        // when & then
        PaymentClientFailException exception = assertThrows(
                PaymentClientFailException.class,
                () -> tossPaymentClientErrorHandler.handleResponseError(httpRequest, clientHttpResponse)
        );

        assertThat(exception.getMessage()).isEqualTo("잘못된 결제 키입니다");
        assertThat(exception.getStatusCode()).isEqualTo(400);
    }

    @Test
    @DisplayName("5xx 서버 에러를 처리한다")
    void handleError_ServerResponseError() throws IOException {
        // given
        when(clientHttpResponse.getStatusCode()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);

        // when & then
        PaymentClientFailException exception = assertThrows(
                PaymentClientFailException.class,
                () -> tossPaymentClientErrorHandler.handleResponseError(httpRequest, clientHttpResponse)
        );

        assertThat(exception.getMessage()).isEqualTo("내부 시스템처리 작업이 실패했습니다. 잠시 후 다시 시도해주세요.");
        assertThat(exception.getStatusCode()).isEqualTo(500);
    }

    @Test
    @DisplayName("잘못된 JSON 형식의 4xx 에러 응답 시 IOException이 발생한다")
    void handleResponseError_InvalidJsonFormat() throws IOException {
        // given
        String invalidJson = "{ invalid json }";
        InputStream inputStream = new ByteArrayInputStream(invalidJson.getBytes(StandardCharsets.UTF_8));

        when(clientHttpResponse.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);
        when(clientHttpResponse.getBody()).thenReturn(inputStream);

        // when & then
        assertThrows(
                IOException.class,
                () -> tossPaymentClientErrorHandler.handleResponseError(httpRequest, clientHttpResponse)
        );
    }
}
