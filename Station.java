package com.CityMetro;

import java.awt.Color;
import java.awt.Point;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Station {
    private Point location;
    private Color color;

    public Station(Point location) {
        this.location = location;
        this.color = Color.RED; // Domyślny kolor stacji
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
