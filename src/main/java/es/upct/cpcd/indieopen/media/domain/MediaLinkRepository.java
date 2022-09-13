package es.upct.cpcd.indieopen.media.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MediaLinkRepository extends JpaRepository<MediaLink, MediaKey> {

	List<MediaLink> findByUnitAndUser(int unitId, String userId);

	List<MediaLink> findByResource(String resource);

}
