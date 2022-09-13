package es.upct.cpcd.indieopen.video;

import static es.upct.cpcd.indieopen.video.VideoUtils.splitQuery;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import java.util.Map;

import org.bson.Document;
import org.json.JSONObject;

import es.upct.cpcd.indieopen.common.exceptions.ErrorCodes;
import es.upct.cpcd.indieopen.common.exceptions.INDIeException;
import es.upct.cpcd.indieopen.common.exceptions.INDIeException.Status;
import es.upct.cpcd.indieopen.common.exceptions.INDIeExceptionBuilder;
import es.upct.cpcd.indieopen.common.exceptions.INDIeExceptionFactory;
import es.upct.cpcd.indieopen.services.document.DocumentDBCollection;
import es.upct.cpcd.indieopen.services.document.DocumentDBManager;
import es.upct.cpcd.indieopen.services.document.DocumentDataException;
import es.upct.cpcd.indieopen.services.document.DocumentHelper;
import es.upct.cpcd.indieopen.user.domain.UserData;
import es.upct.cpcd.indieopen.user.domain.UserRepository;
import es.upct.cpcd.indieopen.utils.ObjectUtils;
import es.upct.cpcd.indieopen.utils.StringUtils;
import es.upct.cpcd.indieopen.video.beans.VideoBean;
import es.upct.cpcd.indieopen.video.domain.Video;
import es.upct.cpcd.indieopen.video.domain.VideoBuilder;
import es.upct.cpcd.indieopen.video.domain.VideoRepository;
import es.upct.cpcd.indieopen.video.dto.VideoWithContent;

class VideoCreator {

	private final UserRepository userRepository;
	private final VideoRepository videoRepository;
	private final DocumentDBManager documentDbManager;
	private final VideoFinder videoFinder;

	VideoCreator(UserRepository userRepository, VideoRepository videoRepository, DocumentDBManager documentDbManager,
			VideoFinder videoFinder) {
		this.userRepository = userRepository;
		this.videoRepository = videoRepository;
		this.documentDbManager = documentDbManager;
		this.videoFinder = videoFinder;
	}

	Video createVideo(String userId, VideoBean videoBean) throws INDIeException {
		UserData author = userRepository.findById(userId)
				.orElseThrow(() -> new INDIeExceptionBuilder("User not found  ").status(Status.USER_ERROR)
						.code(ErrorCodes.ENTITY_NOT_ACCESSIBLE).build());

		String manifestVideoUrl = extractVideoManifest(videoBean.getUrl());
		String documentId;

		try {
			// Create the document
			documentId = documentDbManager.storeDocument(DocumentDBCollection.VIDEOS,
					DocumentHelper.createEmptyVideoDocument());

			// Create the video
			Video video = VideoBuilder.createBuilder().withInfo(videoBean.getName(), manifestVideoUrl)
					.withAuthor(author).withDocumentId(documentId).build();

			// Save in the repo
			video = videoRepository.save(video);

			return video;
		} catch (DocumentDataException ex) {
			throw new INDIeExceptionBuilder("Error in createVideo  ", ex).status(Status.INTERNAL_ERROR).build();
		}
	}

	private String extractVideoManifest(String videoUrl) throws INDIeException {
		try {
			Map<String, String> map = splitQuery(new URL(videoUrl));
			String decodedMediaData = new String(Base64.getUrlDecoder().decode(map.get("v")));
			return decodedMediaData.split(",")[0];
		} catch (UnsupportedEncodingException | MalformedURLException e) {
			throw new INDIeException(e);
		}
	}

	void saveVideoData(String userId, int videoId, JSONObject editorData) throws INDIeException {
		ObjectUtils.requireNonNull(editorData);

		VideoWithContent videoWithContent = videoFinder.findVideoWithContentById(userId, videoId);

		Video video = videoWithContent.getVideo();
		Document document = videoWithContent.getDocument();
		video.setDraft(true);

		try {
			DocumentHelper.updateVideoDocument(document, editorData.getJSONArray("editorData"));
			documentDbManager.replaceDocument(DocumentDBCollection.VIDEOS, video.getDocumentID(), document);
		} catch (DocumentDataException e) {
			throw new INDIeExceptionBuilder("Error in saveVideoData", e).build();
		}
	}

	Video createVideo(UserData author, Video video, Document document) throws INDIeException {
		try {
			documentDbManager.storeDocument(DocumentDBCollection.VIDEOS, document);

			// Update relations
			video.setAuthor(author);

			// Save in the repo
			video = videoRepository.save(video);

			return video;
		} catch (DocumentDataException e) {
			throw new INDIeExceptionBuilder("Error in createVideo", e).build();
		}
	}

	Video updateVideo(String currentUserId, int videoId, String name) throws INDIeException {
		if (!StringUtils.isStringValid(name))
			throw INDIeExceptionFactory.createUserRequestError(name);

		Video video = videoRepository.findByIdAndAuthorId(videoId, currentUserId)
				.orElseThrow(() -> new INDIeExceptionBuilder("Video not found").status(Status.USER_ERROR)
						.code(ErrorCodes.ENTITY_NOT_ACCESSIBLE).build());

		video.setName(name);

		return video;
	}
}
