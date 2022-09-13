package es.upct.cpcd.indieopen.unit.domain;

import java.time.LocalDateTime;
import java.util.List;

import es.upct.cpcd.indieopen.category.domain.Category;
import es.upct.cpcd.indieopen.common.Language;
import es.upct.cpcd.indieopen.educationalcontext.domain.EducationalContext;
import es.upct.cpcd.indieopen.user.domain.UserData;
import es.upct.cpcd.indieopen.utils.ObjectUtils;

public class UnitBuilder {

	public static UnitAuthorStep createBuilder() {
		return new Steps();
	}

	public interface UnitAuthorStep {
		UnitTypeStep withAuthor(UserData author);
	}

	public interface UnitTypeStep {
		UnitInfoStep contentUnit();

		UnitInfoStep evaluationUnit();

		UnitInfoStep withType(UnitType unitType);
	}

	public interface UnitInfoStep {
		CategoryStep withUnitInfo(String name, String shortDescription, Language language);
	}

	public interface CategoryStep {
		LicenseStep withCategory(Category category);
	}

	public interface LicenseStep {
		ModeStep withLicense(License license);

		ModeStep withDefaultLicense();
	}

	public interface ModeStep {
		EducationalContextStep withDefaultMode();

		EducationalContextStep withMode(UnitMode mode);
	}

	public interface EducationalContextStep {
		CreativeCommonsStep withEducationalContext(EducationalContext context);

		CreativeCommonsStep withEducationalContext(List<EducationalContext> context);
	}

	public interface CreativeCommonsStep {

		Build withDefaultCreativeCommons();

		Build withCreativeCommons(CreativeCommons cc);
	}

	public interface Build {
		Build withAverageRating(double rating);

		Build withRatingCount(int count);

		Build withLongDescription(String longDescription);

		Build withCover(String cover);

		Build withTags(String... tags);

		Build withPublishedDate(LocalDateTime publishedDate);

		Build withOriginalUnitStamp(OriginalUnitStamp stamp);

		Build withOriginalUnit(Unit originalUnit);

		Build withCreatedAt(LocalDateTime createdAt);

		Build withResourceId(String resourceId);

		Build withDocumentId(String documentId);

		Build withAgeRange(int min, int max);

		Build withTheme(String theme);

		Unit build();
	}

