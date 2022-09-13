package es.upct.cpcd.indieopen.rate.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.upct.cpcd.indieopen.rate.domain.Rate;

@Repository
public interface RatingRepository extends JpaRepository<Rate, Integer> {
    Page<Rate> findByUnitId(int unitId, Pageable page);

    List<Rate> findByUnitId(int unitId);

	Optional<Rate> findByUnitIdAndAuthorId(int unitId, String userId);
}
