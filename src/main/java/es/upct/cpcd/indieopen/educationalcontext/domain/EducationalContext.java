package es.upct.cpcd.indieopen.educationalcontext.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
@Getter
public class EducationalContext implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String educationalLevel;

    @Column(nullable = false)
    private String name;

    public EducationalContext(String educationalLevel, String name) {
        this.name = name;
        this.educationalLevel = educationalLevel;
    }
}
