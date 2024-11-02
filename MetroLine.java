package com.CityMetro;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MetroLine {
    private List<Line> lines;
    private Color color;
    private static List<Color> availableColors;
    private int MetroIndex;

    static {
        availableColors = new ArrayList<>(List.of(
            new Color(153, 0, 0),   //LIGHT RED
            Color.GREEN,
            new Color(0, 153, 76), // DARK GREEN
            new Color(0, 255, 255), //LIGHT BLUE
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

    public String getColorName() {
        if (getColor().equals(new Color(153, 0, 0))) {
            return "LIGHT RED";
        } else if (getColor().equals(Color.GREEN)) {
            return "GREEN";
        } else if (getColor().equals(new Color(0, 153, 76))) {
            return "DARK GREEN";
        } else if (getColor().equals(new Color(0, 255, 255))) {
            return "LIGHT BLUE";
        } else if (getColor().equals(new Color(255, 255, 0))) {
            return "YELLOW";
        } else if (getColor().equals(Color.ORANGE)) {
            return "ORANGE";
        } else if (getColor().equals(new Color(127, 0, 255))) {
            return "PURPLE";
        } else if (getColor().equals(new Color(153, 76, 0))) {
            return "BROWN";
        } else if (getColor().equals(new Color(255, 0, 255))) {
            return "PINK";
        } else if (getColor().equals(Color.BLACK)) {
            return "BLACK";
        }
        return "WHITE";
    }

    private Color getRandomColor() {
        return availableColors.remove(0);
    }

    public void setMetroIndex(int metroIndex) {
        this.MetroIndex = metroIndex;
    }

    public int getMetroIndex() {
        return MetroIndex;
    }
}