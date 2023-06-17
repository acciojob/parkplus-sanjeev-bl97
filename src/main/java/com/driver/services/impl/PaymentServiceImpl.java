package com.driver.services.impl;

import com.driver.model.Payment;
import com.driver.model.Reservation;
import com.driver.model.PaymentMode;
import com.driver.repository.PaymentRepository;
import com.driver.repository.ReservationRepository;
import com.driver.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    ReservationRepository reservationRepository2;
    @Autowired
    PaymentRepository paymentRepository2;

    @Override
    public Payment pay(Integer reservationId, int amountSent, String mode) throws Exception {

        Reservation reservation = reservationRepository2.findById(reservationId).get();

        int totalCost = reservation.getNumberOfHours() * reservation.getSpot().getPricePerHour();

        if(totalCost > amountSent)
            throw new Exception("Insufficient Amount");

        PaymentMode paymentMode;

        if(mode.equalsIgnoreCase("CASH"))
            paymentMode = PaymentMode.CASH;
       else if(mode.equalsIgnoreCase("UPI"))
            paymentMode = PaymentMode.UPI;
        else if(mode.equalsIgnoreCase("CARD"))
            paymentMode = PaymentMode.CARD;
        else
            throw new Exception("Payment mode not detected");

        Payment payment = new Payment(true,paymentMode);

        payment.setReservation(reservation);

        reservation.setPayment(payment);

        reservationRepository2.save(reservation);

        return payment;

    }
}
