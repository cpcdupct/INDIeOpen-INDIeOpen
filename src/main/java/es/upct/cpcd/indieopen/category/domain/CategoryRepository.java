package es.upct.cpcd.indieopen.category.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
/**
 * Implementation of a JpaRepository of Category
 * 
 * @author CPCD
 *
 */
public interface CategoryRepository extends JpaRepository<Category, Integer> {

}