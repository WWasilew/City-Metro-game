package com.CityMetro;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TrainRoute {
    private List<Point> stations;

    public TrainRoute() {
        this.stations = new ArrayList<>();
    }

    public void addStation(Point station) {
        if (!stations.contains(station)) {
            stations.add(station);
        }
    }

    public List<Point> getStations() {
        return stations;
    }
}
