package roomescape.reservation.application;

import static roomescape.payment.model.PaymentStatus.*;
import static roomescape.payment.model.PaymentType.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.payment.model.Payment;
import roomescape.payment.model.PaymentRepository;
import roomescape.reservation.application.dto.request.CreateReservationServiceRequest;
import roomescape.reservation.application.dto.response.ReservationServiceResponse;
import roomescape.reservation.application.dto.response.UserReservationServiceResponse;
import roomescape.reservation.model.entity.Reservation;
import roomescape.reservation.model.entity.ReservationWaiting;
import roomescape.reservation.model.repository.ReservationRepository;
import roomescape.reservation.model.repository.ReservationWaitingRepository;
import roomescape.reservation.model.repository.dto.ReservationWaitingWithRank;
import roomescape.reservation.model.service.ReservationOperation;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationWaitingRepository reservationWaitingRepository;
    private final ReservationOperation reservationOperation;
    private final PaymentRepository paymentRepository;

    @Transactional
    public ReservationServiceResponse create(CreateReservationServiceRequest request) {
        Reservation savedReservation = reservationOperation.reserve(request.toSchedule(), request.memberId());

        Payment payment = request.toPayment(savedReservation.getId(), PENDING, TOSS);
        Payment savedPayment = paymentRepository.save(payment);

        return ReservationServiceResponse.of(savedReservation, savedPayment.getId());
    }

    public List<UserReservationServiceResponse> getAllByMemberId(Long memberId) {
        List<Reservation> reservations = reservationRepository.findAllByMemberId(memberId);
        List<ReservationWaitingWithRank> reservationWaitingWithRanks = reservationWaitingRepository.findAllWithRankByMemberId(
                memberId);

        List<UserReservationServiceResponse> responses = createUserReservationServiceResponse(
                reservations,
                reservationWaitingWithRanks
        );

        return sortByDateTime(responses);
    }

    @Transactional
    public void cancel(Long id, Long memberId) {
        Reservation reservation = reservationRepository.getById(id);
        reservation.checkOwner(memberId);
        reservationOperation.cancel(reservation.getId());
    }

    private List<UserReservationServiceResponse> createUserReservationServiceResponse(
            List<Reservation> reservations,
            List<ReservationWaitingWithRank> reservationWaitingWithRanks
    ) {
        List<UserReservationServiceResponse> responses = new ArrayList<>();
        for (Reservation reservation : reservations) {
            Payment latestPayment = paymentRepository.getLatestByReservationId(reservation.getId());
            responses.add(UserReservationServiceResponse.of(reservation, latestPayment));
        }
        for (ReservationWaitingWithRank waitingWithRank : reservationWaitingWithRanks) {
            ReservationWaiting reservationWaiting = waitingWithRank.getReservationWaiting();
            int rank = waitingWithRank.getRankToInt();
            responses.add(UserReservationServiceResponse.of(reservationWaiting, rank));
        }

        return responses;
    }

    private List<UserReservationServiceResponse> sortByDateTime(List<UserReservationServiceResponse> responses) {
        return responses.stream()
                .sorted(Comparator.comparing(UserReservationServiceResponse::date)
                        .thenComparing(UserReservationServiceResponse::time))
                .toList();
    }
}
