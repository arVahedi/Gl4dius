package io.gl4dius.cli.repository;

import io.gl4dius.cli.model.entity.Preferences;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PreferencesRepository extends JpaRepository<Preferences, String> {
}
