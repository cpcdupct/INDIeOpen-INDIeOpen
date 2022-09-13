package es.upct.cpcd.indieopen.unit.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import es.upct.cpcd.indieopen.unit.domain.Unit;
import es.upct.cpcd.indieopen.unit.domain.UnitType;
import es.upct.cpcd.indieopen.user.domain.UserData;

@Repository
public interface UnitRepository extends JpaRepository<Unit, Integer>, JpaSpecificationExecutor<Unit> {

	Optional<Unit> findByIdAndAuthorId(int unitId, String authorId);

	@Query(value = "SELECT u FROM Unit u WHERE u.author.id = ?1")
	Page<Unit> findByAuthorId(String authorId, Pageable page);

	List<Unit> findByAuthorId(String authorId);

	Page<Unit> findByAuthorIdAndUnitType(Pageable page, String userId, UnitType type);

	Optional<Unit> findUnitById(int unitId);

	@Query("SELECT u FROM Unit u WHERE u.license != 'PRIVATE' AND u.ratingAverage > 0")
	List<Unit> findTopRatedUnits(Pageable page);

	@Query("SELECT u FROM Unit u WHERE u.author.id = ?1 AND u.unitType = 'EVALUATION'")
	List<Unit> findEvaluationUnitsByAuthorId(String userId);

	@Query("SELECT new my_domain.repository.TopAuthorRecord(u.id, count(u)) FROM UserData u, Unit un WHERE un.author.id = u.id and un.license <> 'PRIVATE' GROUP BY u.id ORDER BY count(u) DESC")
	List<TopAuthorRecord> findTopAuthors(Pageable pageable);

	@Query(value = "SELECT user FROM UserData user WHERE user.completeName like concat('%', ?1,'%') AND user.id IN (SELECT DISTINCT(unit.author.id) FROM Unit unit WHERE unit.license <> 'PRIVATE')")
	List<UserData> findAuthorsWithAtLeastOnePblishedUnit(String name);

	@Query(value = "SELECT user FROM UserData user WHERE user.id IN (SELECT DISTINCT(unit.author.id) FROM Unit unit WHERE unit.license <> 'PRIVATE')")
	List<UserData> findAuthorsWithAtLeastOnePblishedUnit();

	Optional<Unit> findByUnitResourceId(String unitResource);

	@Query("SELECT u FROM Unit u WHERE u.publishedDate IS NOT null AND u.author.id = ?1")
	List<Unit> findAllPublishedUnitsByUser(String userId);
}