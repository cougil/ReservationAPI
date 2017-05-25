package com.codurance;

public class ReservationService {
    private final ReservationRepository reservationRepository;

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public Reservation add(Reservation reservation, Item item) {
        if (reservation == null) {
            reservation = new Reservation("1");
            reservationRepository.create(reservation);
        }
        reservation.add(item);
        return reservation;
    }
}
