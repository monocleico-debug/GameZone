package com.gamezone.service;

import com.gamezone.model.*;
import com.gamezone.repository.VideoGameRepository;
import com.gamezone.util.JsonUtil;
import com.gamezone.util.JsonUtil.SaleRecord;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class VideoGameService {

    private static final String SALES_FILE =
            System.getProperty("user.dir") + "/data/sales.json";
    private final VideoGameRepository repo;
    private final List<SaleRecord> sales;

    public VideoGameService() {
        repo  = new VideoGameRepository();
        sales = JsonUtil.loadSales(SALES_FILE);
    }

    // añadir con validaciones
    public void addVideoGame(VideoGame vg) {
        StringBuilder errors = new StringBuilder();

        if (vg.getTitle() == null || vg.getTitle().isBlank()) {
            errors.append("• El título no puede estar vacío.\n");
        }
        if (vg.getPrice() <= 0) {
            errors.append("• El precio debe ser mayor a 0.\n");
        }
        if (vg.getStock() < 0) {
            errors.append("• El stock debe ser mayor o igual a 0.\n");
        }

        if (errors.length() > 0) {
            throw new IllegalArgumentException(errors.toString());
        }

        repo.create(vg); // lanza "El videojuego ya existe en el catálogo"
    }

    // Lista
    public List<VideoGame> getAllVideoGames() {
        return repo.findAll();
    }

    // por titulo
    public VideoGame searchByTitle(String title) {
        return repo.findByTitle(title);
    }

    // por plataforma
    public List<VideoGame> searchByPlatform(String platform) {
        return repo.findByPlatform(platform);
    }

    // actualizar
    public boolean updateVideoGame(String title, VideoGame newVg) {
        return repo.update(title, newVg);
    }

    // eliminar
    public boolean deleteVideoGame(String title) {
        return repo.delete(title);
    }

    // vender
    public double sellVideoGame(String title, int qty) {
        VideoGame vg = repo.findByTitle(title);
        if (vg == null) {
            throw new IllegalArgumentException(
                    "El juego '" + title + "' no se encontro en el catalogo");
        }
        if (vg.getStock() < qty) {
            throw new IllegalStateException(
                    "Stock insuficiente, Disponible: " + vg.getStock());
        }

        vg.setStock(vg.getStock() - qty);
        double unitPrice = vg.calculateFinalPrice();
        double total     = unitPrice * qty;

        SaleRecord sr = new SaleRecord();
        sr.id        = UUID.randomUUID().toString().substring(0, 8);
        sr.gameTitle = vg.getTitle();
        sr.quantity  = qty;
        sr.unitPrice = unitPrice;
        sr.total     = total;
        sr.saleDate  = LocalDateTime.now()
                .format(DateTimeFormatter
                        .ofPattern("yyyy-MM-dd HH:mm"));
        sales.add(sr);

        repo.update(title, vg);

        try {
            JsonUtil.saveSales(sales, SALES_FILE);
        } catch (IOException e) {
            throw new RuntimeException(
                    "Error en guardar venta: " + e.getMessage());
        }
        return total;
    }

    // Ventas
    public List<SaleRecord> getAllSales() {
        return new ArrayList<>(sales);
    }
}