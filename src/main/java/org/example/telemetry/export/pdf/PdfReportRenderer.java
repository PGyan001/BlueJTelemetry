package org.example.telemetry.export.pdf;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.example.telemetry.aggregate.ActivityTime;
import org.example.telemetry.aggregate.CompileStats;
import org.example.telemetry.aggregate.DevelopmentTimeline;
import org.example.telemetry.aggregate.ReportData;
import org.example.telemetry.aggregate.SessionMetadata;
import org.example.telemetry.export.ReportRenderer;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class PdfReportRenderer implements ReportRenderer {

    private static final int MAX_TIMELINE_ROWS = 150;
    private static final DateTimeFormatter TIME =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

    private static final Font TITLE = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22);
    private static final Font H2 = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
    private static final Font BODY = FontFactory.getFont(FontFactory.HELVETICA, 10);
    private static final Font BOLD = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
    private static final Font NOTE = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 9);

    @Override
    public Path render(ReportData data, Path file) throws IOException {
        Path parent = file.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        try (OutputStream out = Files.newOutputStream(file)) {
            PdfWriter.getInstance(document, out);
            document.open();
            addSummary(document, data);
            addActivity(document, data);
            addErrors(document, data);
            addTimeline(document, data);
            document.close();
        } catch (DocumentException e) {
            throw new IOException("Could not build PDF report", e);
        }
        return file;
    }

    private void addSummary(Document doc, ReportData data) throws DocumentException {
        SessionMetadata s = data.session();
        CompileStats c = data.compile();

        doc.add(new Paragraph("BlueJ Telemetry Report", TITLE));
        doc.add(heading("Session summary"));

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        row(table, "Student ID", s.studentId());
        row(table, "Session ID", s.sessionId());
        row(table, "Session start", TIME.format(s.startTime()));
        row(table, "Session end", TIME.format(s.endTime()));
        row(table, "Session duration", format(Duration.ofSeconds(s.durationSeconds())));
        row(table, "Total events recorded", String.valueOf(s.totalEvents()));
        row(table, "Compilation attempts", String.valueOf(c.attempts()));
        row(table, "Successful compilations", String.valueOf(c.successes()));
        row(table, "Failed compilations", String.valueOf(c.failures()));
        row(table, "Compile success rate", String.format("%.1f%%", c.successRate() * 100));
        row(table, "Program runs observed", String.valueOf(data.programRuns()));
        row(table, "Runtime exceptions", String.valueOf(data.runtimeExceptions()));
        doc.add(table);
    }

    private void addActivity(Document doc, ReportData data) throws DocumentException, IOException {
        ActivityTime t = data.activity();
        doc.add(heading("Active vs idle time"));

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        row(table, "Active coding time", format(t.active()));
        row(table, "Idle time", format(t.idle()));
        row(table, "Total tracked", format(t.total()));
        doc.add(table);

        if (t.total().isZero()) {
            doc.add(note("Not enough events to compute activity time."));
        } else {
            addChart(doc, ChartImages.activityPie(t.active(), t.idle()));
        }
        doc.add(note("Approximation: BlueJ does not expose keystrokes, so a gap between events of at most "
                + data.idleThreshold().toSeconds() + " seconds counts as active time and longer gaps as idle."));
    }

    private void addErrors(Document doc, ReportData data) throws DocumentException, IOException {
        Map<String, Long> errors = data.errorFrequency();
        doc.add(heading("Compilation errors"));
        if (errors.isEmpty()) {
            doc.add(note("No compilation errors were recorded."));
            return;
        }
        PdfPTable table = new PdfPTable(new float[]{3, 1});
        table.setWidthPercentage(100);
        header(table, "Error category", "Count");
        errors.forEach((category, count) -> {
            table.addCell(cell(category, BODY));
            table.addCell(cell(String.valueOf(count), BODY));
        });
        doc.add(table);
        addChart(doc, ChartImages.errorBars(errors));
    }

    private void addTimeline(Document doc, ReportData data) throws DocumentException {
        List<DevelopmentTimeline.Entry> timeline = data.timeline();
        doc.add(heading("Class and method timeline"));
        if (timeline.isEmpty()) {
            doc.add(note("No class or method definitions were recorded."));
            return;
        }
        PdfPTable table = new PdfPTable(new float[]{2.2f, 1.6f, 1.6f, 3.6f});
        table.setWidthPercentage(100);
        table.setHeaderRows(1);
        header(table, "Time", "Event", "Class", "Detail");
        int shown = Math.min(timeline.size(), MAX_TIMELINE_ROWS);
        for (int i = 0; i < shown; i++) {
            DevelopmentTimeline.Entry e = timeline.get(i);
            table.addCell(cell(TIME.format(e.time()), BODY));
            table.addCell(cell(e.kind(), BODY));
            table.addCell(cell(e.className(), BODY));
            table.addCell(cell(e.detail(), BODY));
        }
        doc.add(table);
        if (timeline.size() > shown) {
            doc.add(note("Showing the first " + shown + " of " + timeline.size() + " entries."));
        }
    }

    private static void addChart(Document doc, byte[] png) throws DocumentException, IOException {
        Image image = Image.getInstance(png);
        image.scaleToFit(480, 300);
        image.setAlignment(Element.ALIGN_CENTER);
        doc.add(image);
    }

    private static Paragraph heading(String text) {
        Paragraph p = new Paragraph(text, H2);
        p.setSpacingBefore(16);
        p.setSpacingAfter(6);
        return p;
    }

    private static Paragraph note(String text) {
        Paragraph p = new Paragraph(text, NOTE);
        p.setSpacingBefore(4);
        return p;
    }

    private static void row(PdfPTable table, String label, String value) {
        table.addCell(cell(label, BOLD));
        table.addCell(cell(value, BODY));
    }

    private static void header(PdfPTable table, String... labels) {
        for (String label : labels) {
            PdfPCell c = cell(label, BOLD);
            c.setBackgroundColor(new java.awt.Color(230, 230, 230));
            table.addCell(c);
        }
    }

    private static PdfPCell cell(String text, Font font) {
        PdfPCell c = new PdfPCell(new Phrase(text == null ? "" : text, font));
        c.setPadding(4);
        return c;
    }

    static String format(Duration d) {
        long s = d.getSeconds();
        return String.format("%dh %02dm %02ds", s / 3600, (s % 3600) / 60, s % 60);
    }
}
