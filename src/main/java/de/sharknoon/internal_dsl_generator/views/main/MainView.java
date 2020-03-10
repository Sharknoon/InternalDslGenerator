package de.sharknoon.internal_dsl_generator.views.main;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabVariant;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import de.sharknoon.internal_dsl_generator.views.about.AboutView;
import de.sharknoon.internal_dsl_generator.views.generator.GeneratorView;
import de.sharknoon.internal_dsl_generator.views.help.HelpView;

import java.util.Optional;

/**
 * The main view is a top-level placeholder for other views.
 */
@CssImport("./styles/shared-styles.css")
@CssImport(value = "./styles/views/main/main-view.css", themeFor = "vaadin-app-layout")
@Theme(value = Lumo.class, variant = Lumo.DARK)
public class MainView extends AppLayout {

    private final Tabs menu;

    public MainView() {
        menu = createMenuTabs();
        addToNavbar(menu);
    }

    private Tabs createMenuTabs() {
        final Tabs tabs = new Tabs();
        tabs.setId("tabs");
        tabs.add(getAvailableTabs());
        return tabs;
    }

    private Tab[] getAvailableTabs() {
        return new Tab[]{
                createTab("Generator", VaadinIcon.COGS.create(), GeneratorView.class),
                createTab("Help", VaadinIcon.QUESTION.create(), HelpView.class),
                createTab("About", VaadinIcon.INFO.create(), AboutView.class)
        };
    }

    private Tab createTab(String title, Icon icon, Class<? extends Component> viewClass) {
        return createTab(populateLink(new RouterLink(null, viewClass), title), icon);
    }

    private Tab createTab(Component content, Icon icon) {
        final Tab tab = new Tab(icon);
        tab.addThemeVariants(TabVariant.LUMO_ICON_ON_TOP);
        tab.add(content);
        return tab;
    }

    private <T extends HasComponents> T populateLink(T a, String title) {
        a.add(title);
        return a;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        selectTab();
    }

    private void selectTab() {
        String target = RouteConfiguration.forSessionScope().getUrl(getContent().getClass());
        Optional<Component> tabToSelect = menu.getChildren().filter(tab -> {
            Component child = tab.getChildren().findFirst().orElse(null);
            return child instanceof RouterLink && ((RouterLink) child).getHref().equals(target);
        }).findFirst();
        tabToSelect.ifPresent(tab -> menu.setSelectedTab((Tab) tab));
    }
}
