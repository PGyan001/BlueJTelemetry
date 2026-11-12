package org.example.telemetry.extension;

import bluej.extensions2.BPackage;
import bluej.extensions2.MenuGenerator;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuItem;
import org.example.telemetry.export.SessionExporter;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

class TelemetryMenuGenerator extends MenuGenerator {

    private final SessionExporter exporter;

    TelemetryMenuGenerator(SessionExporter exporter) {
        this.exporter = exporter;
    }

    @Override
    public MenuItem getToolsMenuItem(BPackage aPackage) {
        MenuItem item = new MenuItem("Export Telemetry (JSON + PDF)");
        item.setOnAction(e -> {
            try {
                List<Path> files = exporter.exportAll();
                String paths = files.stream().map(Path::toString).collect(Collectors.joining("\n"));
                new Alert(Alert.AlertType.INFORMATION, "Exported:\n" + paths).showAndWait();
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, "Export failed: " + ex.getMessage()).showAndWait();
            }
