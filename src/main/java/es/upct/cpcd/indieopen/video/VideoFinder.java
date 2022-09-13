package es.upct.cpcd.indieopen.video;

import org.bson.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import es.upct.cpcd.indieopen.common.exceptions.ErrorCodes;
import es.upct.cpcd.indieopen.common.exceptions.INDIeException;
import es.upct.cpcd.indieopen.common.exceptions.INDIeException.Status;
import es.upct.cpcd.indieopen.common.exceptions.INDIeExceptionBuilder;
import es.upct.cpcd.indieopen.services.document.DocumentDBCollection;
import es.upct.cpcd.indieopen.services.document.DocumentDBManager;
import es.upct.cpcd.indieopen.video.domain.Video;
import es.upct.cpcd.indieopen.video.domain.VideoRepository;
import es.upct.cpcd.indieopen.video.dto.VideoWithContent;

class VideoFinder {
	private final VideoRepository videoRepository;
	private final DocumentDBManager documentDbManager;

	VideoFinder(VideoRepository videoRepository, DocumentDBManager documentDbManager) {
		this.videoRepository = videoRepository;
		this.documentDbManager = documentDbManager;
	}

	Page<Video> findVideosByUser(String userId, Pageable page) {
		return videoRepository.findByAuthorId(userId, page);
	}

	Video findVideo(int videoId, String userId) throws INDIeException {
		return videoRepository.findByIdAndAuthorId(videoId, userId)
				.orElseThrow(() -> new INDIeExceptionBuilder("External video not found").status(Status.USER_ERROR)
						.code(ErrorCodes.ENTITY_NOT_ACCESSIBLE).build());

	}

	VideoWithContent findPublishedVideoByExternalId(String externalId) throws INDIeException {
		Video video = videoRepository.findByExternalID(externalId)
				.orElseThrow(() -> new INDIeExceptionBuilder("External video not found").status(Status.USER_ERROR)
						.code(ErrorCodes.ENTITY_NOT_ACCESSIBLE).build());

		if (!video.isPublished()) {
			throw new INDIeExceptionBuilder("Video not published").status(Status.USER_ERROR)
					.code(ErrorCodes.ENTITY_NOT_ACCESSIBLE).build();
		}

		Document document = documentDbManager.findDocument(DocumentDBCollection.VIDEOS, video.getDocumentID())
				.orElseThrow(() -> new INDIeExceptionBuilder("Video document not found").status(Status.INTERNAL_ERROR)
						.code(ErrorCodes.ENTITY_NOT_ACCESSIBLE).build());

		return new VideoWithContent(video, document);
	}

	VideoWithContent findVideoWithContentById(String userId, int videoId) throws INDIeException {
		Video video = videoRepository.findByIdAndAuthorId(videoId, userId)
				.orElseThrow(() -> new INDIeExceptionBuilder("External video not found").status(Status.USER_ERROR)
						.code(ErrorCodes.ENTITY_NOT_ACCESSIBLE).build());

		Document document = documentDbManager.findDocument(DocumentDBCollection.VIDEOS, video.getDocumentID())
				.orElseThrow(() -> new INDIeExceptionBuilder("Video document not found").status(Status.INTERNAL_ERROR)
						.code(ErrorCodes.ENTITY_NOT_ACCESSIBLE).build());

		return new VideoWithContent(video, document);
	}

}
