package roomescape.payment.model;

import lombok.Builder;

//TODO : 클래스명 변경
@Builder
public record TossPaymentApproveInfo(
        String paymentKey,
        String orderId,
        Long amount
) {
}
