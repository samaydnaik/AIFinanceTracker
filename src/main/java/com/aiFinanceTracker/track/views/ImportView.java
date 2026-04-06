package com.aiFinanceTracker.track.views;

import java.io.InputStream;

import com.aiFinanceTracker.track.service.StatementImportService;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "import")
@PageTitle("Import Statements")
public class ImportView extends VerticalLayout {

    private final StatementImportService importService;

    public ImportView(StatementImportService importService) {
        this.importService = importService;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        H2 title = new H2("Import SBI Statement (CSV)");

        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setAcceptedFileTypes(".csv");
        upload.setMaxFiles(1);
        upload.setDropLabel(new Span("Drop SBI CSV here or click to upload"));

        upload.addSucceededListener(event -> {
            String fileName = event.getFileName();
            try (InputStream in = buffer.getInputStream()) {
                importService.importSbiCsv(in);
                Notification.show("Imported " + fileName + " successfully",
                        3000, Notification.Position.MIDDLE);
            } catch (Exception e) {
                Notification.show("Import failed: " + e.getMessage(),
                        5000, Notification.Position.MIDDLE);
            }
        });

        upload.addFileRejectedListener(event ->
                Notification.show("File rejected: " + event.getErrorMessage(),
                        5000, Notification.Position.MIDDLE));

        upload.addFailedListener(event ->
                Notification.show("Upload failed: " + event.getReason().getMessage(),
                        5000, Notification.Position.MIDDLE));

        add(title, upload);
    }
}