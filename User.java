package com.CityMetro;

public class User {
    private String name;
    private String city;

    // Konstruktor
    public User() {
        this.name = "Default User";
        this.city = "Warsaw";
    }

    // Konstruktor
    public User(String name) {
        this.name = name;
    }

    // Getter dla imienia
    public String getName() {
        return name;
    }

    // Setter dla imienia
    public void setName(String name) {
        this.name = name;
    }

    // Getter dla miasta
    public String getCity() {
        return city;
    }

    // Setter dla miasta
    public void setCity(String city) {
        this.city = city;
    }
}