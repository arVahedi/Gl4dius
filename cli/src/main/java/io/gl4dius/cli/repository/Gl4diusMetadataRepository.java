package io.gl4dius.cli.repository;

import io.gl4dius.cli.model.entity.Gl4diusMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

public interface Gl4diusMetadataRepository extends JpaRepository<Gl4diusMetadata, String> {
}
