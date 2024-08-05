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

    public void addPointToRouteBegin(Point station) {
        if (!stations.contains(station)) {
            stations.add(0, station);
        }
    }

    public void addPointToRouteEnd(Point station) {
        if (!stations.contains(station)) {
            stations.add(station);
        }
    }

    public List<Point> getStations() {
        return stations;
    }

    public int getSize() {
        return stations.size();
    }
}
