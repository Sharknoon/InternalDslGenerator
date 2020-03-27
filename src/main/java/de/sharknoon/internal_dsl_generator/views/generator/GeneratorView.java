package de.sharknoon.internal_dsl_generator.views.generator;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Route(value = "generator", layout = MainView.class)
@RouteAlias(value = "", layout = MainView.class)
@PageTitle("Generator")
@CssImport("./styles/views/generator/generator-view.css")
public class GeneratorView extends VerticalLayout {

    private final GeneratorService service;

    public GeneratorView(@Autowired GeneratorService service) {
        this.service = service;
        setId("generator-view");
        setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.STRETCH);
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
    private String graph = null;

    private Component[] getUploadComponents() {
        //Textfield for the package name
        TextField packageNameField = new TextField();
        packageNameField.setPlaceholder(DEFAULT_PACKAGE_NAME);
        packageNameField.setLabel("Package Name");
        packageNameField.setRequired(true);

        //Editor section for the grammar
        TextArea grammarArea = new TextArea();
        grammarArea.setPlaceholder(DEFAULT_BNF);
        grammarArea.setLabel("Grammar");
        grammarArea.setRequired(true);
        grammarArea.setWidthFull();

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

        //Fill the Text Area with the uploaded File
        upload.addSucceededListener(event -> {
            MemoryBuffer buffer = (MemoryBuffer) event.getUpload().getReceiver();
            InputStream inputStream = buffer.getInputStream();
            String grammar = new BufferedReader(new InputStreamReader(inputStream)).lines()
                    .parallel().collect(Collectors.joining("\n"));
            grammarArea.setValue(grammar);
        });

        //Checkbox for including the DOT-Graph
        Checkbox includeDOTGraphCheckbox = new Checkbox();
        includeDOTGraphCheckbox.setLabel("Include DOT Graph");

        // Start Button
        Button startButton = new Button("Generate");
        startButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        //Download Button
        Button downloadButton = new Button("Download");
        downloadButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        downloadButton.setVisible(false);

        //Anchor for the download button
        Anchor downloadAnchor = new Anchor();
        downloadAnchor.getElement().setAttribute("download", true);
        downloadAnchor.add(downloadButton);

        //Sample Button
        Button showSampleButton = new Button("Show Sample");
        showSampleButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        showSampleButton.addClickListener(event -> {
            packageNameField.setValue(DEFAULT_PACKAGE_NAME);
            grammarArea.setValue(DEFAULT_BNF);
            includeDOTGraphCheckbox.setValue(true);
        });

        //Button Layout
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(false);
        buttonLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
        buttonLayout.add(startButton, downloadAnchor, showSampleButton);

        IFrame graphIFrame = new IFrame();
        graphIFrame.setWidthFull();
        graphIFrame.setHeight("0px");
        graphIFrame.getElement().setAttribute("frameBorder", "0");

        downloadButton.addClickListener(e -> {
            downloadButton.setVisible(false);
            startButton.setVisible(true);
            showSampleButton.setVisible(true);
            packageNameField.clear();
            grammarArea.clear();
            graphIFrame.setSrc("");
            //Resetting the uploaded File
            upload.setReceiver(new MemoryBuffer());
            upload.getElement().setPropertyJson("files", Json.createArray());
            graph = null;
            graphIFrame.setHeight("0px");
            includeDOTGraphCheckbox.setValue(false);
        });

        startButton.addClickListener(event -> {
            String grammar = grammarArea.getValue();
            String grammarName = ((MemoryBuffer) upload.getReceiver()).getFileName();
            String packageName = packageNameField.getValue();
            boolean includeDOTGraph = includeDOTGraphCheckbox.getValue();
            if (!validateInput(packageName, grammar)) {
                return;
            }
            try {
                StreamResource resource = service.generate(
                        new Project(
                                grammar,
                                grammarName,
                                packageName,
                                includeDOTGraph
                        ),
                        s -> graph = s
                );
                downloadAnchor.setHref(resource);
                startButton.setVisible(false);
                showSampleButton.setVisible(false);
                downloadButton.setVisible(true);
                //Show the graph
                if (graph != null) {
                    String encoded = URLEncoder.encode(graph, StandardCharsets.UTF_8);
                    String prefix = "https://dreampuf.github.io/GraphvizOnline/#";
                    String url = prefix + encoded;
                    //strange but blanks are nor encoded
                    String specialUrl = url.replace('+',' ');
                    //getUI().ifPresent(ui -> ui.getPage().open(specialUrl));
                    graphIFrame.setSrc(specialUrl);
                    graphIFrame.setHeight("750px");
                }
            } catch (Exception e) {
                e.printStackTrace();
                showErrorNotification("Error during Generation " + e);
            }
        });

        return new Component[]{
                new Div(packageNameField),
                new Div(grammarArea),
                upload,
                new Div(includeDOTGraphCheckbox),
                buttonLayout,
                graphIFrame
        };
    }

    private boolean validateInput(String packageName, String grammar) {
        if (packageName.isEmpty()) {
            showErrorNotification("Please enter a package name");
            return false;
        } else if (grammar.isEmpty()) {
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

    private void openGraph(String graph) {

    }

    private final String DEFAULT_PACKAGE_NAME = "de.etgramli";

    private final String DEFAULT_BNF = "<joi>\n" +
            "    = <component>\n" +
            "    ;\n" +
            "\n" +
            "<component>\n" +
            "    = ('component' | 'singleton') <componentName>\n" +
            "    <componentInterface> {<componentInterface>}\n" +
            "    <componentMethod> {<componentMethod>}\n" +
            "    {<componentField>}\n" +
            "    ;\n" +
            "\n" +
            "<componentName>\n" +
            "    = String\n" +
            "    ;\n" +
            "\n" +
            "<componentInterface>\n" +
            "    = 'impl' String\n" +
            "    ;\n" +
            "\n" +
            "<componentMethod>\n" +
            "    = 'method' String\n" +
            "    ;\n" +
            "\n" +
            "<componentField>\n" +
            "    = 'field' String\n" +
            "    ;";

}
