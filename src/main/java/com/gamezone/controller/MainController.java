package com.gamezone.controller;

import com.gamezone.model.*;
import com.gamezone.service.VideoGameService;
import com.gamezone.util.JsonUtil.SaleRecord;
import com.gamezone.interfaces.Displayable;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.*;

import java.util.List;

public class MainController extends Application {

    private final VideoGameService service = new VideoGameService();
    private Stage primaryStage;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        stage.setTitle("GameZone - Sistema de gestion");
        stage.setMinWidth(700);
        stage.setMinHeight(520);
        showMainMenu();
        stage.show();
    }

    // MENU

    private void showMainMenu() {
        VBox root = new VBox(12);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(35));
        root.setStyle("-fx-background-color: #1a1a2e;");

        Label title = new Label("GameZone - Sistema de gestion");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        title.setTextFill(Color.web("#e94560"));

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: #e94560;");

        String[] labels = {
                "1. Agregar videojuego (Crear, listar, Eliminar, Actualizar)",
                "2. Listar todos los videojuegos",
                "3. Buscar por título",
                "4. Buscar por plataforma",
                "5. Realizar venta",
                "6. Mostrar ventas",
                "7. Salir"
        };

        VBox buttons = new VBox(8);
        buttons.setAlignment(Pos.CENTER);
        for (int i = 0; i < labels.length; i++) {
            final int opt = i + 1;
            Button btn = menuButton(labels[i]);
            btn.setOnAction(e -> handleOption(opt));
            buttons.getChildren().add(btn);
        }

        root.getChildren().addAll(title, sep, buttons);
        primaryStage.setScene(new Scene(root, 700, 520));
    }

    private void handleOption(int opt) {
        switch (opt) {
            case 1 -> showCrudMenu();
            case 2 -> showAllGames();
            case 3 -> showSearchByTitle();
            case 4 -> showSearchByPlatform();
            case 5 -> showSellForm();
            case 6 -> showSalesHistory();
            case 7 -> primaryStage.close();
        }
    }

    // ═══════════════════════════════════════════════════════════
    // CRUD MENU
    // ═══════════════════════════════════════════════════════════
    private void showCrudMenu() {
        Stage s = modal("Sistema de gestion");

        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: #1a1a2e;");

        Label lbl = header("Sistema de gestion");

        Button btnAdd    = menuButton("Añadir videojuego");
        Button btnList   = menuButton("Lista videojuegos");
        Button btnUpdate = menuButton("Actualizar videjuego");
        Button btnDelete = menuButton("Eliminar videojuego");
        Button btnBack   = menuButton("Regresar");

        btnAdd.setOnAction(e    -> showAddForm(s));
        btnList.setOnAction(e   -> showAllGames());
        btnUpdate.setOnAction(e -> showUpdateSearch());
        btnDelete.setOnAction(e -> showDeleteForm());
        btnBack.setOnAction(e   -> s.close());

        root.getChildren().addAll(lbl, new Separator(),
                btnAdd, btnList, btnUpdate, btnDelete, btnBack);

        s.setScene(new Scene(root, 480, 380));
        s.show();
    }

    // Añadir
    private void showAddForm(Stage parent) {
        Stage s = modal("Añadir videojuego");

        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: #16213e;");

        VBox root = new VBox(8);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #16213e;");

        Label lbl = header("Añadir nuevo videojuego");

        TextField tfTitle    = input("Titulo");
        TextField tfPrice    = input("Precio base");
        TextField tfPlatform = input("Plataforma");
        TextField tfStock    = input("Stock");
        TextField tfGenre    = input("Genero");

        Label lblType = fieldLabel("Tipo:");
        ToggleGroup tg = new ToggleGroup();
        RadioButton rbDigital  = radio("Digital",  tg, true);
        RadioButton rbPhysical = radio("Fisico", tg, false);
        HBox typeBox = new HBox(16, rbDigital, rbPhysical);

        VBox digitalBox = new VBox(6);
        TextField tfSize   = input("Tamaño en GB");
        TextField tfDlPlat = input("Plataforma de descarga");
        digitalBox.getChildren().addAll(
                fieldLabel("Tamaño (GB):"), tfSize,
                fieldLabel("Plataforma de descarga:"), tfDlPlat);

        VBox physicalBox = new VBox(6);
        physicalBox.setVisible(false);
        physicalBox.setManaged(false);
        TextField tfCondition   = input("Condicion  (nuevo / usado)");
        TextField tfDistributor = input("Distribuidor");
        physicalBox.getChildren().addAll(
                fieldLabel("Condicion:"), tfCondition,
                fieldLabel("Distribuidor:"), tfDistributor);

        tg.selectedToggleProperty().addListener((obs, o, n) -> {
            boolean isDigital = n == rbDigital;
            digitalBox.setVisible(isDigital);
            digitalBox.setManaged(isDigital);
            physicalBox.setVisible(!isDigital);
            physicalBox.setManaged(!isDigital);
        });

        Button btnSave = actionButton("Guardar", "#4caf50");
        Label  lblMsg  = new Label();
        lblMsg.setTextFill(Color.web("#e94560"));

        btnSave.setOnAction(e -> {
            try {
                String title    = tfTitle.getText().trim();
                double price    = Double.parseDouble(tfPrice.getText().trim());
                String platform = tfPlatform.getText().trim();
                int    stock    = Integer.parseInt(tfStock.getText().trim());
                String genre    = tfGenre.getText().trim();

                VideoGame vg;
                if (rbDigital.isSelected()) {
                    double gb  = Double.parseDouble(tfSize.getText().trim());
                    String dlp = tfDlPlat.getText().trim();
                    vg = new DigitalVideoGame(title, price, platform,
                            stock, genre, gb, dlp);
                } else {
                    String cond = tfCondition.getText().trim();
                    String dist = tfDistributor.getText().trim();
                    vg = new PhysicalVideoGame(title, price, platform,
                            stock, genre, cond, dist);
                }
                service.addVideoGame(vg);
                alert(Alert.AlertType.INFORMATION,
                        "Felicidades" , "Videojuego añadido correctamente");
                s.close();

            } catch (NumberFormatException ex) {
                alert(Alert.AlertType.ERROR,
                        "Error de formato",
                        "Precio, stock y titulo deben ser  válidos.");
            } catch (IllegalArgumentException ex) {
                alert(Alert.AlertType.WARNING,
                        "Advertencia", ex.getMessage());
            }
        });

        root.getChildren().addAll(
                lbl, new Separator(),
                fieldLabel("Titulo:"),    tfTitle,
                fieldLabel("Precio:"),    tfPrice,
                fieldLabel("Plataforma:"), tfPlatform,
                fieldLabel("Stock:"),    tfStock,
                fieldLabel("Genero:"),    tfGenre,
                lblType, typeBox,
                digitalBox, physicalBox,
                btnSave, lblMsg);

        scroll.setContent(root);
        s.setScene(new Scene(scroll, 460, 580));
        s.show();
    }

    // lista
    private void showAllGames() {
        List<VideoGame> games = service.getAllVideoGames();
        if (games.isEmpty()) {
            alert(Alert.AlertType.INFORMATION,
                    "Catalogo vacio", "No hay videojuegos registrados");
            return;
        }

        Stage s = modal("Todos los videojuegos");
        VBox root = new VBox(10);
        root.setPadding(new Insets(15));
        root.setStyle("-fx-background-color: #1a1a2e;");

        Label lbl = header("Catalogo");
        TableView<VideoGame> table = buildGameTable();
        table.setItems(FXCollections.observableArrayList(games));

        root.getChildren().addAll(lbl, table);
        VBox.setVgrow(table, Priority.ALWAYS);
        s.setScene(new Scene(root, 820, 420));
        s.show();
    }

    //Buscar por titulo

    private void showSearchByTitle() {
        TextInputDialog dlg = new TextInputDialog();
        dlg.setTitle("Buscar por titulo");
        dlg.setHeaderText("Ingresa el titulo:");
        dlg.setContentText("Titulo:");
        dlg.showAndWait().ifPresent(title -> {
            VideoGame vg = service.searchByTitle(title);
            if (vg == null) {
                alert(Alert.AlertType.WARNING,
                        "No se encontro",
                        "No existe un videojuego con ese titulo");
            } else {
                alert(Alert.AlertType.INFORMATION,
                        "Juego encontrado",
                        vg instanceof Displayable d
                                ? d.getDisplayInfo() : vg.toString());
            }
        });
    }

    //Buscar por plataforma

    private void showSearchByPlatform() {
        TextInputDialog dlg = new TextInputDialog();
        dlg.setTitle("Buscar por plataforma");
        dlg.setHeaderText("Ingresa la plataforma (PC, PS5, Xbox):");
        dlg.setContentText("Plataforma:");
        dlg.showAndWait().ifPresent(platform -> {
            List<VideoGame> games = service.searchByPlatform(platform);
            if (games == null || games.isEmpty()) {
                alert(Alert.AlertType.WARNING,
                        "No encontrado",
                        "No se encontraron juegos para la plataforma: " + platform);
                return;
            }
            Stage s = modal("Resultados: " + platform);
            VBox root = new VBox(10);
            root.setPadding(new Insets(15));
            root.setStyle("-fx-background-color: #1a1a2e;");
            Label lbl = header("Juegos por: " + platform);
            TableView<VideoGame> table = buildGameTable();
            table.setItems(FXCollections.observableArrayList(games));
            root.getChildren().addAll(lbl, table);
            VBox.setVgrow(table, Priority.ALWAYS);
            s.setScene(new Scene(root, 820, 380));
            s.show();
        });
    }

    //Actualizar
    private void showUpdateSearch() {
        TextInputDialog dlg = new TextInputDialog();
        dlg.setTitle("Actualizar videojuego");
        dlg.setHeaderText("Ingresa el titulo del juego a actualizar:");
        dlg.setContentText("Titulo:");
        dlg.showAndWait().ifPresent(title -> {
            VideoGame existing = service.searchByTitle(title);
            if (existing == null) {
                alert(Alert.AlertType.WARNING,
                        "No se encontrp", "No existe juego con ese titulo");
            } else {
                showUpdateForm(existing);
            }
        });
    }

    private void showUpdateForm(VideoGame existing) {
        Stage s = modal("Actualizacion: " + existing.getTitle());

        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: #16213e;");

        VBox root = new VBox(8);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #16213e;");

        Label lbl = header("Videojuego actualizado");

        TextField tfTitle    = prefilled(existing.getTitle());
        TextField tfPrice    = prefilled(String.valueOf(existing.getPrice()));
        TextField tfPlatform = prefilled(existing.getPlatform());
        TextField tfStock    = prefilled(String.valueOf(existing.getStock()));
        TextField tfGenre    = prefilled(existing.getGenre());

        VBox extraFields = new VBox(6);

        if (existing instanceof DigitalVideoGame dg) {
            TextField tfSize = prefilled(String.valueOf(dg.getSizeGB()));
            TextField tfDlP  = prefilled(dg.getDownloadPlatform());
            extraFields.getChildren().addAll(
                    fieldLabel("Tamaño (GB):"),          tfSize,
                    fieldLabel("Plataforma descargado:"),  tfDlP);

            Button btnSave = actionButton("Cambios guardados", "#4caf50");
            btnSave.setOnAction(e -> {
                try {
                    DigitalVideoGame updated = new DigitalVideoGame(
                            tfTitle.getText().trim(),
                            Double.parseDouble(tfPrice.getText().trim()),
                            tfPlatform.getText().trim(),
                            Integer.parseInt(tfStock.getText().trim()),
                            tfGenre.getText().trim(),
                            Double.parseDouble(tfSize.getText().trim()),
                            tfDlP.getText().trim());
                    service.updateVideoGame(existing.getTitle(), updated);
                    alert(Alert.AlertType.INFORMATION,
                            "Felicidades!", "Videojuego actualizado correctamente.");
                    s.close();
                } catch (Exception ex) {
                    alert(Alert.AlertType.ERROR, "Error", ex.getMessage());
                }
            });
            extraFields.getChildren().add(btnSave);

        } else if (existing instanceof PhysicalVideoGame pg) {
            TextField tfCond = prefilled(pg.getCondition());
            TextField tfDist = prefilled(pg.getDistributor());
            extraFields.getChildren().addAll(
                    fieldLabel("Condicion:"),    tfCond,
                    fieldLabel("Distribuidor:"),  tfDist);

            Button btnSave = actionButton("Guardar cambios", "#4caf50");
            btnSave.setOnAction(e -> {
                try {
                    PhysicalVideoGame updated = new PhysicalVideoGame(
                            tfTitle.getText().trim(),
                            Double.parseDouble(tfPrice.getText().trim()),
                            tfPlatform.getText().trim(),
                            Integer.parseInt(tfStock.getText().trim()),
                            tfGenre.getText().trim(),
                            tfCond.getText().trim(),
                            tfDist.getText().trim());
                    service.updateVideoGame(existing.getTitle(), updated);
                    alert(Alert.AlertType.INFORMATION,
                            "Felicidades!", "Videojuego actualizado correctamente");
                    s.close();
                } catch (Exception ex) {
                    alert(Alert.AlertType.ERROR, "Error", ex.getMessage());
                }
            });
            extraFields.getChildren().add(btnSave);
        }

        root.getChildren().addAll(
                lbl, new Separator(),
                fieldLabel("Titulo:"),    tfTitle,
                fieldLabel("Precio:"),    tfPrice,
                fieldLabel("Plataforma:"), tfPlatform,
                fieldLabel("Stock:"),    tfStock,
                fieldLabel("Genero:"),    tfGenre,
                extraFields);

        scroll.setContent(root);
        s.setScene(new Scene(scroll, 460, 500));
        s.show();
    }

    // eliminar
    private void showDeleteForm() {
        TextInputDialog dlg = new TextInputDialog();
        dlg.setTitle("Eliminar videojuego");
        dlg.setHeaderText("Ingresa titulo a eliminar:");
        dlg.setContentText("Titulo:");
        dlg.showAndWait().ifPresent(title -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "Seguro que deseas eliminarlo '" + title + "'?",
                    ButtonType.YES, ButtonType.NO);
            confirm.setTitle("Eliminacion confirmada");
            confirm.showAndWait().ifPresent(btn -> {
                if (btn == ButtonType.YES) {
                    boolean ok = service.deleteVideoGame(title);
                    if (ok) {
                        alert(Alert.AlertType.INFORMATION,
                                "Eliminado", "Videojuego eliminado correctamente");
                    } else {
                        alert(Alert.AlertType.WARNING,
                                "No se encontro", "No existe videojuego con ese titulo");
                    }
                }
            });
        });
    }

    // Vender
    private void showSellForm() {
        Stage s = modal("Venta");

        VBox root = new VBox(10);
        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: #1a1a2e;");

        Label lbl = header("Vender videojuego");

        TextField tfTitle = input("Titulo");
        TextField tfQty   = input("Cantidad");

        Button btnSell = actionButton("Confirmar venta", "#e94560");
        Label  lblResult = new Label();
        lblResult.setTextFill(Color.web("#4caf50"));
        lblResult.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        btnSell.setOnAction(e -> {
            try {
                String title = tfTitle.getText().trim();
                int    qty   = Integer.parseInt(tfQty.getText().trim());
                double total = service.sellVideoGame(title, qty);
                lblResult.setText(
                        String.format("Venta confirmada!  Total: $%.2f", total));
                alert(Alert.AlertType.INFORMATION, "Venta completada",
                        String.format("Videojuego: %s%nCantidad: %d%nTotal: $%.2f",
                                title, qty, total));
            } catch (NumberFormatException ex) {
            alert(Alert.AlertType.ERROR,
                    "ERROR", "La cantidad debe ser un número entero");
        } catch (IllegalArgumentException ex) {
            alert(Alert.AlertType.WARNING,
                    "La venta no se pudo completar", ex.getMessage());
        } catch (IllegalStateException ex) {
            alert(Alert.AlertType.WARNING,
                    "La venta no se pudo completar", ex.getMessage());
        }
        });

        root.getChildren().addAll(
                lbl, new Separator(),
                fieldLabel("Titulo:"), tfTitle,
                fieldLabel("Cantidad:"),         tfQty,
                btnSell, lblResult);

        s.setScene(new Scene(root, 420, 300));
        s.show();
    }

    // Historial de ventas
    private void showSalesHistory() {
        List<SaleRecord> sales = service.getAllSales();
        if (sales.isEmpty()) {
            alert(Alert.AlertType.INFORMATION,
                    "No ventas", "No hay ventas registradas");
            return;
        }

        Stage s = modal("Historial");
        VBox root = new VBox(10);
        root.setPadding(new Insets(15));
        root.setStyle("-fx-background-color: #1a1a2e;");

        Label lbl = header("Historial de ventas");

        TableView<SaleRecord> table = new TableView<>();
        table.setStyle("-fx-background-color: #16213e;");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        table.getColumns().addAll(
                saleCol("ID",         r -> r.id),
                saleCol("Juego",       r -> r.gameTitle),
                saleCol("Cantidad",        r -> String.valueOf(r.quantity)),
                saleCol("Precio unidad", r -> String.format("$%.2f", r.unitPrice)),
                saleCol("Total",      r -> String.format("$%.2f", r.total)),
                saleCol("Fecha",       r -> r.saleDate)
        );
        table.setItems(FXCollections.observableArrayList(sales));

        double grandTotal = sales.stream()
                .mapToDouble(r -> r.total).sum();
        Label lblTotal = new Label(
                String.format("Total: $%.2f", grandTotal));
        lblTotal.setTextFill(Color.web("#4caf50"));
        lblTotal.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        root.getChildren().addAll(lbl, table, lblTotal);
        VBox.setVgrow(table, Priority.ALWAYS);
        s.setScene(new Scene(root, 750, 420));
        s.show();
    }

    private TableView<VideoGame> buildGameTable() {
        TableView<VideoGame> table = new TableView<>();
        table.setStyle("-fx-background-color: #16213e;");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<VideoGame, String> cTitle = new TableColumn<>("Titulo");
        cTitle.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getTitle()));

        TableColumn<VideoGame, String> cPlatform = new TableColumn<>("Plataforma");
        cPlatform.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getPlatform()));

        TableColumn<VideoGame, String> cGenre = new TableColumn<>("Genero");
        cGenre.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getGenre()));

        TableColumn<VideoGame, String> cStock = new TableColumn<>("Stock");
        cStock.setCellValueFactory(d ->
                new SimpleStringProperty(String.valueOf(d.getValue().getStock())));

        TableColumn<VideoGame, String> cPrice = new TableColumn<>("Precio final");
        cPrice.setCellValueFactory(d ->
                new SimpleStringProperty(
                        String.format("$%.2f", d.getValue().calculateFinalPrice())));

        TableColumn<VideoGame, String> cType = new TableColumn<>("Tipo");
        cType.setCellValueFactory(d ->
                new SimpleStringProperty(
                        d.getValue() instanceof DigitalVideoGame
                                ? "Digital" : "Fisico"));

        table.getColumns().addAll(
                cTitle, cPlatform, cGenre, cStock, cPrice, cType);
        return table;
    }

    private TableColumn<SaleRecord, String> saleCol(
            String name,
            java.util.function.Function<SaleRecord, String> mapper) {
        TableColumn<SaleRecord, String> col = new TableColumn<>(name);
        col.setCellValueFactory(d ->
                new SimpleStringProperty(mapper.apply(d.getValue())));
        return col;
    }

    private Stage modal(String title) {
        Stage s = new Stage();
        s.setTitle(title);
        s.initModality(Modality.APPLICATION_MODAL);
        return s;
    }

    private Label header(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        l.setTextFill(Color.web("#e94560"));
        return l;
    }

    private Label fieldLabel(String text) {
        Label l = new Label(text);
        l.setTextFill(Color.web("#cccccc"));
        l.setFont(Font.font("Arial", 13));
        return l;
    }

    private TextField input(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle(
                "-fx-background-color: #0f3460; " +
                        "-fx-text-fill: white; " +
                        "-fx-prompt-text-fill: #888888; " +
                        "-fx-border-color: #444466; " +
                        "-fx-border-radius: 4; " +
                        "-fx-background-radius: 4;");
        return tf;
    }

    private TextField prefilled(String value) {
        TextField tf = input("");
        tf.setText(value);
        return tf;
    }

    private RadioButton radio(String text, ToggleGroup tg, boolean selected) {
        RadioButton rb = new RadioButton(text);
        rb.setToggleGroup(tg);
        rb.setSelected(selected);
        rb.setStyle("-fx-text-fill: #cccccc;");
        return rb;
    }

    private Button menuButton(String text) {
        Button btn = new Button(text);
        btn.setMinWidth(340);
        btn.setMinHeight(38);
        btn.setStyle(
                "-fx-background-color: #16213e; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 13px; " +
                        "-fx-border-color: #e94560; " +
                        "-fx-border-radius: 5; " +
                        "-fx-background-radius: 5;");
        btn.setOnMouseEntered(e -> btn.setStyle(
                "-fx-background-color: #e94560; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 13px; " +
                        "-fx-border-radius: 5; " +
                        "-fx-background-radius: 5;"));
        btn.setOnMouseExited(e -> btn.setStyle(
                "-fx-background-color: #16213e; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 13px; " +
                        "-fx-border-color: #e94560; " +
                        "-fx-border-radius: 5; " +
                        "-fx-background-radius: 5;"));
        return btn;
    }

    private Button actionButton(String text, String color) {
        Button btn = new Button(text);
        btn.setStyle(
                "-fx-background-color: " + color + "; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 13px; " +
                        "-fx-padding: 8 22; " +
                        "-fx-border-radius: 5; " +
                        "-fx-background-radius: 5;");
        return btn;
    }

    private void alert(Alert.AlertType type,
                       String title, String message) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(message);
        a.showAndWait();
    }
}