package com.CityMetro;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MetroLine {
    private List<Line> lines;
    private Color color;

    public MetroLine() {
        this.lines = new ArrayList<>();
        this.color = getRandomColor();
    }

    public void addLine(Line line) {
        if (!lines.contains(line)) {
            lines.add(line);
        }
    }

    public List<Line> getLines() {
        return lines;
    }

    public Line getLine(int index) {
        return lines.get(index);
    }

    public Color getColor() {
        return color;
    }

    private Color getRandomColor() {
        return new Color((float)Math.random(), (float)Math.random(), (float)Math.random());
    }
}
