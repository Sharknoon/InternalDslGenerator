package de.sharknoon.internal_dsl_generator

import com.vaadin.flow.component.page.AppShellConfigurator
import com.vaadin.flow.server.PWA
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class Application

@PWA(name = "Internal DSL Generator", shortName = "Internal DSL Generator")
class AppShell : AppShellConfigurator

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
