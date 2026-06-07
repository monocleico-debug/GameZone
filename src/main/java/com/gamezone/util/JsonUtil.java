package com.gamezone.util;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.gamezone.model.*;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class JsonUtil {

    private static final Gson GSON = new GsonBuilder()
            .registerTypeHierarchyAdapter(VideoGame.class, new VideoGameAdapter())
            .setPrettyPrinting()
            .create();

    public static void saveVideoGames(List<VideoGame> list,
                                      String filePath) throws IOException {
        Files.createDirectories(Paths.get(filePath).getParent());
        try (Writer w = new FileWriter(filePath)) {
            GSON.toJson(list, w);
        }
    }

    public static List<VideoGame> loadVideoGames(String filePath) {
        File f = new File(filePath);
        if (!f.exists()) return new ArrayList<>();
        try (Reader r = new FileReader(f)) {
            Type listType = new TypeToken<List<VideoGame>>(){}.getType();
            List<VideoGame> result = GSON.fromJson(r, listType);
            if (result == null) return new ArrayList<>();
            result.removeIf(g -> g == null);
            return result;
        } catch (Exception e) {
            System.out.println("Error al cargar " + e.getMessage());
            return new ArrayList<>();
        }
    }


    public static void saveSales(List<SaleRecord> list,
                                 String filePath) throws IOException {
        Files.createDirectories(Paths.get(filePath).getParent());
        try (Writer w = new FileWriter(filePath)) {
            new GsonBuilder().setPrettyPrinting().create().toJson(list, w);
        }
    }

    public static List<SaleRecord> loadSales(String filePath) {
        File f = new File(filePath);
        if (!f.exists()) return new ArrayList<>();
        try (Reader r = new FileReader(f)) {
            Type t = new TypeToken<List<SaleRecord>>(){}.getType();
            List<SaleRecord> result = new Gson().fromJson(r, t);
            return result != null ? result : new ArrayList<>();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public static class SaleRecord {
        public String id;
        public String gameTitle;
        public String saleDate;
        public int    quantity;
        public double unitPrice;
        public double total;
    }


    static class VideoGameAdapter
            implements JsonSerializer<VideoGame>,
            JsonDeserializer<VideoGame> {

        @Override
        public JsonElement serialize(VideoGame src, Type typeOfSrc,
                                     JsonSerializationContext context) {
            JsonObject obj = new JsonObject();

            obj.addProperty("tipo",
                    src instanceof DigitalVideoGame ? "DIGITAL" : "FISICA");

            obj.addProperty("titulo",    src.getTitle());
            obj.addProperty("precio",    src.getPrice());
            obj.addProperty("plataforma", src.getPlatform());
            obj.addProperty("stock",    src.getStock());
            obj.addProperty("genero",    src.getGenre());

            if (src instanceof DigitalVideoGame dg) {
                obj.addProperty("Tamaño GB",           dg.getSizeGB());
                obj.addProperty("Plataforma de descarga", dg.getDownloadPlatform());
            } else if (src instanceof PhysicalVideoGame pg) {
                obj.addProperty("condicion",   pg.getCondition());
                obj.addProperty("distribuidor", pg.getDistributor());
            }

            return obj;
        }

        @Override
        public VideoGame deserialize(JsonElement json, Type typeOfT,
                                     JsonDeserializationContext context)
                throws JsonParseException {

            JsonObject obj = json.getAsJsonObject();

            if (!obj.has("tipo") || obj.get("tipo").isJsonNull()) {
                return null;
            }

            String type     = obj.get("tipo").getAsString();
            String title    = obj.get("titulo").getAsString();
            double price    = obj.get("precio").getAsDouble();
            String platform = obj.get("plataforma").getAsString();
            int    stock    = obj.get("stock").getAsInt();
            String genre    = obj.get("genero").getAsString();

            if ("DIGITAL".equals(type)) {
                double sizeGB           = obj.get("Tamaño GB").getAsDouble();
                String downloadPlatform = obj.get("Plataforma de descarga").getAsString();
                return new DigitalVideoGame(title, price, platform,
                        stock, genre,
                        sizeGB, downloadPlatform);
            } else {
                String condition   = obj.get("condicion").getAsString();
                String distributor = obj.get("distribuidor").getAsString();
                return new PhysicalVideoGame(title, price, platform,
                        stock, genre,
                        condition, distributor);
            }
        }
    }
}
