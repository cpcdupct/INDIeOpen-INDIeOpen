package es.upct.cpcd.indieopen.unit.domain;

import static es.upct.cpcd.indieopen.utils.SpecificationUtils.contains;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PreUpdate;
import javax.persistence.criteria.Expression;

import org.springframework.data.jpa.domain.Specification;

import es.upct.cpcd.indieopen.category.domain.Category;
import es.upct.cpcd.indieopen.common.Language;
import es.upct.cpcd.indieopen.educationalcontext.domain.EducationalContext;
import es.upct.cpcd.indieopen.rate.domain.Rate;
import es.upct.cpcd.indieopen.user.domain.UserData;
import es.upct.cpcd.indieopen.utils.ModelUtils;
import es.upct.cpcd.indieopen.utils.StringUtils;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public final class Unit implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final UnitMode DEFAULT_MODE = UnitMode.ORIGINAL;
	public static final License DEFAULT_LICENSE = License.PRIVATE;
	public static final CreativeCommons DEFAULT_CREATIVE_COMMONS = CreativeCommons.PRIVATE;
	public static final Language DEFAULT_LANGUAGE = Language.ENGLISH;
	public static final String TAG_REGEX = "[0-9a-zA-Z ]+(;([0-9a-zA-Z ])+)*";
	public static final String TAG_SPLITTER = ";";
	private static final String DEFAULT_THEME = "GeneralTheme1";

	// BASIC INFORMATION
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;

	@Column(nullable = false)
	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

	@ManyToOne(optional = false)
	@JoinColumn(name = "AUTHOR_ID")
	private UserData author;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private UnitType unitType;

	@Enumerated(EnumType.STRING)
	@Column(length = 20, nullable = false, columnDefinition = "varchar(20) default 'PRIVATE'")
	private License license;

	@Enumerated(EnumType.STRING)
	@Column(length = 20, nullable = false, columnDefinition = "varchar(20) default 'ORIGINAL'")
	private UnitMode mode;

	@Enumerated(EnumType.STRING)
	@Column(length = 20, nullable = false, columnDefinition = "varchar(20) default 'PRIVATE'")
	private CreativeCommons creativeCommons;

	@ManyToMany
	private List<EducationalContext> educationalContexts;

	// RESOURCE INFORMATION
	@Column(nullable = false, unique = true, length = 32)
	private String documentId;

	@Column(nullable = false, unique = true, length = 32)
	private String unitResourceId;

	/**
	 * UNIT INFORMATION
	 */
	@Column()
	private LocalDateTime publishedDate;

	@Column(nullable = false)
	private boolean draft;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20, columnDefinition = "varchar(20) default 'ENGLISH'")
	private Language language;

	@Column(nullable = false, length = 60, columnDefinition = "varchar(20) default 'general-1'")
	private String theme;

	@Column(nullable = false, length = 120)
	private String name;

	@Column(nullable = false, length = 240)
	private String shortDescription;

	@Column(length = 2000, columnDefinition = "TEXT")
	private String longDescription;

	@ManyToOne(optional = false)
	@JoinColumn(name = "CATEGORY_ID")
	private Category category;

	@Column()
	private String cover;

	@Column(columnDefinition = "TEXT", length = 700)
	protected String rawTags;

	@Embedded
	private AgeRange ageRange;

	@Column(nullable = false)
	private double ratingAverage;

	@Column(nullable = false)
	private int ratingCount;

	@Column(nullable = false)
	private boolean analytics;

	// RELATIONS
	@ManyToOne()
	@JoinColumn(name = "ORIGINAL_UNIT_ID")
	private Unit originalUnit;

	@Embedded
	private OriginalUnitStamp originalUnitStamp;

	@OneToMany(targetEntity = Unit.class)
	private List<Unit> sharedBy;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "unit", orphanRemoval = true)
	private List<AuthorizacionAccess> authorizedAccesses;

	public Unit() {
		this.unitResourceId = ModelUtils.randomUUID(true);
		this.createdAt = LocalDateTime.now();
		this.sharedBy = new ArrayList<>();
		this.educationalContexts = new ArrayList<>();
		this.authorizedAccesses = new ArrayList<>();
		this.ageRange = new AgeRange(0, 0);
		this.creativeCommons = DEFAULT_CREATIVE_COMMONS;
		this.license = DEFAULT_LICENSE;
		this.mode = DEFAULT_MODE;
		this.theme = DEFAULT_THEME;
	}

	@PreUpdate
	public void preUpdate() {
		updatedAt = LocalDateTime.now();
	}

	public boolean hasContentCreated() {
		return (mode == UnitMode.ORIGINAL || mode == UnitMode.REUSED);
	}

	/**
	 * Determine if a unit can be shared with other INDIe users
	 */
	public boolean canBeShared() {
		return (mode == UnitMode.ORIGINAL || mode == UnitMode.REUSED);
	}

	/**
	 * Determine if a unit can be reused or copied
	 */
	public boolean canBeReusedOrCopied() {
		return (mode == UnitMode.ORIGINAL || mode == UnitMode.REUSED) && (license != License.PRIVATE);
	}

	public static UnitMode getModeFromLicense(License license) {
		if (license == License.ALLOW_REUSE)
			return UnitMode.REUSED;
		else if (license == License.ALLOW_READ_ONLY)
			return UnitMode.COPIED;

		return UnitMode.COPIED;
	}

	public static String getRawTagsFromArray(String... tags) {
		if (tags == null)
			throw new IllegalArgumentException("tags cannot be null");

		return String.join(TAG_SPLITTER, tags);
	}

	public String[] getTags() {
		if (!StringUtils.isStringValid(rawTags))
			return new String[0];

		return rawTags.split(TAG_SPLITTER);
	}

	public void setRawTagsFromArray(String[] tags) {
		this.rawTags = getRawTagsFromArray(tags);
	}

	public boolean isOriginal() {
		return (mode == UnitMode.ORIGINAL);
	}

	public boolean isPublished() {
		return this.publishedDate != null;
	}

	public boolean isOpen() {
		return this.isPublished() && this.license != License.PRIVATE;
	}

	/**
	 * Refresh the rating stats for a unit
	 *
	 * @param ratings Unit ratings
	 */
	public void refreshRatingAverage(List<Rate> ratings) {
		double ratingAcumulated = ratings.stream().map(Rate::getRating).mapToInt(Integer::intValue).sum();

		ratingCount = ratings.size();
		ratingAverage = ratingAcumulated / ratingCount;
	}

	public String getResource() {
		return author.getBase() + "/" + unitResourceId;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof Unit)) {
			return false;
		}
		Unit unit = (Unit) o;
		return id.equals(unit.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	public static class Specifications {

		private Specifications() {

		}

		public static Specification<Unit> nameContains(String name) {
			return (root, query, builder) -> builder.or(builder.like(root.get("name"), contains(name)),
					builder.like(root.get("rawTags").as(String.class), contains(name)),
					builder.like(root.get("author").get("completeName"), contains(name)));
		}

		public static Specification<Unit> tagContains(String tag) {
			return (root, query, builder) -> builder.like(root.get("rawTags").as(String.class), contains(tag));
		}

		public static Specification<Unit> isUnitType(UnitType type) {
			return (root, query, builder) -> builder.equal(root.get("unitType"), type);
		}

		public static Specification<Unit> isUnitShareable() {
			return (root, query, builder) -> builder.notEqual(root.get("license"), License.PRIVATE);
		}

		public static Specification<Unit> isUseModeLicense(License license) {
			return (root, query, builder) -> builder.equal(root.get("license"), license);
		}

		@SuppressWarnings({ "all" })
		public static Specification<Unit> unitInCategories(Integer[] categories) {
			return (root, query, builder) -> builder
					.isTrue(root.get("category").get("id").as(Integer.class).in(categories));
		}

		public static Specification<Unit> hasEducationalContext(EducationalContext educationalContext) {
			return (root, query, builder) -> {
				Expression<List<EducationalContext>> contexts = root.get("educationalContexts");
				return builder.isMember(educationalContext, contexts);
			};
		}

		public static Specification<Unit> isInAgeRange(int[] range) {
			return (root, query, builder) -> builder.or(
					builder.between(root.get("ageRange").get("min"), range[0], range[1]),
					builder.between(root.get("ageRange").get("max"), range[0], range[1]));
		}

		@SuppressWarnings({ "all" })
		public static Specification<Unit> hasLanguage(List<Language> languages) {
			return (root, query, builder) -> builder.isTrue(root.get("language").in(languages));
		}

		public static Specification<Unit> unitAuthorNameContains(String text) {
			return (root, query, builder) -> builder.like(root.get("author").get("completeName"), contains(text));
		}

		public static Specification<Unit> isAuthor(String author) {
			return (root, query, builder) -> builder.equal(root.get("author").get("id").as(String.class), author);
		}

	}

}
