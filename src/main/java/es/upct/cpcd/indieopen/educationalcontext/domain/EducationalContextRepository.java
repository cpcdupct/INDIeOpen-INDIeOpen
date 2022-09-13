package es.upct.cpcd.indieopen.educationalcontext.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EducationalContextRepository extends JpaRepository<EducationalContext, String> {

}
