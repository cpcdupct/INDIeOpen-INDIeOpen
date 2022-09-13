package es.upct.cpcd.indieopen.category.resources;

import java.util.ArrayList;
import java.util.List;

import es.upct.cpcd.indieopen.category.domain.Category;
import lombok.Getter;

/** Category Resource for web requests */
@Getter
public class CategoryResource {

	private String key;
	private List<CategoryResource> subCategories;

	public static CategoryResource from(Category category) {

		CategoryResource resource = new CategoryResource();
		resource.key = String.valueOf(category.getId());
		resource.subCategories = new ArrayList<>();

		for (Category subCategory : category.getSubCategories()) {
			resource.subCategories.add(from(subCategory));
		}

		return resource;
	}

}
