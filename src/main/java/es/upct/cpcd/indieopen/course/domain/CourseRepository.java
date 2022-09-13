package es.upct.cpcd.indieopen.course.domain;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {

	Optional<Course> findByIdAndAuthorId(int courseId, String userId);

	Page<Course> findByAuthorId(String userId, Pageable page);

	Optional<Course> findByExternalID(String externalId);

}
