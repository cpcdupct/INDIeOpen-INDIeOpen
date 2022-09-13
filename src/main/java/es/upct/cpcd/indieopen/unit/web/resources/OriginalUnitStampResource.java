package es.upct.cpcd.indieopen.unit.web.resources;

import com.fasterxml.jackson.annotation.JsonInclude;

import es.upct.cpcd.indieopen.unit.domain.Unit;
import es.upct.cpcd.indieopen.utils.DateUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OriginalUnitStampResource {
	private int id;
	private String authorName;
	private String unitName;
	private String timeStamp;

	public static OriginalUnitStampResource from(Unit unit) {
		if (unit.getOriginalUnitStamp() == null)
			return null;

		OriginalUnitStampResource resource = new OriginalUnitStampResource();

		resource.authorName = unit.getOriginalUnitStamp().getAuthorName();
		resource.unitName = unit.getOriginalUnitStamp().getUnitName();
		resource.timeStamp = DateUtils.dateToISOString(unit.getOriginalUnitStamp().getTimeStamp());

		resource.id = unit.getOriginalUnit() != null ? unit.getOriginalUnit().getId() : null;

		return resource;
	}
}
