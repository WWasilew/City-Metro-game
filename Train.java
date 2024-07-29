package com.CityMetro;

import java.awt.*;
import java.util.List;

public class Train {
    private Point currentPosition;
    private TrainRoute route;
    private boolean forward; // True if moving from start to end, false if moving from end to start
    private int speed;
    private int currentSegment; // Current segment index in the route

    public Train(TrainRoute route, int speed) {
        this.route = route;
        this.speed = speed;
        this.forward = true;
        this.currentSegment = 0;
        this.currentPosition = new Point(route.getStations().get(0));
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

        Point target = forward ? stations.get(currentSegment + 1) : stations.get(currentSegment);

        double distance = currentPosition.distance(target);
        if (distance <= speed) {
            currentPosition.setLocation(target);
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
        } else {
            double angle = Math.atan2(target.y - currentPosition.y, target.x - currentPosition.x);
            currentPosition.setLocation(currentPosition.x + speed * Math.cos(angle),
                                        currentPosition.y + speed * Math.sin(angle));
        }
    }
}
