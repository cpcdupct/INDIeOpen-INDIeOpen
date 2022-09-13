package es.upct.cpcd.indieopen.unit.domain;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Embeddable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class OriginalUnitStamp implements Serializable {
	private static final long serialVersionUID = -8572516794624179868L;

	private String authorName;
	private String authorEmail;
	private String authorInstitution;
	private String unitName;
	private LocalDateTime timeStamp;

	public static OriginalUnitStamp from(Unit originalUnit) {
		OriginalUnitStamp stamp = new OriginalUnitStamp();
		stamp.authorName = originalUnit.getAuthor().getCompleteName();
		stamp.authorEmail = originalUnit.getAuthor().getEmail();
		stamp.authorInstitution = originalUnit.getAuthor().getInstitution();
		stamp.timeStamp = LocalDateTime.now();
		stamp.unitName = originalUnit.getName();
		return stamp;
	}
}
