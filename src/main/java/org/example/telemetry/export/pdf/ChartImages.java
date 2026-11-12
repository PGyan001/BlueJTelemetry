package org.example.telemetry.export.pdf;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Map;

final class ChartImages {

    private ChartImages() {}

    @SuppressWarnings({"rawtypes", "unchecked"})
    static byte[] activityPie(Duration active, Duration idle) throws IOException {
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("Active", (double) active.toSeconds());
        dataset.setValue("Idle", (double) idle.toSeconds());
        return toPng(ChartFactory.createPieChart("Active vs idle time", dataset, true, false, false));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    static byte[] errorBars(Map<String, Long> errors) throws IOException {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        errors.forEach((category, count) -> dataset.addValue(count, "Errors", category));
