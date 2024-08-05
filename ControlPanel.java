package com.CityMetro;

import javax.swing.*;

import com.CityMetro.GamePanel.Mode;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ControlPanel extends JPanel {
    private JButton addStationButton;
    private JButton drawLineButton;
    private JButton addNewMetroLineButton;
    private JButton addTrainButton;
    private JLabel stationCountLabel;
    private JLabel currentActionLabel;
    private JLabel metroLineIndexLabel;

    public ControlPanel(GamePanel gamePanel) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        Dimension buttonSize = new Dimension(150, 20);

        addStationButton = new JButton("Add Station");
        drawLineButton = new JButton("Draw Line");
        addNewMetroLineButton = new JButton("Start new metro line");
        addTrainButton = new JButton("Add train");
        stationCountLabel = new JLabel("Stations: 2");
        currentActionLabel = new JLabel("Action: Add Station");
        metroLineIndexLabel = new JLabel("Current metro line: 1");

        setButtonSize(addStationButton, buttonSize);
        setButtonSize(drawLineButton, buttonSize);
        setButtonSize(addNewMetroLineButton, buttonSize);
        setButtonSize(addTrainButton, buttonSize);

        addStationButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        drawLineButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        addNewMetroLineButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        addTrainButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        stationCountLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        currentActionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        metroLineIndexLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        add(addStationButton);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(drawLineButton);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(addNewMetroLineButton);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(addTrainButton);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(stationCountLabel);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(currentActionLabel);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(metroLineIndexLabel);
        add(Box.createRigidArea(new Dimension(0, 10)));

        addStationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gamePanel.setMode(GamePanel.Mode.ADD_STATION);
            }
        });

        drawLineButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gamePanel.setMode(GamePanel.Mode.DRAW_LINE);
            }
        });

        addNewMetroLineButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gamePanel.setMode(GamePanel.Mode.ADD_NEW_METRO_LINE);
            }
        });

        addTrainButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gamePanel.setMode(GamePanel.Mode.ADD_TRAIN);
            }
        });

        Timer timerForStation = new Timer(100, e -> updateStationCount(gamePanel));
        Timer timerForAction = new Timer(100, e ->updateActionMode(gamePanel));
        Timer timerForActiveMetroLine = new Timer(100, e -> updateActiveMetro(gamePanel));
        timerForStation.start();
        timerForAction.start();
        timerForActiveMetroLine.start();
    }

    private void setButtonSize(JButton button, Dimension size) {
        button.setPreferredSize(size);
        button.setMaximumSize(size);
        button.setMinimumSize(size);
    }

    public void updateActionMode(GamePanel gamePanel) {
        if (gamePanel.getMode() == Mode.ADD_STATION) {
            currentActionLabel.setText("Action: Add Station");
        } else if (gamePanel.getMode() == Mode.DRAW_LINE) {
            currentActionLabel.setText("Action: Draw Line");
        } else if (gamePanel.getMode() == Mode.ADD_NEW_METRO_LINE) {
            currentActionLabel.setText("Action: Add new metro line");
        } else if (gamePanel.getMode() == Mode.ADD_TRAIN) {
            currentActionLabel.setText("Action: Add Train");
        }
    }

    public void updateActiveMetro(GamePanel gamePanel) {
        int metroIndex = gamePanel.getMetroLineIndex() + 1;
        if (metroIndex == 0) {
            metroLineIndexLabel.setText("Current metro line: none");
        } else {
            metroLineIndexLabel.setText("Current metro line: " + metroIndex);
        }
    }

    public void updateStationCount(GamePanel gamePanel) {
        int count = gamePanel.CountStations();
        stationCountLabel.setText("Stations: " + count);
    }
}
