package com.CityMetro;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CityMetroApplication {
    // Zmienne przechowująca dane użytkownika
    private static User logged = new User();
    private static CardLayout cardLayout;
    private static JPanel mainPanel;
    private static JFrame ApplicationFrame;

public static void main(String[] args) {
    // Stworzenie okna
    ApplicationFrame = new JFrame("City Metro Application");

    // Ustawienie działania zamknięcia okna
    ApplicationFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    // Ustawienie rozmiaru okna
    ApplicationFrame.setSize(1200, 900);

    // Ustawienie okna na środku ekranu
    ApplicationFrame.setLocationRelativeTo(null);

    // Tworzenie panelu menu gry
    gameMenu();

    // Ustawienie widoczności okna
    ApplicationFrame.setVisible(true);
}

public static void gameMenu() {
    // Tworzenie CardLayout i głównego panelu
    cardLayout = new CardLayout();
    mainPanel = new JPanel(cardLayout);

    // Dodanie paneli do głównego panelu
    mainPanel.add(loggingMenu(), "namePanel");

    // Dodanie głównego panelu do ramki
    ApplicationFrame.add(mainPanel);
    cardLayout.show(mainPanel, "namePanel"); // Pokaż panel wprowadzania imienia
}

public static JPanel loggingMenu() {
    // Tworzenie panelu do wprowadzania imienia
    JPanel namePanel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);

    JLabel nameLabel = new JLabel("Enter your name:");
    gbc.gridx = 0;
    gbc.gridy = 0;
    namePanel.add(nameLabel, gbc);

    JTextField nameField = new JTextField(15);
    gbc.gridx = 1;
    gbc.gridy = 0;
    namePanel.add(nameField, gbc);

    JButton submitButton = new JButton("Submit");
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.gridwidth = 2;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    namePanel.add(submitButton, gbc);

    // Dodanie akcji do przycisku potwierdzenia imienia
    submitButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            String userName = nameField.getText();
            if (userName.isEmpty()) {
                JOptionPane.showMessageDialog(ApplicationFrame, "Please enter your nickname", "Something went wrong", JOptionPane.ERROR_MESSAGE);
            } else {
                logged.setName(userName);
                mainPanel.add(chooseCityMenu(), "cityPanel");
                cardLayout.show(mainPanel, "cityPanel");
            }
        }
    });
    return namePanel;
}

public static JPanel chooseCityMenu() {
    // Tworzenie panelu menu wyboru miasta
    JPanel cityPanel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    JLabel cityLabel = new JLabel("Choose a city where you want to build your metro: ");
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 2;
    gbc.fill = GridBagConstraints.NONE;
    cityPanel.add(cityLabel, gbc);

    String[] cities = {"Warsaw", "Moscow", "Paris", "Berlin", "Madrid"};
    gbc.gridwidth = 1;
    gbc.gridy = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    for (int i = 0; i < cities.length; i++) {
        JButton cityButton = new JButton(cities[i]);
        gbc.gridx = 1;
        gbc.gridy = i + 1;
        cityPanel.add(cityButton, gbc);

        cityButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logged.setCity(cityButton.getText());
                JOptionPane.showMessageDialog(ApplicationFrame, "Time to build your metro in " + logged.getCity(), "Information", JOptionPane.INFORMATION_MESSAGE);
                RunGame();
            }
        });
    }

    // Dodanie napisu z nickiem użytkownika w prawym górnym rogu
    JLabel userLabel = new JLabel("Logged: " + logged.getName());
    gbc.gridx = 2;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.NORTHEAST;
    cityPanel.add(userLabel, gbc);

    return cityPanel;
}

private static void RunGame() {
    JFrame gameFrame = new JFrame("City metro Game: " + logged.getCity());
    gameFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    gameFrame.setSize(1200, 900);
    gameFrame.setLocationRelativeTo(null);
    GamePanel gamePanel = new GamePanel();
    ControlPanel controlPanel = new ControlPanel(gamePanel);

    gameFrame.setLayout(new BorderLayout());
    gameFrame.add(gamePanel, BorderLayout.CENTER);
    gameFrame.add(controlPanel, BorderLayout.WEST);

    gameFrame.setVisible(true);
}

}