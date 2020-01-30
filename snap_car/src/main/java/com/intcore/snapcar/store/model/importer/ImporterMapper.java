package com.intcore.snapcar.store.model.importer;

import com.intcore.snapcar.core.scope.ApplicationScope;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

@ApplicationScope
public class ImporterMapper {

    @Inject
    ImporterMapper() {
    }

    public ImporterModel toImporterModel(ImporterApiResponse response) {
        if (response == null) return null;
        return new ImporterModel(response.getId(),
                response.getNameAr(),
                response.getNameEn());
    }

    public ImporterViewModel toImporterViewModel(ImporterModel model) {
        if (model == null) return null;
        String name = model.getNameAr();
        if (Locale.getDefault().getLanguage().contentEquals("en")) {
            name = model.getNameEn();
        }
        return new ImporterViewModel(model.getId(),
                name);
    }

    public List<ImporterModel> toImporterModels(List<ImporterApiResponse> responses) {
        List<ImporterModel> models = new ArrayList<>();
        for (ImporterApiResponse response : responses) {
            models.add(toImporterModel(response));
        }
        return models;
    }

    public List<ImporterViewModel> toImporterViewModels(List<ImporterModel> responses) {
        List<ImporterViewModel> viewModels = new ArrayList<>();
        for (ImporterModel response : responses) {
            viewModels.add(toImporterViewModel(response));
        }
        return viewModels;
    }
}