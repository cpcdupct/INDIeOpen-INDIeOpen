package es.upct.cpcd.indieopen.questions.domain;

import java.io.Serializable;
import java.util.Objects;
import java.util.Random;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import es.upct.cpcd.indieopen.common.Language;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class QuestionGroup implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Random random = new Random();
	private static final int KEY_LENGTH = 10;

	@Column(length = KEY_LENGTH)
	private String groupKey;

	private String groupName;

	public QuestionGroup(String name) {
		this(randomKey(), name);
	}

	public QuestionGroup(String groupKey, String groupName) {
		this.groupKey = groupKey;
		this.groupName = groupName;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof QuestionGroup)) {
			return false;
		}
		QuestionGroup questionGroup = (QuestionGroup) o;
		return Objects.equals(groupKey, questionGroup.groupKey) && Objects.equals(groupName, questionGroup.groupName);
	}

	@Override
	public int hashCode() {
		return Objects.hash(groupKey, groupName);
	}

	private static String randomKey() {
		int leftLimit = 48; // numeral '0'
		int rightLimit = 122; // letter 'z'

		return random.ints(leftLimit, rightLimit + 1).filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
				.limit(KEY_LENGTH).collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
				.toString();
	}

	@Override
	public String toString() {
		return "QuestionGroup [groupKey=" + groupKey + ", groupName=" + groupName + "]";
	}

	public static String getReusedGroupName(Language language) {
		switch (language) {
			case SPANISH:
				return "Reutilizadas";
			case FRENCH:
				return "";
			case ENGLISH:
			default:
				return "Reused";

		}
	}

}
