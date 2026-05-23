package io.gl4dius.cli.model.entity;

import io.gl4dius.cli.assets.PreferencesKey;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "preferences")
public class Preferences {

    @Id
    @Column(name = "key", nullable = false)
    private PreferencesKey key;

    @Column(name = "value", nullable = false)
    private String value;
}
