package com.gamezone.model;

import com.gamezone.interfaces.Sellable;
import com.gamezone.interfaces.Displayable;

public class DigitalVideoGame extends VideoGame
        implements Sellable, Displayable {

    private double sizeGB;
    private String downloadPlatform;

    public DigitalVideoGame(String title, double price, String platform, int stock,
                            String genre, double sizeGB, String downloadPlatform) {
        super(title, price, platform, stock, genre);
        this.sizeGB = sizeGB;
        this.downloadPlatform = downloadPlatform;
    }

    public double getSizeGB() {
        return sizeGB;
    }

    public void setSizeGB(double sizeGB) {
        this.sizeGB = sizeGB;
    }

    public String getDownloadPlatform() {
        return downloadPlatform;
    }

    public void setDownloadPlatform(String downloadPlatform) {
        this.downloadPlatform = downloadPlatform;
    }

    @Override
    public double calculateFinalPrice() {
        return sizeGB > 50 ? price + 5000 : price;
    }

    @Override
    public double sell(int qty) {
        this.stock -= qty;
        return calculateFinalPrice() * qty;
    }

    @Override
    public String getDisplayInfo() {
        return String.format(
                "[DIGITAL] %s | Platforma: %s | Precio final: $%.2f | " +
                        "Stock: %d | Tamaño: %.1f GB | Descarga: %s",
                title, platform, calculateFinalPrice(),
                stock, sizeGB, downloadPlatform);
    }

    @Override
    public Object[] toTableRow() {
        return new Object[]{
                title, platform, genre,
                String.format("$%.2f", calculateFinalPrice()),
                stock, sizeGB + " GB", "Digital", downloadPlatform
        };
    }

    @Override
    public String toString() {
        return "Videojuego digital{" +
                "Tamaño memoria (GB)=" + sizeGB +
                ", Plataforma de descarga='" + downloadPlatform + '\'' +
                ", titulo='" + title + '\'' +
                ", precio=" + price +
                ", platforma='" + platform + '\'' +
                ", stock=" + stock +
                ", genero='" + genre + '\'' +
                '}';
    }
}