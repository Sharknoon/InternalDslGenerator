package de.sharknoon.internal_dsl_generator.views.help;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.sharknoon.internal_dsl_generator.views.main.MainView;

@Route(value = "help", layout = MainView.class)
@PageTitle("Help")
@CssImport("styles/views/help/help-view.css")
public class HelpView extends Div {

    public HelpView() {
        setId("help-view");
        add(new Label("Content placeholder"));
    }

}
