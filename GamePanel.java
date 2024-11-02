package com.CityMetro;

import javax.swing.*;

import com.CityMetro.Station.Shape;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GamePanel extends JPanel {
    private List<Station> stations;         // lista stacji znajdujących się na planszy
    private List<MetroLine> metroLines;     // lista dróg między stacjami
    private List<TrainRoute> routes;        // Drogi którymi będą poruszały się wagoniki
    private List<Train> trains;             // lista wagoników
    private Point tempStation;              // Stacja która się wyświetla na szaro
    private Point startLine;                // Linia startowa
    private Mode mode;                      // Mówi o tym czy dodajemy stację czy rysujemi linię
    private int metroLineIndex = -1;
    private int completePassnegers = 0;     // Ile pasażerów już obsłużyiliśmy
    private Point tempTrainPoint;           // Punkt, w którym wyświetla się tymczasowy pociąg

    private final int GRID_SIZE = 50;                       // Szerokość siatki
    private final int STATION_RADIUS = 10;                  // Wymiary kropki
    private final int STATION_CLICK_RADIUS = GRID_SIZE/5;   // Do poszukiwania najblizszej stacji
    private final int MAX_LINES_PER_STATION = 2;            // Ile drog moze byc od jednego punktu
    private final int TRAIN_SPEED = 10;                     // Dodanie szybkości wagoników
    private final int MAX_NUMBER_OF_METRO_LINES = 5;        // Ile maksymalnie linii metra można zbudować
    private final int MIN_DISTANCE_BETWEEN_STATIONS = GRID_SIZE * 2;  // Minimalna odległość między stacjami
    private final int MAX_DISTANCE_BETWEEN_STATIONS = GRID_SIZE * 3;  // Minimalna odległość między stacjami
    private final int PASSENGER_RADIUS = 5;                           // Rozmiar pasażerów
    private final int PASSENGER_OFFSET = STATION_RADIUS + 5;          // Odległość od stacji

    public enum Mode {
        ADD_STATION,
        DRAW_LINE,
        ADD_NEW_METRO_LINE,
        ADD_TRAIN
    }

    public GamePanel() {
        stations = new ArrayList<>();
        metroLines = new ArrayList<>();
        routes = new ArrayList<>();
        trains = new ArrayList<>();
        mode = Mode.ADD_STATION;                // Domyślny tryb to dodawanie stacji
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
                } else if (mode == Mode.ADD_TRAIN) {
                    Station nearestStation = findStationNear(gridPoint);
                    if (nearestStation != null) {
                        TrainRoute route = findRouteNear(nearestStation.getLocation());
                        if (route != null) {
                            trains.add(new Train(route, TRAIN_SPEED));
                            tempTrainPoint = null;
                            repaint();
                        }
                    }
                } else if (mode == Mode.DRAW_LINE || mode == Mode.ADD_NEW_METRO_LINE) {
                    Station nearestStation = findStationNear(gridPoint);
                    if (nearestStation != null) {
                        if (startLine == null) {
                            startLine = nearestStation.getLocation();
                        } else if (mode == Mode.DRAW_LINE) {
                            if (canAddLine(startLine, nearestStation.getLocation())) {
                                //whichLineIsThat(startLine);
                                Line newLine = new Line(startLine, nearestStation.getLocation());
                                if (metroLines.get(metroLineIndex).isLineNotAPoint(newLine)) {
                                    metroLines.get(metroLineIndex).addLine(newLine);
                                    updateRoute(metroLineIndex, newLine);
                                    nearestStation.setColor(metroLines.get(metroLineIndex).getColor());
                                    findStationNear(startLine).setColor(metroLines.get(metroLineIndex).getColor());
                                    startLine = null;
                                    repaint();
                                } else {
                                    JOptionPane.showMessageDialog(GamePanel.this, "Bad station connection", "Error", JOptionPane.ERROR_MESSAGE);
                                }
                            } else {
                                JOptionPane.showMessageDialog(GamePanel.this, "You cannot connect stations in that way...", "Error", JOptionPane.ERROR_MESSAGE);
                                startLine = null;
                            }
                        } else if (mode == Mode.ADD_NEW_METRO_LINE) {
                            addMetroLine();
                            Line newLine = new Line(startLine, nearestStation.getLocation());
                            if (metroLines.get(metroLineIndex).isLineNotAPoint(newLine)) {
                                metroLines.get(metroLineIndex).addLine(newLine);
                                updateRoute(metroLineIndex, newLine);
                                nearestStation.setColor(metroLines.get(metroLineIndex).getColor());
                                findStationNear(startLine).setColor(metroLines.get(metroLineIndex).getColor());
                                startLine = null;
                                repaint();
                                mode = Mode.DRAW_LINE;
                                JOptionPane.showMessageDialog(GamePanel.this, "You succesfully started new metro line", "Success", JOptionPane.INFORMATION_MESSAGE);
                                startLine = null;
                            } else {
                                JOptionPane.showMessageDialog(GamePanel.this, "Bad station connection", "Error", JOptionPane.ERROR_MESSAGE);
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
                if (mode == Mode.ADD_TRAIN) {
                    Station nearestStation = findStationNear(tempStation);
                    if (nearestStation != null) {
                        tempTrainPoint = nearestStation.getLocation();
                    } else {
                        tempTrainPoint = null;
                    }
                }
                repaint();
            }
        });

        // Timer do aktualizacji pozycji kolejek
        Timer timer = new Timer(100, e -> updateTrains());
        timer.start();

        // Timer do automatycznego dodawania stacji
        Timer stationTimer = new Timer(10000, e -> addRandomStation());
        stationTimer.start();

        // Timer do pojawiania się pasażerów
        Timer passengerTimer = new Timer(10000, e -> addRandomPassenger());
        passengerTimer.start();
    }

    public void addRandomPassenger() {
        Station randomStartStation = getRandomStation();
        Station randomDestinationStation = getRandomStation();
        while (randomDestinationStation.getShape().equals(randomStartStation.getShape())) {
            randomDestinationStation = getRandomStation();
        }
        Passenger newPassenger = new Passenger(randomStartStation, randomDestinationStation);
        randomStartStation.addPassengerToStation(newPassenger);
    }

    public void managePassengers(Train train){
        Point stationLocation = train.getCurrentPosition();
        whichLineIsThat(stationLocation);
        int i = 0;
        int stationIndex = 0;
        for(Station station : stations) {
            if (station.getLocation().equals(stationLocation)) {
                stationIndex = i;
            }
            ++i;
        }
        Station station = stations.get(stationIndex);

        // Usuwanie pasażerów z pociągu, jeśli dotarli do stacji
        List<Passenger> passengersToRemove = new ArrayList<>();
        for (Passenger passenger : train.getPassengers()) {
            if (passenger.getShape().equals(station.getShape())) {
                passengersToRemove.add(passenger);
                completePassnegers++;
        }
        }
        // Usuń pasażerów po iteracji
        for (Passenger passenger : passengersToRemove) {
            train.removePassengerFromTrain(passenger);
        }

        // Dodawanie pasażerów do pociągu z aktualnej stacji
        List<Passenger> passengersToAdd = new ArrayList<>(station.getPassengers());
        for (Passenger passenger : passengersToAdd) {
            train.addPassengertoTrain(passenger);
            station.removePassengerFromStation(passenger);
        }
    }

    // Funkcja do losowania stacji
    private Station getRandomStation() {
        if (stations.isEmpty()) {
            return null;
        }
        Random random = new Random();
        int randomIndex = random.nextInt(stations.size());
        return stations.get(randomIndex);
    }

    public void addStation(Point p) {
        if (findStationNear(p) == null) {
            stations.add(new Station(p));
        } else {
            JOptionPane.showMessageDialog(GamePanel.this, "You cannot build station here!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addRandomStation() {
        Random rand = new Random();
        List<Station> possibleStations = possibleStationLocations();
        Station randomPoint = possibleStations.get(rand.nextInt(possibleStations.size()));
        addStation(randomPoint.getLocation());
        repaint();
    }

    public List<Station> possibleStationLocations() {
        List<Station> possibleStations = new ArrayList<>();
        int[] distances = {MAX_DISTANCE_BETWEEN_STATIONS, MIN_DISTANCE_BETWEEN_STATIONS};

        for (Station station : stations) {
            Point stationLocation = station.getLocation();
            for (int dx : distances) {
                for (int dy : distances) {
                    addPossibleStation(possibleStations, new Point(stationLocation.x - dx, stationLocation.y + dy));
                    addPossibleStation(possibleStations, new Point(stationLocation.x + dx, stationLocation.y + dy));
                    addPossibleStation(possibleStations, new Point(stationLocation.x - dx, stationLocation.y - dy));
                    addPossibleStation(possibleStations, new Point(stationLocation.x + dx, stationLocation.y - dy));
                    addPossibleStation(possibleStations, new Point(stationLocation.x - dx, stationLocation.y));
                    addPossibleStation(possibleStations, new Point(stationLocation.x + dx, stationLocation.y));
                    addPossibleStation(possibleStations, new Point(stationLocation.x, stationLocation.y + dy));
                    addPossibleStation(possibleStations, new Point(stationLocation.x, stationLocation.y - dy));
                }
            }
        }

        // Usuń stacje, które są zbyt blisko istniejących stacji
        for (Station station : stations) {
            Point stationLocation = station.getLocation();
            removeStationIfTooClose(possibleStations, new Point(stationLocation.x - GRID_SIZE, stationLocation.y + GRID_SIZE));
            removeStationIfTooClose(possibleStations, new Point(stationLocation.x, stationLocation.y + GRID_SIZE));
            removeStationIfTooClose(possibleStations, new Point(stationLocation.x + GRID_SIZE, stationLocation.y + GRID_SIZE));
            removeStationIfTooClose(possibleStations, new Point(stationLocation.x + GRID_SIZE, stationLocation.y));
            removeStationIfTooClose(possibleStations, new Point(stationLocation.x + GRID_SIZE, stationLocation.y - GRID_SIZE));
            removeStationIfTooClose(possibleStations, new Point(stationLocation.x, stationLocation.y - GRID_SIZE));
            removeStationIfTooClose(possibleStations, new Point(stationLocation.x - GRID_SIZE, stationLocation.y - GRID_SIZE));
            removeStationIfTooClose(possibleStations, new Point(stationLocation.x - GRID_SIZE, stationLocation.y));
        }

        // Usuń stacje poza wyznoczonymi granicami x <50, 1000> i y <50, 850>
        removeStationBeyondBorders(possibleStations);

        return possibleStations;
    }

    private void addPossibleStation(List<Station> possibleStations, Point location) {
        Station newStation = new Station(location);
        if (!stations.contains(newStation) && !possibleStations.contains(newStation)) {
            possibleStations.add(newStation);
        }
    }

    private void removeStationIfTooClose(List<Station> possibleStations, Point location) {
        if (possibleStations.isEmpty()) {
            return;
        }
        Station existingStation = new Station(location);
        if (possibleStations.contains(existingStation)) {
            possibleStations.remove(existingStation);
        }
    }

    private void removeStationBeyondBorders(List<Station> possibleStations) {
        if (possibleStations.isEmpty()) {
            return;
        }
        List<Station> correctStations = new ArrayList<>();
        for (Station station : possibleStations) {
            Point stationLocation = station.getLocation();
            if (((stationLocation.x >= 50 && stationLocation.x <= 1000) && (stationLocation.y >= 50 && stationLocation.y <= 850))) {
                correctStations.add(station);
            }
        }
        possibleStations.clear();
        possibleStations.addAll(correctStations);
    }

    public void whichLineIsThat(Point p) {
        int count = 0;
        for (MetroLine metroLine : metroLines) {
            for (Line line : metroLine.getLines()) {
                if (line.end == p || line.start == p) {
                    metroLineIndex = count;
                }
            }
            count++;
        }
    }

    public void addMetroLine() {
        metroLineIndex = metroLines.size();
        metroLines.add(new MetroLine());                // Dodanie nowej linni metra
        metroLines.get(metroLineIndex).setMetroIndex(metroLineIndex);
        routes.add(new TrainRoute());                   // Dodanie nowej trasy dla wagoników
    }

    private List<Integer> getMetroLinesForStation(Point station) {
        List<Integer> connectedLines = new ArrayList<>();
        for (MetroLine metroLine : metroLines) {
            for (Line line : metroLine.getLines()) {
                if (line.start.equals(station) || line.end.equals(station)) {
                    connectedLines.add(metroLine.getMetroIndex());
                    break;
                }
            }
        }
        return connectedLines;
    }

    // Sprawdzenie czy możemy dodać linię do stacji
    private boolean canAddLine(Point start, Point end) {
        boolean buildingLineFromCorrectSide = false;
        List<Integer> connectedLines = getMetroLinesForStation(start);

        // Jeśli jest więcej niż jedna linia metra podłączona do stacji, wybierz linię metra z okna dialogowego
        if (connectedLines.size() > 1) {
            String[] lineNames = new String[connectedLines.size()];
            for (int i = 0; i < connectedLines.size(); i++) {
                lineNames[i] = metroLines.get(connectedLines.get(i)).getColorName();
            }
            String selectedLine = (String) JOptionPane.showInputDialog(
                    this,
                    "Select the metro line to extend:",
                    "Choose Metro Line",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    lineNames,
                    lineNames[0]
            );
            if (selectedLine != null) {
                for (int i = 0; i < connectedLines.size(); i++) {
                    if (metroLines.get(connectedLines.get(i)).getColorName().equals(selectedLine)) {
                        metroLineIndex = connectedLines.get(i);
                        break;
                    }
                }
                buildingLineFromCorrectSide = true;
            } else {
                return false;
            }
        } else if (connectedLines.size() == 1) {
            metroLineIndex = connectedLines.get(0);
            buildingLineFromCorrectSide = true;
        }
        boolean startCount = countLines(start);

        return startCount && buildingLineFromCorrectSide;
    }

    // Pomocnicza do zliczania lini miedzy stacjami
    private boolean countLines(Point station) {
        int count = 0;
        boolean result = false;
        //whichLineIsThat(station);
        for (Line l : metroLines.get(metroLineIndex).getLines()) {
            if (l.start.equals(station) || l.end.equals(station)) {
                count++;
            }
        }
        if (count < MAX_LINES_PER_STATION) {
            result = true;
        }
        return result;
    }

    // Aktualizowanie trasy wagoników
    private void updateRoute(int metroLineIndex, Line line) {
        if (routes.get(metroLineIndex).getSize() == 0) {
            routes.get(metroLineIndex).addPointToRouteBegin(line.start);
            routes.get(metroLineIndex).addPointToRouteEnd(line.end);
            trains.add(new Train(routes.get(metroLineIndex), TRAIN_SPEED));
        } else {
            Point startPoint = line.start;
            Point endPoint = line.end;
            MetroLine activeMetroLine = metroLines.get(metroLineIndex);
            for (Line metroLines : activeMetroLine.getLines()) {
                if (startPoint.equals(metroLines.start)) {
                    routes.get(metroLineIndex).addPointToRouteBegin(endPoint);
                    Point temPoint = startPoint;
                    line.start = line.end;
                    line.end = temPoint;
                    break;
                } else if (startPoint.equals(metroLines.end)) {
                    routes.get(metroLineIndex).addPointToRouteEnd(endPoint);
                    break;
                }
            }
        }
    }

    // Funkcja która odświeża położenie wagoników
    private void updateTrains() {
        for (Train train : trains) {
            int whatToDo = train.updatePosition();
            if(whatToDo == -1) {
                return;
            } else if (whatToDo == 0) {
                //System.out.println("I am at station");
                managePassengers(train);
            }
        }
        repaint();
    }

    private void paintRectangle(Graphics2D g2d, int x, int y, int radius) {
        g2d.fillRect(x - radius, y - radius, radius*2, radius*2);
    }

    private void paintOval(Graphics2D g2d, int x, int y, int radius) {
        g2d.fillOval(x-radius, y-radius, radius*2, radius*2);
    }

    private void paintTriangle(Graphics2D g2d, int x, int y, int radius) {
        Polygon triangle = new Polygon();
        triangle.addPoint(x, y - radius);
        triangle.addPoint(x - radius, y + radius);
        triangle.addPoint(x + radius, y + radius);
        g2d.fillPolygon(triangle);
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
        for (MetroLine metroLine : metroLines) {
            g2d.setColor(metroLine.getColor());
            for (Line line : metroLine.getLines()) {
                g2d.drawLine(line.start.x, line.start.y, line.end.x, line.end.y);
            }
        }

        // Rysowanie stacji
        for (Station station : stations) {
            g2d.setColor(station.getColor());
            int x = station.getLocation().x;
            int y = station.getLocation().y;
            switch (station.getShape()) {
                case RECTANGLE:
                paintRectangle(g2d, x, y, STATION_RADIUS);
                break;

                case TRIANGLE:
                paintTriangle(g2d, x, y, STATION_RADIUS);
                break;

                case OVAL:
                paintOval(g2d, x, y, STATION_RADIUS);
                break;
            }
        }

        // Rysowanie tymczasowej stacji na szaro
        if (tempStation != null && mode == Mode.ADD_STATION) {
            g2d.setColor(Color.GRAY);
            g2d.fillOval(tempStation.x - 5, tempStation.y - 5, 10, 10);
        }

        // Rysowanie tymczasowej linii
        if (startLine != null && (mode == Mode.DRAW_LINE || mode == Mode.ADD_NEW_METRO_LINE)) {
            g2d.setColor(Color.GRAY);
            g2d.drawLine(startLine.x, startLine.y, tempStation.x, tempStation.y);
        }

        // Rysowanie tymczasowego pociągu
        if (tempTrainPoint != null && mode == Mode.ADD_TRAIN) {
            g2d.setColor(Color.GRAY);
            g2d.fillRect(tempTrainPoint.x - STATION_RADIUS / 2, tempTrainPoint.y - STATION_RADIUS / 2, STATION_RADIUS, STATION_RADIUS);
        }

        // Rysowanie kolejek
        g2d.setColor(Color.BLUE);
        for (Train train : trains) {
            Point pos = train.getCurrentPosition();
            g2d.fillRect(pos.x - STATION_RADIUS / 2, pos.y - STATION_RADIUS / 2, STATION_RADIUS, STATION_RADIUS);
        }

        // Rysowanie pasażerów na stacjach
        for (Station station : stations) {
            int passengerIndex = 0; // Pozycjonowanie pasażerów wokół stacji
            for (Passenger passenger : station.getPassengers()) {
                // Wyznaczenie pozycji pasażera wokół stacji
                int x = station.getLocation().x - (passengerIndex * (PASSENGER_RADIUS * 2 + 5)) - PASSENGER_OFFSET;
                int y = station.getLocation().y - PASSENGER_OFFSET;

                // Rysowanie pasażera
                g2d.setColor(Color.RED); // Kolor pasażerów
                switch (passenger.getShape()) {
                    case RECTANGLE:
                    paintRectangle(g2d, x, y, PASSENGER_RADIUS);
                    break;

                    case TRIANGLE:
                    paintTriangle(g2d, x, y, PASSENGER_RADIUS);
                    break;

                    case OVAL:
                    paintOval(g2d, x, y, PASSENGER_RADIUS);
                    break;
                }
                passengerIndex++;
            }
        }

        // Rysowanie pasażerów w kolejkach
        for (Train train : trains) {
            int passengerIndex = 0; // Pozycjonowanie pasażerów wokół stacji
            for (Passenger passenger : train.getPassengers()) {
                // Wyznaczenie pozycji pasażera wokół stacji
                Point currentPosition = train.getCurrentPosition();
                int x = currentPosition.x + (passengerIndex * (PASSENGER_RADIUS * 2 + 5)) + PASSENGER_OFFSET;
                int y = currentPosition.y - PASSENGER_OFFSET;

                // Rysowanie pasażera
                g2d.setColor(Color.RED); // Kolor pasażerów
                switch (passenger.getShape()) {
                    case RECTANGLE:
                    paintRectangle(g2d, x, y, PASSENGER_RADIUS);
                    break;

                    case TRIANGLE:
                    paintTriangle(g2d, x, y, PASSENGER_RADIUS);
                    break;

                    case OVAL:
                    paintOval(g2d, x, y, PASSENGER_RADIUS);
                    break;
                }
                passengerIndex++;
            }
        }
    }

    private Point snapToGrid(Point p) {
        int x = ((p.x + GRID_SIZE/2) / GRID_SIZE) * GRID_SIZE;
        int y = ((p.y + GRID_SIZE/2) / GRID_SIZE) * GRID_SIZE;
        return new Point(x, y);
    }

    private Station findStationNear(Point p) {
        for (Station station : stations) {
            if (station.getLocation().distance(p) <= STATION_CLICK_RADIUS) {
                return station;
            }
        }
        return null;
    }

    private TrainRoute findRouteNear(Point station) {
        for (TrainRoute route : routes) {
            for (Point point : route.getStations()) {
                if (point.equals(station)) {
                    return route;
                }
            }
        }
        return null;
    }

    private void BeginGame() {
        Point HomeStation1 = new Point(500, 450);
        Point HomeStation2 = new Point(400, 400);
        addStation(HomeStation1);
        addStation(HomeStation2);
        stations.get(0).setShape(Shape.OVAL);
        stations.get(1).setShape(Shape.RECTANGLE);
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public Mode getMode() {
        return this.mode;
    }

    public int CountStations() {
        return stations.size();
    }

    public int getCompletedPassengers() {
        return completePassnegers;
    }

    public int getMetroLineIndex() {
        return metroLineIndex;
    }

    public int getMaxMetroLines() {
        return MAX_NUMBER_OF_METRO_LINES;
    }
}
