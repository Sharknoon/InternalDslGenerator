package de.sharknoon.internal_dsl_generator.views.generator;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import de.sharknoon.internal_dsl_generator.backend.GrammarFile;
import de.sharknoon.internal_dsl_generator.views.main.MainView;

@Route(value = "generator", layout = MainView.class)
@RouteAlias(value = "", layout = MainView.class)
@PageTitle("Generator")
@CssImport("styles/views/generator/generator-view.css")
public class GeneratorView extends VerticalLayout {

    //State state of this view
    private GeneratorState state = GeneratorState.UPLOAD;
    //The components of this view, to toggle visibility
    private Component upload;
    private Component processing;
    private Details usage;


    public GeneratorView() {
        setId("generator-view");
        addComponents();
        changeState(GeneratorState.UPLOAD);
    }

    private void addComponents() {
        //Title
        Component title = getTitleComponent();
        add(title);

        //Adding the Upload Component to the left side
        upload = getUploadComponent();
        add(upload);

        //Also the Calculating Components
        processing = getCalculatingComponent();
        add(processing);

        //Adding the help Component to the right side
        usage = getUsageComponent();
        add(usage);
    }

    private Component getTitleComponent() {
        //A little Title informing the user about the Generator
        H1 title = new H1();
        title.setText("Java Internal DSL Generator");
        title.setWidth(null);
        setHorizontalComponentAlignment(Alignment.CENTER, title);
        return title;
    }

    private Upload getUploadComponent() {
        //Buffer for the File
        MemoryBuffer buffer = new MemoryBuffer();

        Upload upload = new Upload(buffer);
        //Only accept EBNF and BNF Grammar Files
        upload.setAcceptedFileTypes(".bnf", ".ebnf");
        //Only allow one File to be uploaded
        upload.setMaxFiles(1);
        //Only allow 5 MB file sizes
        upload.setMaxFileSize(5 * 1024 * 1024);

        //Customizing the Upload Button, Optional
        Button uploadButton = new Button("Upload Grammar File");
        upload.setUploadButton(uploadButton);

        //Customizing the Drag Upload Text, Optional
        Span dropLabel = new Span("Drop grammar file here");
        upload.setDropLabel(dropLabel);

        //Adding Succeed Listener
        upload.addSucceededListener(event -> {
            //Making a bean of the uploaded grammar file
            GrammarFile uploadedFile = new GrammarFile(
                    event.getFileName(),
                    event.getContentLength(),
                    buffer
            );
            //Changing the state
            changeState(GeneratorState.PROCESSING);
        });

        //Adding File Rejection Listener
        upload.addFileRejectedListener(event -> {
            Notification errorNotification = new Notification();
            errorNotification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            errorNotification.setText(event.getErrorMessage());
            errorNotification.open();
        });

        return upload;
    }

    private Details getUsageComponent() {
        //A little help text
        Details component = new Details();
        component.setSummaryText("Usage");
        component.addThemeVariants(DetailsVariant.REVERSE, DetailsVariant.FILLED);
        return component;
    }

    private void updateUsageComponent(GeneratorState newState){
        usage.setContent(new Text(getUsageString(newState)));
    }

    private String getUsageString(GeneratorState state) {
        //Returns the usage string depending on the state of the Generator
        switch (state) {
            case UPLOAD:
                return "Upload a .ebnf or .bnf grammar File. This file will be checked " +
                        "for syntax and if everything looks great, the generator starts working.";
            case PROCESSING:
                return "Please wait while your new library is being generated";
            case FINISHED:
                return "Congrats! Your new library is generated! Download it now.";
            default:
                return "Can't happen";
        }
    }

    private Component getCalculatingComponent() {
        //Making a Wrapping Layout
        VerticalLayout layout = new VerticalLayout();

        //A Progress Bar to indicate generating
        ProgressBar progressBar = new ProgressBar();
        progressBar.setIndeterminate(true);
        layout.add(progressBar);

        //A Label indicating the Generation
        Span generatingLabel = new Span("Generating Java Files...");
        layout.add(generatingLabel);

        return layout;
    }

    private void changeState(GeneratorState newState) {
        switch (newState) {
            case UPLOAD:
                upload.setVisible(true);
                processing.setVisible(false);
                updateUsageComponent(GeneratorState.UPLOAD);
                break;
            case PROCESSING:
                upload.setVisible(false);
                processing.setVisible(true);
                updateUsageComponent(GeneratorState.PROCESSING);
                break;
            case FINISHED:
                upload.setVisible(false);
                processing.setVisible(false);
                updateUsageComponent(GeneratorState.FINISHED);
                break;
        }
    }

}
