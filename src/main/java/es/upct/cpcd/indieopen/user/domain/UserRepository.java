package es.upct.cpcd.indieopen.user.domain;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserData, String> {

	Optional<UserData> findByResetPasswordToken(String token);

	Optional<UserData> findByEmail(String email);

	@Query(value = "SELECT user FROM UserData user, Unit u WHERE user.id = ?1 AND u.author.id = user.id AND u.license <> 'PRIVATE'")
	UserData findAuthorWithAtleastOnePublishedUnit(String authorId);
}