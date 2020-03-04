package de.sharknoon.internal_dsl_generator.views.generator;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Label;
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

@Route(value = "generator", layout = MainView.class)
@RouteAlias(value = "", layout = MainView.class)
@PageTitle("Generator")
@CssImport("styles/views/generator/generator-view.css")
public class GeneratorView extends VerticalLayout {

    public GeneratorView() {
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
    MemoryBuffer memoryBuffer = new MemoryBuffer();

    private Component[] getUploadComponents() {
        // Start Button
        Button startButton = new Button("Generate");

        //Textfield for the package name
        TextField packageNameField = new TextField();
        packageNameField.setLabel("Package Name");
        packageNameField.setPlaceholder("de.etgramlich");
        packageNameField.setRequired(true);

        //The upload Element for uploading the grammar file
        Upload upload = new Upload(memoryBuffer);
        upload.setAcceptedFileTypes(".bnf", ".ebnf");
        upload.setMaxFiles(1);
        upload.setMaxFileSize(5 * 1024 * 1024);

        //Customizing the Upload Button, Optional
        Button uploadButton = new Button("Upload Grammar File");
        upload.setUploadButton(uploadButton);

        //Customizing the Drag Upload Text, Optional
        Span dropLabel = new Span("Drop grammar file here");
        upload.setDropLabel(dropLabel);

        //Adding Succeed Listener
        upload.addSucceededListener(event -> {
            if (!packageNameField.getValue().isEmpty()) {
                startButton.setEnabled(true);
            }
        });

        //Adding File Rejection Listener
        upload.addFileRejectedListener(event -> {
            Notification errorNotification = new Notification();
            errorNotification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            errorNotification.setText(event.getErrorMessage());
            errorNotification.open();
        });


        startButton.addClickListener(event -> {
            String packageNameFieldValue = packageNameField.getValue();
            if (!validateInput(packageNameFieldValue, memoryBuffer)) {
                return;
            }
            try {
                StreamResource resource = GeneratorService.generate(memoryBuffer, packageNameFieldValue);

                Label label = new Label("Congratulations, your generated File is ready to be downloaded :)");

                Button downloadButton = new Button("Download");
                Anchor downloadAnchor = new Anchor();
                downloadAnchor.setHref(resource);
                downloadAnchor.getElement().setAttribute("download", true);
                downloadAnchor.add(downloadButton);

                Dialog dialog = new Dialog();
                dialog.add(new VerticalLayout(label, downloadAnchor));
                dialog.setWidth("400px");
                dialog.setHeight("150px");
                dialog.open();
                dialog.addDialogCloseActionListener(event1 -> {
                    
                });

                downloadButton.addClickListener(e -> dialog.close());
            } catch (Exception e) {
                e.printStackTrace();
                Notification notification = new Notification("Error during Generation " + e);
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                notification.open();
            }
        });
        return new Component[]{packageNameField, upload, startButton};
    }

    private boolean validateInput(String packageName, MemoryBuffer buffer) {
        if (packageName.isEmpty()) {
            Notification n = new Notification("Please enter a package name");
            n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            n.open();
            return false;
        } else if (memoryBuffer.getFileData() == null) {
            Notification n = new Notification("Please upload a grammar file");
            n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            n.open();
            return false;
        }
        return true;
    }

}
