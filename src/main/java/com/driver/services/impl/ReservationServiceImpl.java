package com.driver.services.impl;

import com.driver.model.*;
import com.driver.model.SpotType;
import com.driver.repository.ParkingLotRepository;
import com.driver.repository.ReservationRepository;
import com.driver.repository.SpotRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservationServiceImpl implements ReservationService {
    @Autowired
    UserRepository userRepository3;
    @Autowired
    SpotRepository spotRepository3;
    @Autowired
    ReservationRepository reservationRepository3;
    @Autowired
    ParkingLotRepository parkingLotRepository3;

    @Override
    public Reservation reserveSpot(Integer userId, Integer parkingLotId, Integer timeInHours, Integer numberOfWheels) throws Exception {
        try{
            User user = userRepository3.findById(userId).get();
        ParkingLot parkingLot = parkingLotRepository3.findById(parkingLotId).get();
        if (user == null || parkingLot == null)
            throw new Exception("Cannot make reservation");
        SpotType spotType;
        if (numberOfWheels <= 2)
            spotType = SpotType.TWO_WHEELER;
        else if (numberOfWheels <= 4)
            spotType = SpotType.FOUR_WHEELER;
        else
            spotType = SpotType.OTHERS;
        List<Spot> spotList = parkingLot.getSpotList();
        int min = Integer.MAX_VALUE;
        Spot minSpot = null;
        for (Spot spot : spotList) {
            if (spotType.equals(SpotType.OTHERS) && spot.getSpotType().equals(SpotType.OTHERS)) {
                if (min > spot.getPricePerHour() && !spot.getOccupied()) {
                    min = spot.getPricePerHour();
                    minSpot = spot;
                }
            } else if (spotType.equals(SpotType.FOUR_WHEELER) && (spot.getSpotType().equals(SpotType.OTHERS) || spot.getSpotType().equals(SpotType.FOUR_WHEELER))) {
                if (min > spot.getPricePerHour() && !spot.getOccupied()) {
                    min = spot.getPricePerHour();
                    minSpot = spot;
                }
            } else if (spotType.equals(SpotType.TWO_WHEELER) && (spot.getSpotType().equals(SpotType.OTHERS) || spot.getSpotType().equals(SpotType.FOUR_WHEELER) || spot.getSpotType().equals(SpotType.TWO_WHEELER))) {
                if (min > spot.getPricePerHour() && !spot.getOccupied()) {
                    min = spot.getPricePerHour();
                    minSpot = spot;
                }
            }
        }
        if (min == Integer.MAX_VALUE)
            throw new Exception("Cannot make reservation");

        minSpot.setOccupied(true);

        Reservation reservation = new Reservation(timeInHours, user, minSpot);
        reservationRepository3.save(reservation);

        user.getReservationList().add(reservation);
        minSpot.getReservationList().add(reservation);

        userRepository3.save(user);
        spotRepository3.save(minSpot);

        return reservation;
    }
        catch (Exception e){
            return null;
        }




    }
}
