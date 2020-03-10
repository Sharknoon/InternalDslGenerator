package de.sharknoon.internal_dsl_generator.views.generator

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.html.Anchor
import com.vaadin.flow.component.html.H1
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.component.upload.Upload
import com.vaadin.flow.component.upload.receivers.MemoryBuffer
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import com.vaadin.flow.router.RouteAlias
import com.vaadin.flow.server.StreamResource
import de.sharknoon.internal_dsl_generator.backend.GeneratorService
import de.sharknoon.internal_dsl_generator.utils.showErrorNotification
import de.sharknoon.internal_dsl_generator.views.main.MainView
import elemental.json.Json
import org.springframework.beans.factory.annotation.Autowired

@Route(value = "generator", layout = MainView::class)
@RouteAlias(value = "", layout = MainView::class)
@PageTitle("Generator")
@CssImport("./styles/views/generator/generator-view.css")
class GeneratorView(@Autowired private val service: GeneratorService) : VerticalLayout() {

    //Buffer for the File
    private val upload = Upload(MemoryBuffer())

    init {
        setId("generator-view")
        addComponents()
    }

    private fun addComponents() {
        //Title
        add(getTitleComponent())

        //Adding the Upload Components
        add(*getUploadComponents())
    }

    private fun getTitleComponent(): Component {
        //A little Title informing the user about the Generator
        return H1("Java Internal DSL Generator")
    }

    private fun getUploadComponents(): Array<Component> {
        //Textfield for the package name
        val packageNameField = TextField().apply {
            label = "Package Name"
            placeholder = "de.etgramlich"
            isRequired = true
        }

        //The upload Element for uploading the grammar file
        upload.apply {
            setAcceptedFileTypes(".bnf", ".ebnf")
            maxFiles = 1
            maxFileSize = 5 * 1024 * 1024
            //Customizing the Upload Button, Optional
            uploadButton = Button("Upload Grammar File")
            //Customizing the Drag Upload Text, Optional
            dropLabel = Span("Drop grammar file here")
        }


        //Adding File Rejection Listener
        upload.addFileRejectedListener {
            showErrorNotification(it.errorMessage)
        }

        // Start Button
        val startButton = Button("Generate").apply {
            addThemeVariants(ButtonVariant.LUMO_PRIMARY)
        }

        //Download Button
        val downloadButton = Button("Download").apply {
            addThemeVariants(ButtonVariant.LUMO_SUCCESS)
            setId("downloadButton")
            isVisible = false
            addClickListener {
                isVisible = false
                startButton.isVisible = true
                packageNameField.clear()
                //Resetting the uploaded File
                upload.receiver = MemoryBuffer()
                upload.element.setPropertyJson("files", Json.createArray())
            }
        }

        //Anchor for the download button
        val downloadAnchor = Anchor()
        downloadAnchor.element.setAttribute("download", true)
        downloadAnchor.add(downloadButton)
        startButton.addClickListener {
            val packageName = packageNameField.value
            if (!validateInput(packageName, upload.receiver as MemoryBuffer)) {
                return@addClickListener
            }
            try {
                val resource: StreamResource = service.generate(upload.receiver as MemoryBuffer, packageName)
                downloadAnchor.setHref(resource)
                startButton.isVisible = false
                downloadButton.isVisible = true
            } catch (e: Exception) {
                e.printStackTrace()
                showErrorNotification("Error during Generation $e")
            }
        }
        return arrayOf(packageNameField, upload, startButton, downloadAnchor)
    }

    private fun validateInput(packageName: String, buffer: MemoryBuffer): Boolean {
        if (packageName.isEmpty()) {
            showErrorNotification("Please enter a package name")
            return false
        } else if (buffer.fileData == null) {
            showErrorNotification("Please upload a grammar file")
            return false
        }
        return true
    }

}