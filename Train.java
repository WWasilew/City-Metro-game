package com.CityMetro;

import java.awt.*;
import java.util.List;

public class Train {
    private Point currentPosition;
    private TrainRoute route;
    private boolean forward; // True if moving from start to end, false if moving from end to start
    private int speed;
    private int currentSegment; // Current segment index in the route
    private boolean atStation; // Flag indicating if the train is at a station
    private long stopTime; // Stop time at each station (1 second)
    private long stopTimer; // Timer to track stop time

    public Train(TrainRoute route, int speed) {
        this.route = route;
        this.speed = speed;
        this.forward = true;
        this.currentSegment = 0;
        this.currentPosition = new Point(route.getStations().get(0));
        this.atStation = false;
        this.stopTime = 1000; // 1 second stop time at each station in milliseconds
        this.stopTimer = 0;
    }

    public Point getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(Point currentPosition) {
        this.currentPosition = currentPosition;
    }

    public int getCurrentSegment() {
        return currentSegment;
    }

    public void setCurrentSegment(int currentSegment) {
        this.currentSegment = currentSegment;
    }

    public boolean isForward() {
        return forward;
    }

    public void setForward(boolean forward) {
        this.forward = forward;
    }

    public void updatePosition() {
        List<Point> stations = route.getStations();
        if (stations.isEmpty()) return;

        if (atStation) {
            if (System.currentTimeMillis() - stopTimer >= stopTime) {
                atStation = false;
                progressToNextSegment();
            }
            return;
        }

        Point target = forward ? stations.get(currentSegment + 1) : stations.get(currentSegment);

        double distance = currentPosition.distance(target);
        if (distance <= speed) {
            currentPosition.setLocation(target);
            atStation = true;
            stopTimer = System.currentTimeMillis();
        } else {
            double angle = Math.atan2(target.y - currentPosition.y, target.x - currentPosition.x);
            currentPosition.setLocation(currentPosition.x + speed * Math.cos(angle),
                                        currentPosition.y + speed * Math.sin(angle));
        }
    }

    private void progressToNextSegment() {
        List<Point> stations = route.getStations();
        if (forward) {
            currentSegment++;
            if (currentSegment >= stations.size() - 1) {
                setForward(false);
                currentSegment--;
            }
        } else {
            currentSegment--;
            if (currentSegment < 0) {
                setForward(true);
                currentSegment++;
            }
        }
    }
}
