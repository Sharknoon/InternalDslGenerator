package de.sharknoon.internal_dsl_generator.views.about

import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import de.sharknoon.internal_dsl_generator.views.main.MainView

@Route(value = "about", layout = MainView::class)
@PageTitle("About")
@CssImport("./styles/views/about/about-view.css")
class AboutView : Div() {
    init {
        setId("about-view")
        add(Label("Content placeholder"))
    }
}