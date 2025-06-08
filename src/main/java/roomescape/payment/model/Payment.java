package roomescape.payment.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String orderId;

    @Column(unique = true)
    private String paymentKey;

    private int amount;

    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private Long reservationId;

    @Builder
    private Payment(String orderId, String paymentKey, int amount, PaymentType paymentType, PaymentStatus status,
            Long reservationId) {
        this.orderId = orderId;
        this.paymentKey = paymentKey;
        this.amount = amount;
        this.paymentType = paymentType;
        this.status = status;
        this.reservationId = reservationId;
    }

    public void updateStatusTo(PaymentStatus status) {
        this.status = status;
    }
}
