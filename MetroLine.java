package com.CityMetro;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MetroLine {
    private List<Line> lines;
    private Color color;
    private static List<Color> availableColors;

    static {
        availableColors = new ArrayList<>(List.of(
            Color.RED,
            Color.GREEN,
            new Color(0, 153, 76), // DARK GREEN
            new Color(0, 255, 255), //LIGHST BLUE
            new Color(255,255,0),   // YELLOW
            Color.ORANGE,
            new Color(127, 0, 255), // PURPLE
            new Color(153, 76, 0), // BROWN
            new Color(255, 0, 255), // PINK
            Color.BLACK
        ));
        Collections.shuffle(availableColors); // Shuffle to ensure randomness
    }

    public MetroLine() {
        this.lines = new ArrayList<>();
        this.color = getRandomColor();
    }

    public boolean isLineNotAPoint(Line line) {
        return line.start != line.end ? true : false;
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
        return availableColors.remove(0);
    }
}