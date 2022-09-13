package es.upct.cpcd.indieopen.unit.beans;

import es.upct.cpcd.indieopen.common.Language;
import es.upct.cpcd.indieopen.utils.validators.ValidateEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUnitInfoBean {

	@NotBlank(message = "Blank")
	@Size(min = 1, max = 120, message = "Size (1,120)")
	private String name;

	@NotBlank(message = "Blank")
	@Size(min = 1, max = 240, message = "Size (1,240)")
	private String shortDescription;

	@Size(min = 1, max = 3000, message = "Size (1,3000)")
	private String longDescription;

	@ValidateEnum(value = Language.class, enumMethod = "getValue", message = "Not valid")
	private String language;

	private String[] educationalContext;

	@NotEmpty
	private Integer[] ageRange;

	private int category;

	private String[] tags;

	@URL(message = "URL")
	private String cover;

	private String theme;

	// FUNCTIONS
	public Language getLanguageEnum() {
		return Language.get(language);
	}

	public int getAgeRangeMin() {
		return ageRange[0];
	}

	public int getAgeRangeMax() {
		return ageRange[1];
	}

}