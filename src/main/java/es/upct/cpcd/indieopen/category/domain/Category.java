package es.upct.cpcd.indieopen.category.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a Category in INDIeOpen
 * 
 * @author CPCD
 *
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Category implements Serializable {
	private static final long serialVersionUID = "MY_SERIAL_VERSION";

	/** Category identifier */
	@Id
	private Integer id;

	/** Category description only for better reading */
	private String description;

	/** Collection of subcategories. Realtion parent-children graph */
	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "category_children", joinColumns = @JoinColumn(name = "FK_CATEGORY_PARENT", nullable = false), inverseJoinColumns = @JoinColumn(name = "FK_CATEGORY_CHILD", nullable = false))
	private List<Category> subCategories;

	/** Collection of parent categories. */
	@ManyToMany(mappedBy = "subCategories", cascade = CascadeType.ALL)
	private List<Category> parentCategories;

	public Category(String description) {
		this.description = description;
		this.parentCategories = new ArrayList<>();
		this.subCategories = new ArrayList<>();
	}

	public Category(int id, String description) {
		this(description);
		this.id = id;
	}

	/**
	 * Adds a Category as a subcategory to the Category
	 * 
	 * @param categoryChild Subcategory
	 */
	public void addSubCategory(Category categoryChild) {
		this.subCategories.add(categoryChild);
		categoryChild.addParentCategory(this);
	}

	/**
	 * Ads a parent Category to a Category
	 * 
	 * @param category Parent Category
	 */
	private void addParentCategory(Category category) {
		this.parentCategories.add(category);
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof Category)) {
			return false;
		}
		Category category = (Category) o;
		return Objects.equals(id, category.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	@Override
	public String toString() {
		return "Category {" + " id='" + getId() + "'" + ", description='" + getDescription() + "'"
				+ ", parentsCategories='" + getParentCategories() + "'" + "}";
	}

}