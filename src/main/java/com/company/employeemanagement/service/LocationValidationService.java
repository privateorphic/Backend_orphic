package com.company.employeemanagement.service;

import com.company.employeemanagement.entity.OfficeLocation;
import com.company.employeemanagement.repository.OfficeLocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LocationValidationService {

    private final OfficeLocationRepository officeLocationRepository;

    @Transactional
    public OfficeLocation getOrCreateDefaultOfficeLocation() {
        return officeLocationRepository.findFirstByActiveTrue()
                .orElseGet(() -> {
                    OfficeLocation defaultLocation = new OfficeLocation();
                    defaultLocation.setName("Main Head Office");
                    defaultLocation.setLatitude(23.525123);
                    defaultLocation.setLongitude(77.808123);
                    defaultLocation.setRadiusMeters(100.0);
                    defaultLocation.setActive(true);
                    return officeLocationRepository.save(defaultLocation);
                });
    }

    /**
     * Calculates distance between user GPS coordinates and office location in meters using Haversine Formula.
     */
    public double calculateDistanceMeters(double lat1, double lng1, double lat2, double lng2) {
        final int EARTH_RADIUS_METERS = 6371000;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_METERS * c;
    }

    public boolean isWithinRadius(double distanceMeters, double allowedRadiusMeters) {
        return distanceMeters <= allowedRadiusMeters;
    }
}
