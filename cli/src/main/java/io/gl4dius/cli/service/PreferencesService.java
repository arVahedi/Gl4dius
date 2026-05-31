package io.gl4dius.cli.service;

import io.gl4dius.cli.assets.PreferencesKey;
import io.gl4dius.cli.model.entity.Preferences;
import io.gl4dius.cli.repository.PreferencesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PreferencesService {

    private final PreferencesRepository preferencesRepository;

    @Transactional
    public void updatePreferences(@NonNull String key, @NonNull String value) {
        var preferencesKey = PreferencesKey.fromAcronym(key);
        var preferences = this.preferencesRepository.findById(preferencesKey)
                .orElseGet(() -> {
                    var newPreferences = new Preferences();
                    newPreferences.setKey(preferencesKey);
                    return newPreferences;
                });

        preferences.setValue(value);
        this.preferencesRepository.save(preferences);
    }

    public List<Preferences> listPreferences() {
        return this.preferencesRepository.findAll();
    }
}
