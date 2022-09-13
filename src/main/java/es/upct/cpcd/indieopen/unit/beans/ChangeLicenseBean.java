package es.upct.cpcd.indieopen.unit.beans;

import es.upct.cpcd.indieopen.unit.domain.CreativeCommons;
import es.upct.cpcd.indieopen.utils.validators.ValidateEnum;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
public class ChangeLicenseBean {

	@ValidateEnum(value = CreativeCommons.class, enumMethod = "getValue", message = "Not valid")
	private String license;

	public ChangeLicenseBean(String license) {
		this.license = license;
	}
}
