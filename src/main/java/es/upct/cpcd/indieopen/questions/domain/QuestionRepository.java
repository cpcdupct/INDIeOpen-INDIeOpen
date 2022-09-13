package es.upct.cpcd.indieopen.questions.domain;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<Question, String> {
	Page<Question> findByAuthorId(String currentUserId, Pageable page);

	Optional<Question> findQuestionByAuthorIdAndID(String authorId, String questionId);

	List<Question> findByAuthorId(String userId);

	List<Question> findByAuthorIdAndIDIn(String userId, Collection<String> ids);

	@Query("SELECT new my_domain.QuestionGroup(q.group.groupKey, q.group.groupName) FROM Question q WHERE q.group.groupKey = ?1")
	Set<QuestionGroup> findFirstQuestionGroupByGroupKey(String groupKey);

	@Query("SELECT new my_domain.QuestionGroup(q.group.groupKey, q.group.groupName) FROM Question q WHERE q.author.id = ?1")
	Set<QuestionGroup> findQuestionGroupsByAuthor(String userId);

	@Query("SELECT q FROM Question q WHERE q.author.id = ?1 AND q.group.groupKey = ?2")
	List<Question> findQuestionByAuthorAndGroupKey(String userId, String groupKey);

	@Query("SELECT q FROM Question q WHERE q.author.id = ?1 AND q.group.groupKey = ?2")
	Page<Question> findQuestionByAuthorAndGroupKey(String userId, Pageable page, String groupKey);

	@Query("SELECT q FROM Question q WHERE q.author.id = ?1 AND question_type = ?2")
	Page<Question> findQuestionByAuthorAndType(String userId, Pageable page, String type);

	@Query("SELECT q FROM Question q WHERE q.author.id = ?1 AND q.group.groupKey = ?2 AND  question_type = ?3")
	Page<Question> findQuestionByAuthorAndTypeAndGroup(String userId, Pageable page, String groupKey, String klass);

}