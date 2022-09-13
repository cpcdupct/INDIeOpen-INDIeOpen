package es.upct.cpcd.indieopen.explore;

import static es.upct.cpcd.indieopen.utils.validators.TypeValidator.areCategoriesValid;
import static es.upct.cpcd.indieopen.utils.validators.TypeValidator.isAgeRangeValid;
import static es.upct.cpcd.indieopen.utils.validators.TypeValidator.isEducationalContextValid;
import static es.upct.cpcd.indieopen.utils.validators.TypeValidator.isLanguageValid;
import static es.upct.cpcd.indieopen.utils.validators.TypeValidator.isLicenseValid;
import static es.upct.cpcd.indieopen.utils.validators.TypeValidator.isTypeValid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.upct.cpcd.indieopen.category.domain.Category;
import es.upct.cpcd.indieopen.category.domain.CategoryRepository;
import es.upct.cpcd.indieopen.common.Language;
import es.upct.cpcd.indieopen.common.exceptions.INDIeException;
import es.upct.cpcd.indieopen.common.exceptions.INDIeExceptionFactory;
import es.upct.cpcd.indieopen.educationalcontext.domain.EducationalContext;
import es.upct.cpcd.indieopen.educationalcontext.domain.EducationalContextRepository;
import es.upct.cpcd.indieopen.unit.domain.License;
import es.upct.cpcd.indieopen.unit.domain.Unit;
import es.upct.cpcd.indieopen.unit.domain.UnitType;
import es.upct.cpcd.indieopen.unit.domain.repository.UnitRepository;
import es.upct.cpcd.indieopen.unit.web.resources.AuthorResource;
import es.upct.cpcd.indieopen.unit.web.resources.UnitResource;
import es.upct.cpcd.indieopen.user.domain.UserData;
import es.upct.cpcd.indieopen.user.domain.UserRepository;
import es.upct.cpcd.indieopen.utils.StringUtils;

@Service
@Transactional(rollbackFor = INDIeException.class)
public class ExploreService {

	private final UnitRepository unitRepository;
	private final UserRepository userRepository;
	private final CategoryRepository categoryRepository;
	private final EducationalContextRepository educationalContextRepository;

	@Autowired
	public ExploreService(UnitRepository unitRepository, UserRepository userRepository,
			CategoryRepository categoryRepository, EducationalContextRepository educationalContextRepository) {
		this.unitRepository = unitRepository;
		this.userRepository = userRepository;
		this.categoryRepository = categoryRepository;
		this.educationalContextRepository = educationalContextRepository;
	}

	public List<Unit> findTopRatedUnits() {
		return unitRepository.findTopRatedUnits(
				PageRequest.of(0, 10, Sort.by("ratingAverage").descending().and(Sort.by("ratingCount").descending())));
	}

	public List<Unit> findMostRecentUnitsShared(String type) {
		if (isTypeValid(type)) {
			return unitRepository.findAll(
					Unit.Specifications.isUnitShareable().and(Unit.Specifications.isUnitType(UnitType.get(type))),
					PageRequest.of(0, 20, Sort.by(Direction.DESC, "publishedDate"))).getContent();
		} else
			return unitRepository.findAll(Unit.Specifications.isUnitShareable(),
					PageRequest.of(0, 20, Sort.by(Direction.DESC, "publishedDate"))).getContent();
	}

	public UnitResource findUnitDetailsById(int unitId) throws INDIeException {
		Unit u = unitRepository.findUnitById(unitId)
				.orElseThrow(() -> INDIeExceptionFactory.createUnitNotFound(unitId));

		if (u.canBeReusedOrCopied())
			return UnitResource.fromUnit(u);

		throw INDIeExceptionFactory.createUnitNotAccessible(unitId);
	}

	public List<AuthorResource> getListOfAuthors(String name) {
		if (StringUtils.isStringValid(name))
			return this.unitRepository.findAuthorsWithAtLeastOnePblishedUnit(name).stream().map(AuthorResource::from)
					.collect(Collectors.toList());
		else
			return this.unitRepository.findAuthorsWithAtLeastOnePblishedUnit().stream().map(AuthorResource::from)
					.collect(Collectors.toList());
	}

	public Page<UnitResource> searchUnits(Pageable page, String text, String category, String type, String usemode,
			String context, String language, String ageRange, String author) {
		Specification<Unit> specifications = buildSearchSpecifications(text, category, type, usemode, context, language,
				ageRange, author);

		return unitRepository.findAll(specifications, page).map(UnitResource::fromUnit);
	}

	private Specification<Unit> buildSearchSpecifications(String text, String category, String type, String usemode,
			String context, String language, String ageRange, String author) {
		Specification<Unit> specifications = Specification.where(Unit.Specifications.nameContains(text));

		if (StringUtils.isStringValid(author))
			specifications = specifications.and(Unit.Specifications.isAuthor(author));

		if (isTypeValid(type))
			specifications = specifications.and(Unit.Specifications.isUnitType(UnitType.get(type)));

		if (isEducationalContextValid(context)) {
			EducationalContext educationalContext = educationalContextRepository.findById(context)
					.orElseThrow(IllegalStateException::new);
			specifications = specifications.and(Unit.Specifications.hasEducationalContext(educationalContext));
		}

		if (isLanguageValid(language)) {
			List<Language> collectionOfLanguages = new ArrayList<>();
			String[] languages = language.split(",");
			for (String languageValue : languages) {
				collectionOfLanguages.add(Language.get(languageValue));
			}
			specifications = specifications.and(Unit.Specifications.hasLanguage(collectionOfLanguages));
		}

		if (isAgeRangeValid(ageRange)) {
			int[] range = Arrays.asList(ageRange.split(",")).stream().mapToInt(Integer::parseInt).toArray();
			specifications = specifications.and(Unit.Specifications.isInAgeRange(range));
		}

		if (isLicenseValid(usemode))
			specifications = specifications.and(Unit.Specifications.isUseModeLicense(License.get(usemode)));
		else
			specifications = specifications.and(Unit.Specifications.isUnitShareable());

		if (areCategoriesValid(category)) {
			Category categoryInstance = categoryRepository.findById(Integer.parseInt(category))
					.orElseThrow(IllegalStateException::new);

			List<Category> categoryAndChildrenCategories = categoryInstance.getSubCategories();
			categoryAndChildrenCategories.add(categoryInstance);
			Integer[] categoriesIDs = categoryAndChildrenCategories.stream().map(Category::getId)
					.toArray(Integer[]::new);
			specifications = specifications.and(Unit.Specifications.unitInCategories(categoriesIDs));
		}

		return specifications;
	}

	public AuthorResource getAuthor(String authorId) throws INDIeException {
		UserData author = this.userRepository.findAuthorWithAtleastOnePublishedUnit(authorId);
		return AuthorResource.from(author);
	}

}
