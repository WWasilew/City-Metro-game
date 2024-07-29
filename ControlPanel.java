package com.CityMetro;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ControlPanel extends JPanel {
    private JButton addStationButton;
    private JButton drawLineButton;
    private JButton addNewMetroLineButton;

    public ControlPanel(GamePanel gamePanel) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        addStationButton = new JButton("Add Station");
        drawLineButton = new JButton("Draw Line");
        addNewMetroLineButton = new JButton("Add new metro line");

        addStationButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        drawLineButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        addNewMetroLineButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        add(addStationButton);
        add(Box.createRigidArea(new Dimension(0, 10))); // Przerwa między przyciskami
        add(drawLineButton);
        add(Box.createRigidArea(new Dimension(0, 10))); // Przerwa między przyciskami
        add(addNewMetroLineButton);

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
            //    gamePanel.addMetroLine();
            }
        });
    }
}
