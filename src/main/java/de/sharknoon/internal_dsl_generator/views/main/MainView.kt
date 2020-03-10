package de.sharknoon.internal_dsl_generator.views.main

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.applayout.AppLayout
import com.vaadin.flow.component.applayout.DrawerToggle
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.tabs.Tab
import com.vaadin.flow.component.tabs.Tabs
import com.vaadin.flow.component.tabs.TabsVariant
import com.vaadin.flow.router.RouteConfiguration
import com.vaadin.flow.router.RouterLink
import com.vaadin.flow.theme.Theme
import com.vaadin.flow.theme.lumo.Lumo
import de.sharknoon.internal_dsl_generator.views.about.AboutView
import de.sharknoon.internal_dsl_generator.views.generator.GeneratorView
import de.sharknoon.internal_dsl_generator.views.help.HelpView

/**
 * The main view is a top-level placeholder for other views.
 */
@CssImport(value = "./styles/views/main/main-view.css", themeFor = "vaadin-app-layout")
@Theme(value = Lumo::class, variant = Lumo.DARK)
class MainView : AppLayout() {

    private val menu: Tabs

    init {
        primarySection = Section.DRAWER
        addToNavbar(true, DrawerToggle())
        menu = createMenuTabs()
        addToDrawer(menu)
    }

    private fun createMenuTabs(): Tabs {
        val tabs = Tabs()
        tabs.orientation = Tabs.Orientation.VERTICAL
        tabs.addThemeVariants(TabsVariant.LUMO_MINIMAL)
        tabs.setId("tabs")
        tabs.add(*getAvailableTabs())
        return tabs
    }

    private fun getAvailableTabs() = arrayOf(
            createTab("Generator", VaadinIcon.COGS.create(), GeneratorView::class.java),
            createTab("Help", VaadinIcon.QUESTION.create(), HelpView::class.java),
            createTab("About", VaadinIcon.INFO.create(), AboutView::class.java)
    )

    private fun createTab(title: String, icon: Component, viewClass: Class<out Component>): Tab {
        return createTab(populateLink(RouterLink(null, viewClass), title), icon)
    }

    private fun createTab(content: Component, icon: Component): Tab {
        val tab = Tab(icon)
        tab.add(content)
        return tab
    }

    private fun <T : HasComponents> populateLink(a: T, title: String): T {
        a.add(title)
        return a
    }

    override fun afterNavigation() {
        super.afterNavigation()
        selectTab()
    }

    private fun selectTab() {
        val target = RouteConfiguration.forSessionScope().getUrl(content.javaClass)
        val tabToSelect = menu.children.filter { tab: Component ->
            val child = tab.children.findFirst().get()
            child is RouterLink && child.href == target
        }.findFirst()
        tabToSelect.ifPresent { tab: Component -> menu.selectedTab = tab as Tab }
    }


}