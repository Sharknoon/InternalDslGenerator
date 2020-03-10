package de.sharknoon.internal_dsl_generator.views.generator;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.StreamResource;
import de.sharknoon.internal_dsl_generator.backend.GeneratorService;
import de.sharknoon.internal_dsl_generator.views.main.MainView;
import elemental.json.Json;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "generator", layout = MainView.class)
@RouteAlias(value = "", layout = MainView.class)
@PageTitle("Generator")
@CssImport("./styles/views/generator/generator-view.css")
public class GeneratorView extends VerticalLayout {

    private GeneratorService service;

    public GeneratorView(@Autowired GeneratorService service) {
        this.service = service;
        setId("generator-view");
        addComponents();
    }

    private void addComponents() {
        //Title
        add(getTitleComponent());

        //Adding the Upload Components
        add(getUploadComponents());
    }

    private Component getTitleComponent() {
        //A little Title informing the user about the Generator
        H1 title = new H1();
        title.setText("Java Internal DSL Generator");
        title.setWidth(null);
        setHorizontalComponentAlignment(Alignment.CENTER, title);
        return title;
    }

    //Buffer for the File
    private final Upload upload = new Upload(new MemoryBuffer());

    private Component[] getUploadComponents() {
        //Textfield for the package name
        TextField packageNameField = new TextField();
        packageNameField.setLabel("Package Name");
        packageNameField.setPlaceholder("de.etgramlich");
        packageNameField.setRequired(true);

        //The upload Element for uploading the grammar file
        upload.setAcceptedFileTypes(".bnf", ".ebnf");
        upload.setMaxFiles(1);
        upload.setMaxFileSize(5 * 1024 * 1024);

        //Customizing the Upload Button, Optional
        Button uploadButton = new Button("Upload Grammar File");
        upload.setUploadButton(uploadButton);

        //Customizing the Drag Upload Text, Optional
        Span dropLabel = new Span("Drop grammar file here");
        upload.setDropLabel(dropLabel);

        //Adding File Rejection Listener
        upload.addFileRejectedListener(event -> {
            Notification errorNotification = new Notification();
            errorNotification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            errorNotification.setText(event.getErrorMessage());
            errorNotification.open();
        });

        // Start Button
        Button startButton = new Button("Generate");
        startButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        //Download Button
        Button downloadButton = new Button("Download");
        downloadButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        downloadButton.getStyle().set("margin-top", "var(--lumo-space-m)");
        downloadButton.setVisible(false);
        downloadButton.addClickListener(e -> {
            downloadButton.setVisible(false);
            startButton.setVisible(true);
            packageNameField.clear();
            //Resetting the uploaded File
            upload.setReceiver(new MemoryBuffer());
            upload.getElement().setPropertyJson("files", Json.createArray());
        });

        //Anchor for the download button
        Anchor downloadAnchor = new Anchor();
        downloadAnchor.getElement().setAttribute("download", true);
        downloadAnchor.add(downloadButton);

        startButton.addClickListener(event -> {
            String packageNameFieldValue = packageNameField.getValue();
            if (!validateInput(packageNameFieldValue, (MemoryBuffer) upload.getReceiver())) {
                return;
            }
            try {
                StreamResource resource = service.generate((MemoryBuffer) upload.getReceiver(), packageNameFieldValue);
                downloadAnchor.setHref(resource);
                startButton.setVisible(false);
                downloadButton.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                showErrorNotification("Error during Generation " + e);
            }
        });
        return new Component[]{packageNameField, upload, startButton, downloadAnchor};
    }

    private boolean validateInput(String packageName, MemoryBuffer buffer) {
        if (packageName.isEmpty()) {
            showErrorNotification("Please enter a package name");
            return false;
        } else if (buffer.getFileData() == null) {
            showErrorNotification("Please upload a grammar file");
            return false;
        }
        return true;
    }

    private void showErrorNotification(String s) {
        Notification notification = new Notification(s);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        notification.setDuration(1000 * 5);
        notification.open();
    }

}
