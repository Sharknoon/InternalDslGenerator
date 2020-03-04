package de.sharknoon.internal_dsl_generator.backend;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class BackendService {

    private List<GrammarFile> grammarFiles;

    {
    // Init dummy data

        Long id = 42L;

        grammarFiles = new ArrayList<>();

    }

    public List<GrammarFile> getGrammarFiles() {
        return grammarFiles;
    }

}
