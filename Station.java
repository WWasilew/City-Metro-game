package com.CityMetro;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.List;

public class Station {
    public enum Shape {
        RECTANGLE, TRIANGLE, OVAL
    }

    private Point location;
    private Color color;
    private Shape shape;
    private List <Passenger> passengers;

    public Station(Point location) {
        this.location = location;
        this.color = Color.RED; // Domyślny kolor stacji
        setShape(getRandomShape());
        passengers = new ArrayList<>();
    }

    public Station(Point location, Shape shape) {
        this.location = location;
        this.color = Color.RED; // Domyślny kolor stacji
        this.shape = shape;
        passengers = new ArrayList<>();
    }

    public Point getLocation() {
        return location;
    }

    public void setLocation(Point location) {
        this.location = location;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Shape getShape() {
        return shape;
    }

    public void setShape(Shape shape) {
        this.shape = shape;
    }

    public Shape getRandomShape() {
        Shape[] shapes = Shape.values();
        int randomIndex = new Random().nextInt(shapes.length);
        return shapes[randomIndex];
    }

    public void addPassengerToStation(Passenger passenger) {
        passengers.add(passenger);
    }

    public void removePassengerFromStation(Passenger passenger) {
        passengers.remove(passenger);
    }

    public List<Passenger> getPassengers() {
        return passengers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Station station = (Station) o;
        return Objects.equals(location, station.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(location);
    }

    public Set<Character> createPossibleStations(String CityName) {
        Set<Character> uniqueChars = new HashSet<>();
        for (int i = 0; i < CityName.length(); i++) {
            char currentChar = CityName.charAt(i);
            uniqueChars.add(currentChar);
        }
        return uniqueChars;
    }
}
