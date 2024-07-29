package com.CityMetro;

import java.util.HashSet;
import java.util.Set;

public class Station {
    Set<Character> possibleStationsNames;
    String cityName;

    public Station(String cityName) {
        this.cityName = cityName;
        this.possibleStationsNames = createPossibleStations(cityName);
    }

    public Set<Character> createPossibleStations(String CityName) {
        Set<Character> uniqueChars = new HashSet<>();
        for (int i = 0; i < CityName.length(); i++) {
            char currentChar = CityName.charAt(i);
            uniqueChars.add(currentChar);
        }
        return uniqueChars;
    }

    public Set<Character> getPossibleStations() {
        return possibleStationsNames;
    }
}
