package de.sharknoon.internal_dsl_generator.views.help;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.data.MutableDataSet;
import de.sharknoon.internal_dsl_generator.views.main.MainView;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;

@Route(value = "help", layout = MainView.class)
@PageTitle("Help")
@CssImport("./styles/views/help/help-view.css")
public class HelpView extends Div {

    public HelpView() {
        setId("help-view");
        String readme = fetchREADME();
        String html = convertToHTML(readme);
        add(new Html(html));
    }

    /**
     * @return The Markdown formatted README
     */
    private String fetchREADME() {
        try {
            HttpClient client = HttpClient.newBuilder()
                    .followRedirects(HttpClient.Redirect.ALWAYS)
                    .build();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(README_LINK))
                    .build();
            return client.send(request, HttpResponse.BodyHandlers.ofString())
                    .body();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "<div>Error</div>";
    }

    private String convertToHTML(String markdown) {
        MutableDataSet options = new MutableDataSet();
        options.set(Parser.EXTENSIONS, Arrays.asList(TablesExtension.create(), StrikethroughExtension.create()));
        Parser parser = Parser.builder(options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(options).build();
        Document document = parser.parse(markdown);
        return "<div>" + renderer.render(document) + "</div>";
    }

    private final String README_LINK = "https://github.com/etgramli/AntlrTest/raw/master/README.md";

}
