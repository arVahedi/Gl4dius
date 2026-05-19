package io.gl4dius.cli.repository;

import io.gl4dius.cli.model.entity.Gl4diusMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Gl4diusMetadataRepository extends JpaRepository<Gl4diusMetadata, String> {
}
