package de.sharknoon.internal_dsl_generator.views.generator;

import java.util.Objects;

public final class Project {

    private final String grammar;
    private final String grammarName;
    private final String packageName;
    private final boolean includeDOTGraph;

    public Project(String grammar, String grammarName, String packageName, boolean includeDOTGraph) {
        this.grammar = Objects.requireNonNull(grammar);
        this.grammarName = Objects.requireNonNull(grammarName);
        this.packageName = Objects.requireNonNull(packageName);
        this.includeDOTGraph = includeDOTGraph;
    }

    public String getGrammar() {
        return grammar;
    }

    public String getGrammarName() {
        return grammarName;
    }

    public String getPackageName() {
        return packageName;
    }

    public boolean isIncludeDOTGraph() {
        return includeDOTGraph;
    }
}
