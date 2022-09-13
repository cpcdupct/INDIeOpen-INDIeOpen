package es.upct.cpcd.indieopen.video.domain;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoRepository extends JpaRepository<Video, Integer> {

	Optional<Video> findByIdAndAuthorId(int videoId, String userId);

	Page<Video> findByAuthorId(String userId, Pageable page);

	Optional<Video> findByExternalID(String externalId);

}