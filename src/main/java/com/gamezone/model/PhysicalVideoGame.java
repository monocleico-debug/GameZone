package com.gamezone.model;

import com.gamezone.interfaces.Sellable;
import com.gamezone.interfaces.Displayable;

public class PhysicalVideoGame extends VideoGame
        implements Sellable, Displayable {

    private String condition;
    private String distributor;

    public PhysicalVideoGame(String title, double price, String platform, int stock,
                             String genre, String condition, String distributor) {
        super(title, price, platform, stock, genre);
        this.condition = condition;
        this.distributor = distributor;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getDistributor() {
        return distributor;
    }

    public void setDistributor(String distributor) {
        this.distributor = distributor;
    }

    @Override
    public double calculateFinalPrice() {
        return "usado".equalsIgnoreCase(condition) ? price * 0.75 : price;
    }

    @Override
    public double sell(int qty) {
        this.stock -= qty;
        return calculateFinalPrice() * qty;
    }

    @Override
    public String getDisplayInfo() {
        return String.format(
                "[Fisico] %s | Platforma: %s | Precio final: $%.2f | " +
                        "Stock: %d | Condicion: %s | Distribuidor: %s",
                title, platform, calculateFinalPrice(),
                stock, condition, distributor);
    }

    @Override
    public Object[] toTableRow() {
        return new Object[]{
                title, platform, genre,
                String.format("$%.2f", calculateFinalPrice()),
                stock, condition, "Fisico", distributor
        };
    }

    @Override
    public String toString() {
        return "Videojuego fisico{" +
                "condicion='" + condition + '\'' +
                ", distribuidor='" + distributor + '\'' +
                ", titulo='" + title + '\'' +
                ", precio=" + price +
                ", plataforma='" + platform + '\'' +
                ", stock=" + stock +
                ", genero='" + genre + '\'' +
                '}';
    }
}
