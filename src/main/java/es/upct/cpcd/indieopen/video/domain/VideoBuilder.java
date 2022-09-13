package es.upct.cpcd.indieopen.video.domain;

import java.time.LocalDateTime;

import es.upct.cpcd.indieopen.user.domain.UserData;
import es.upct.cpcd.indieopen.utils.ObjectUtils;

public class VideoBuilder {

	public static VideoInfoStep createBuilder() {
		return new Steps();
	}

	public interface VideoInfoStep {
		WithAuthorStep withInfo(String name, String videoURL);
	}

	public interface WithAuthorStep {
		DocumentStep withAuthor(UserData author);
	}

	public interface DocumentStep {
		Build withDocumentId(String documentID);
	}

	public interface Build {
		Video build();

		Build withExternalID(String externalID);

		Build withPublishedDate(LocalDateTime publishedAt);

	}

	private static class Steps implements VideoInfoStep, WithAuthorStep, DocumentStep, Build {
		private Video video;

		private Steps() {
			this.video = new Video();
		}

		@Override
		public Video build() {
			return this.video;
		}

		@Override
		public Build withExternalID(String externalID) {
			ObjectUtils.requireStringValid(externalID);
			this.video.setExternalID(externalID);

			return this;
		}

		@Override
		public Build withDocumentId(String documentID) {
			ObjectUtils.requireStringValid(documentID);
			this.video.setDocumentID(documentID);

			return this;
		}

		@Override
		public WithAuthorStep withInfo(String name, String videoURL) {
			ObjectUtils.requireStringsValid(name, videoURL);

			this.video.setName(name);
			this.video.setVideoURL(videoURL);

			return this;
		}

		@Override
		public DocumentStep withAuthor(UserData author) {
			ObjectUtils.requireNonNull(author);

			this.video.setAuthor(author);

			return this;
		}

		@Override
		public Build withPublishedDate(LocalDateTime publishedAt) {
			ObjectUtils.requireNonNull(publishedAt);

			this.video.setPublishedAt(publishedAt);

			return this;
		}

	}
}