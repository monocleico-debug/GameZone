package com.gamezone.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Sale {

    private String        id;
    private VideoGame     videoGame;
    private int           quantity;
    private double        unitPrice;
    private double        total;
    private LocalDateTime saleDate;

    public Sale(String id, VideoGame videoGame, int quantity, double unitPrice) {
        this.id = id;
        this.videoGame = videoGame;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.total = unitPrice * quantity;
        this.saleDate = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public VideoGame getVideoGame() {
        return videoGame;
    }

    public void setVideoGame(VideoGame videoGame) {
        this.videoGame = videoGame;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public LocalDateTime getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(LocalDateTime saleDate) {
        this.saleDate = saleDate;
    }

    @Override
    public String toString() {
        DateTimeFormatter fmt =
                DateTimeFormatter.ofPattern("yyyy-mm-dd hh:mm");
        return String.format(
                "Sale[id=%s, juego=%s, Cantidad=%d, Precio unidad=%.2f, " +
                        "total=%.2f, Fecha=%s]",
                id, videoGame.getTitle(), quantity,
                unitPrice, total, saleDate.format(fmt));
    }
}