	private static class Steps implements UnitAuthorStep, ModeStep, UnitTypeStep, EducationalContextStep, UnitInfoStep,
			CategoryStep, LicenseStep, CreativeCommonsStep, Build {
		private final Unit unit;

		private Steps() {
			this.unit = new Unit();
		}

		@Override
		public Build withCover(String cover) {
			ObjectUtils.requireStringValid(cover);
			this.unit.setCover(cover);

			return this;
		}

		@Override
		public Build withTags(String... tags) {
			ObjectUtils.requireTrue(tags.length > 0, "tags must not be empty");
			this.unit.setRawTagsFromArray(tags);
			return this;
		}

		@Override
		public Unit build() {
			return this.unit;
		}

		@Override
		public EducationalContextStep withDefaultMode() {
			this.unit.setMode(Unit.DEFAULT_MODE);

			return this;
		}

		@Override
		public EducationalContextStep withMode(UnitMode mode) {
			ObjectUtils.requireNonNull(mode);
			this.unit.setMode(mode);

			return this;
		}

		@Override
		public ModeStep withLicense(License license) {
			ObjectUtils.requireNonNull(license);
			this.unit.setLicense(license);

			return this;
		}

		@Override
		public ModeStep withDefaultLicense() {
			this.unit.setLicense(Unit.DEFAULT_LICENSE);

			return this;
		}

		@Override
		public LicenseStep withCategory(Category category) {
			ObjectUtils.requireNonNull(category);
			this.unit.setCategory(category);

			return this;
		}

		@Override
		public Build withDocumentId(String documentId) {
			ObjectUtils.requireStringValid(documentId);
			this.unit.setDocumentId(documentId);

			return this;
		}

		@Override
		public CategoryStep withUnitInfo(String name, String shortDescription, Language language) {
			ObjectUtils.requireStringsValid(name, shortDescription);
			ObjectUtils.requireNonNull(language);

			this.unit.setName(name);
			this.unit.setShortDescription(shortDescription);
			this.unit.setLanguage(language);

			return this;
		}

		@Override
		public UnitInfoStep contentUnit() {
			this.unit.setUnitType(UnitType.CONTENT);

			return this;
		}

		@Override
		public UnitInfoStep evaluationUnit() {
			this.unit.setUnitType(UnitType.EVALUATION);

			return this;
		}

		@Override
		public UnitInfoStep withType(UnitType unitType) {
			ObjectUtils.requireNonNull(unitType);
			this.unit.setUnitType(unitType);

			return this;
		}

		@Override
		public UnitTypeStep withAuthor(UserData author) {
			ObjectUtils.requireNonNull(author);

			this.unit.setAuthor(author);
			return this;
		}

		@Override
		public Build withLongDescription(String longDescription) {
			ObjectUtils.requireStringsValid(longDescription);
			this.unit.setLongDescription(longDescription);

			return this;
		}

		@Override
		public Build withAverageRating(double rating) {
			if (rating < 0 || rating > 5)
				throw new IllegalArgumentException();

			this.unit.setRatingAverage(rating);

			return this;
		}

		@Override
		public Build withPublishedDate(LocalDateTime publishedDate) {
			ObjectUtils.requireNonNull(publishedDate);
			this.unit.setPublishedDate(publishedDate);
			return this;
		}

		@Override
		public Build withRatingCount(int ratingCount) {
			ObjectUtils.paramGreaterThan(ratingCount, 0);
			this.unit.setRatingCount(ratingCount);
			return this;
		}

		@Override
		public Build withOriginalUnitStamp(OriginalUnitStamp stamp) {
			ObjectUtils.requireNonNull(stamp);
			this.unit.setOriginalUnitStamp(stamp);
			return this;
		}

		@Override
		public Build withCreatedAt(LocalDateTime createdAt) {
			ObjectUtils.requireNonNull(createdAt);
			this.unit.setCreatedAt(createdAt);
			return this;
		}

		@Override
		public Build withResourceId(String resourceId) {
			ObjectUtils.requireStringsValid(resourceId);
			this.unit.setUnitResourceId(resourceId);
			return this;
		}

		@Override
		public Build withOriginalUnit(Unit originalUnit) {
			ObjectUtils.requireNonNull(originalUnit);
			this.unit.setOriginalUnit(originalUnit);

			return this;
		}

		@Override
		public Build withAgeRange(int min, int max) {
			ObjectUtils.requireTrue(min <= max, "Min must be lower or equal than max");
			this.unit.setAgeRange(new AgeRange(min, max));

			return this;
		}

		@Override
		public CreativeCommonsStep withEducationalContext(EducationalContext context) {
			ObjectUtils.requireNonNull(context);
			this.unit.getEducationalContexts().add(context);

			return this;
		}

		@Override
		public CreativeCommonsStep withEducationalContext(List<EducationalContext> contexts) {
			ObjectUtils.requireNonEmpty(contexts, "contexts");
			this.unit.getEducationalContexts().addAll(contexts);

			return this;
		}

		@Override
		public Build withDefaultCreativeCommons() {
			this.unit.setCreativeCommons(Unit.DEFAULT_CREATIVE_COMMONS);

			return this;
		}

		@Override
		public Build withCreativeCommons(CreativeCommons cc) {
			ObjectUtils.requireNonNull(cc);
			this.unit.setCreativeCommons(cc);

			return this;
		}

		@Override
		public Build withTheme(String theme) {
			ObjectUtils.requireStringValid(theme);
			this.unit.setTheme(theme);

			return this;
		}
	}
}