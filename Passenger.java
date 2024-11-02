package com.CityMetro;

import com.CityMetro.Station.Shape;
import java.awt.Point;


public class Passenger {
    private Point homeLocation;
    private Point passengerLocation;
    private Shape shape;

    public Passenger(Station homeStation, Station destination) {
        this.passengerLocation = homeStation.getLocation();
        this.homeLocation = homeStation.getLocation();
        this.shape = destination.getShape();
    }

    public Point getLocation() {
        return passengerLocation;
    }

    public void setLocation(Point location) {
        this.passengerLocation = location;
    }

    public Shape getShape() {
        return shape;
    }

    public void setShape(Shape shape) {
        this.shape = shape;
    }

    public boolean destinationLocation(Station station) {
        if (station.getShape().equals(shape) && !station.getLocation().equals(homeLocation)) {
            return true;
        }
        return false;
    }
}
