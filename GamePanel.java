package com.CityMetro;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;

public class GamePanel extends JPanel {
    private List<Point> stations; // lista stacji znajdujących się na planszy
    private List<Line> lines;   //lista dróg między stacjami
    private List<TrainRoute> routes; // Drogi którymi będą poruszały się wagoniki
    private List<Train> trains; // lista wagoników
    private Point tempStation; // Stacja która się wyświetla na szaro
    private Point startLine; // Linia startowa
    private Mode mode; // Mówi o tym czy dodajemy stację czy rysujemi linię

    private final int GRID_SIZE = 50; // Szerokość siatki
    private final int STATION_RADIUS = 5; // Wymiary kropki
    private final int STATION_CLICK_RADIUS = GRID_SIZE/5; // Do poszukiwania najblizszej stacji
    private final int MAX_LINES_PER_STATION = 2; // Ile drog moze byc od jednego punktu
    private final int TRAIN_SPEED = 10; // Dodanie szybkości wagoników

    public enum Mode {
        ADD_STATION,
        DRAW_LINE
    }

    public GamePanel() {
        stations = new ArrayList<>();
        lines = new ArrayList<>();
        routes = new ArrayList<>();
        trains = new ArrayList<>();
        mode = Mode.ADD_STATION; // Domyślny tryb to dodawanie stacji

        // Dodanie stacji początkowej
        BeginGame();

        // Dodanie akcji na kliknięcie myszy
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Point gridPoint = snapToGrid(e.getPoint());

                if (mode == Mode.ADD_STATION) {
                    addStation(gridPoint);
                    tempStation = null;
                    repaint();
                } else if (mode == Mode.DRAW_LINE) {
                    Point nearestStation = findStationNear(gridPoint);
                    if (nearestStation != null) {
                        if (startLine == null) {
                            startLine = nearestStation;
                        } else {
                            if (canAddLine(startLine, nearestStation)) {
                                Line newLine = new Line(startLine, nearestStation);
                                addLine(newLine);
                                updateRoutes();
                                startLine = null;
                                repaint();
                            } else {
                                JOptionPane.showMessageDialog(GamePanel.this, "You cannot connect stations in that way...", "Error", JOptionPane.ERROR_MESSAGE);
                                startLine = null; // Reset startLine even if the line cannot be added
                            }
                        }
                    }
                }
            }
        });

        // Dodanie akcji na ruch myszy
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                tempStation = snapToGrid(e.getPoint());
                repaint();
            }
        });

        // Timer do aktualizacji pozycji kolejek
        Timer timer = new Timer(100, e -> updateTrains());
        timer.start();
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public void addStation(Point p) {
        if (!stations.contains(p)) {
            stations.add(p);
        } else {
            JOptionPane.showMessageDialog(GamePanel.this, "You cannot build station here!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void addLine(Line line) {
        if (!lines.contains(line)) {
            lines.add(line);
        } else {
            JOptionPane.showMessageDialog(GamePanel.this, "You cannot connect lines in that way!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Sprawdzenie czy możemy dodać linię do stacji
    private boolean canAddLine(Point start, Point end) {
        int startCount = countLines(start);
        int endCount = countLines(end);
        boolean buildingLineFromCorrectSide = false;

        for (Line canConnect : lines) {
            if (start == canConnect.start || start == canConnect.end) {
                buildingLineFromCorrectSide = true;
            }
        }

        return startCount < MAX_LINES_PER_STATION && endCount < MAX_LINES_PER_STATION && buildingLineFromCorrectSide;
    }

    // Pomocnicza do zliczania lini miedzy stacjami
    private int countLines(Point station) {
        int count = 0;
        for (Line line : lines) {
            if (line.start.equals(station) || line.end.equals(station)) {
                count++;
            }
        }
        return count;
    }

    // Aktualizowanie trasy wagoników
    private void updateRoutes() {
        routes.clear();
        TrainRoute route = new TrainRoute();

        Point startStation = findSingleConnectionStation();
        if (startStation == null) {
            return;
        }

        route.addStation(startStation);
        Point currentStation = startStation;

        while (true) {
            Point nextStation = findNextConnectedStation(route, currentStation);
            if (nextStation == null) {
                break;
            }
            route.addStation(nextStation);
            currentStation = nextStation;
        }

        routes.add(route);
        List<Train> newTrains = new ArrayList<>();
        if (trains.isEmpty()) {
            trains.add(new Train(route, TRAIN_SPEED));
        } else {
            for (Train train : trains) {
                Train newTrain = new Train(route, TRAIN_SPEED);
                newTrain.setCurrentPosition(train.getCurrentPosition());
                newTrain.setCurrentSegment(train.getCurrentSegment());
                newTrain.setForward(train.isForward());
                newTrains.add(newTrain);
            }
            trains = newTrains;
        }

    }

    // Tworzenie drogi dla wagonika, tak aby zaczynał od stacji z jedną możliwą drogą
    private Point findSingleConnectionStation() {
        for (Point station : stations) {
            if (countLines(station) == 1) {
                return station;
            }
        }
        return null;
    }

    private Point findNextConnectedStation(TrainRoute route, Point currentStation) {
        for (Line line : lines) {
            if (line.start.equals(currentStation) && !route.getStations().contains(line.end)) {
                return line.end;
            }
            if (line.end.equals(currentStation) && !route.getStations().contains(line.start)) {
                return line.start;
            }
        }
        return null;
    }

    // Funkcja która odświeża położenie wagoników
    private void updateTrains() {
        for (Train train : trains) {
            train.updatePosition();
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Rysowanie siatki
        g2d.setColor(Color.LIGHT_GRAY);
        for (int x = 0; x < getWidth(); x += GRID_SIZE) {
            for (int y = 0; y < getHeight(); y += GRID_SIZE) {
                g2d.drawRect(x, y, GRID_SIZE, GRID_SIZE);
            }
        }

        // Rysowanie linii
        g2d.setColor(Color.BLUE);
        for (Line line : lines) {
            g2d.drawLine(line.start.x, line.start.y, line.end.x, line.end.y);
        }

        // Rysowanie stacji
        g2d.setColor(Color.RED);
        for (Point station : stations) {
            g2d.fillOval(station.x - STATION_RADIUS, station.y - STATION_RADIUS, STATION_RADIUS * 2, STATION_RADIUS * 2);
        }

        // Rysowanie tymczasowej stacji na szaro
        if (tempStation != null) {
            g2d.setColor(Color.GRAY);
            g2d.fillOval(tempStation.x - 5, tempStation.y - 5, 10, 10);
        }

        // Rysowanie tymczasowej linii
        if (startLine != null && mode == Mode.DRAW_LINE) {
            g2d.setColor(Color.GRAY);
            g2d.drawLine(startLine.x, startLine.y, tempStation.x, tempStation.y);
        }

        // Rysowanie kolejek
        g2d.setColor(Color.BLUE);
        for (Train train : trains) {
            Point pos = train.getCurrentPosition();
            g2d.fillRect(pos.x - STATION_RADIUS / 2, pos.y - STATION_RADIUS / 2, STATION_RADIUS, STATION_RADIUS);
        }
    }

    private Point snapToGrid(Point p) {
        int x = ((p.x + GRID_SIZE/2) / GRID_SIZE) * GRID_SIZE;
        int y = ((p.y + GRID_SIZE/2) / GRID_SIZE) * GRID_SIZE;
        return new Point(x, y);
    }

    private Point findStationNear(Point p) {
        for (Point station : stations) {
            if (station.distance(p) <= STATION_CLICK_RADIUS) {
                return station;
            }
        }
        return null;
    }

    private void BeginGame() {
        Point HomeStation1 = new Point(500, 450);
        Point HomeStation2 = new Point(400, 400);
        addStation(HomeStation1);
        addStation(HomeStation2);
        addLine(new Line(HomeStation1, HomeStation2));
        updateRoutes();
    }
}