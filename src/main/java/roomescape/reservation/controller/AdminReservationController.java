package roomescape.reservation.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import roomescape.reservation.application.AdminReservationService;
import roomescape.reservation.application.dto.response.ReservationServiceResponse;
import roomescape.reservation.controller.dto.request.AdminCreateReservationRequest;
import roomescape.reservation.controller.dto.request.ReservationSearchRequest;
import roomescape.reservation.controller.dto.response.ReservationResponse;
import roomescape.reservation.model.entity.vo.ReservationStatus;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/reservations")
public class AdminReservationController {

    private final AdminReservationService adminReservationService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ReservationResponse create(@RequestBody @Valid AdminCreateReservationRequest request) {
        ReservationServiceResponse response = adminReservationService.create(request.toServiceRequest());
        return ReservationResponse.from(response);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<ReservationResponse> getAllByStatus(
            @RequestParam(required = false, defaultValue = "CONFIRMED") List<ReservationStatus> statuses
    ) {
        List<ReservationServiceResponse> responses = adminReservationService.getAllByStatuses(statuses);
        return responses.stream()
                .map(ReservationResponse::from)
                .toList();
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/search")
    public List<ReservationResponse> getSearchedAll(@ModelAttribute ReservationSearchRequest request) {
        List<ReservationServiceResponse> responses = adminReservationService.getSearchedAll(request.toServiceRequest());
        return responses.stream()
                .map(ReservationResponse::from)
                .toList();
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/{id}/cancel")
    public void cancel(@PathVariable("id") Long id) {
        adminReservationService.cancel(id);
    }
}
