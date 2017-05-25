package com.codurance.acceptance;

import com.codurance.Item;
import com.codurance.Reservation;
import com.codurance.ReservationRepository;
import com.codurance.ReservationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class UpdateReservationFeature {

    @Mock
    private ReservationRepository reservationRepository;

    @Test
    public void add_new_item_creates_a_new_reservation_when_it_does_not_exist() {
        ReservationService reservationService = new ReservationService(reservationRepository);
        Reservation reservation = new Reservation("1");
        Item item = new Item(1);
        reservation.add(item);

        Reservation createdReservation = reservationService.add(null, item);

        verify(reservationRepository).create(createdReservation);

        assertThat(createdReservation).isEqualTo(reservation);
    }

}