package es.upct.cpcd.indieopen.course.domain;

import java.time.LocalDateTime;

import es.upct.cpcd.indieopen.user.domain.UserData;
import es.upct.cpcd.indieopen.utils.ObjectUtils;

public class CourseBuilder {

	public static CourseInfoStep createBuilder() {
		return new Steps();
	}

	public interface CourseInfoStep {
		WithAuthorStep withName(String name);
	}

	public interface WithAuthorStep {
		DocumentStep withAuthor(UserData author);
	}

	public interface DocumentStep {
		Build withDocumentId(String documentID);
	}

	public interface Build {
		Course build();

		Build withExternalID(String externalID);

		Build withPublishedDate(LocalDateTime publishedAt);

	}

	private static class Steps implements CourseInfoStep, Build, DocumentStep, WithAuthorStep {

		private Course course;

		private Steps() {
			this.course = new Course();
		}

		@Override
		public Course build() {
			return this.course;
		}

		@Override
		public DocumentStep withAuthor(UserData author) {
			ObjectUtils.requireNonNull(author);

			this.course.setAuthor(author);

			return this;
		}

		@Override
		public Build withExternalID(String externalID) {
			ObjectUtils.requireStringValid(externalID);

			this.course.setExternalID(externalID);
			return this;
		}

		@Override
		public Build withPublishedDate(LocalDateTime publishedAt) {
			ObjectUtils.requireNonNull(publishedAt);

			this.course.setPublishedAt(publishedAt);
			return this;
		}

		@Override
		public Build withDocumentId(String documentID) {
			ObjectUtils.requireStringValid(documentID);
			this.course.setDocumentID(documentID);

			return this;
		}

		@Override
		public WithAuthorStep withName(String name) {
			ObjectUtils.requireStringValid(name);
			this.course.setName(name);

			return this;
		}
	}
}
