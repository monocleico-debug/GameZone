package com.gamezone.repository;

import com.gamezone.model.VideoGame;
import com.gamezone.util.JsonUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class VideoGameRepository {

    private static final String FILE_PATH =
            System.getProperty("user.dir") + "/data/videogames.json";
    private List<VideoGame> catalog;

    public VideoGameRepository() {
        catalog = JsonUtil.loadVideoGames(FILE_PATH);
        if (catalog == null) catalog = new ArrayList<>();
    }

    private void persist() {
        try {
            JsonUtil.saveVideoGames(catalog, FILE_PATH);
        } catch (IOException e) {
            throw new RuntimeException(
                    "Error en guardar el catalogo: " + e.getMessage());
        }
    }

    // CREAR
    public void create(VideoGame vg) {
        boolean exists = catalog.stream()
                .anyMatch(g -> g.getTitle()
                        .equalsIgnoreCase(vg.getTitle()));
        if (exists) {
            throw new IllegalArgumentException(
                    "El videojuego ya existe en el catálogo");
        }
        catalog.add(vg);
        persist();
    }

    // LEER TODO
    public List<VideoGame> findAll() {
        return new ArrayList<>(catalog);
    }

    // LEER POR TITULO
    public VideoGame findByTitle(String title) {
        return catalog.stream()
                .filter(g -> g.getTitle().equalsIgnoreCase(title))
                .findFirst()
                .orElse(null);
    }

    // LEER POR PLATAFORMA
    public List<VideoGame> findByPlatform(String platform) {
        return catalog.stream()
                .filter(g -> g.getPlatform().equalsIgnoreCase(platform))
                .collect(Collectors.toList());
    }

    // ACTUALIZAR
    public boolean update(String title, VideoGame newVg) {
        for (int i = 0; i < catalog.size(); i++) {
            if (catalog.get(i).getTitle().equalsIgnoreCase(title)) {
                catalog.set(i, newVg);
                persist();
                return true;
            }
        }
        return false;
    }

    // ELIMINAR
    public boolean delete(String title) {
        boolean removed = catalog.removeIf(
                g -> g.getTitle().equalsIgnoreCase(title));
        if (removed) persist();
        return removed;
    }
}