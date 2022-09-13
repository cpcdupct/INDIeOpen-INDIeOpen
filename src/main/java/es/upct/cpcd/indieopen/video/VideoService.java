package es.upct.cpcd.indieopen.video;

import static es.upct.cpcd.indieopen.utils.LogUtils.log;

import javax.validation.Valid;

import org.bson.Document;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import es.upct.cpcd.indieopen.common.exceptions.INDIeException;
import es.upct.cpcd.indieopen.services.document.DocumentDBManager;
import es.upct.cpcd.indieopen.services.transform.TransformService;
import es.upct.cpcd.indieopen.token.ContentType;
import es.upct.cpcd.indieopen.token.TokenParser;
import es.upct.cpcd.indieopen.user.domain.UserData;
import es.upct.cpcd.indieopen.user.domain.UserRepository;
import es.upct.cpcd.indieopen.video.beans.VideoBean;
import es.upct.cpcd.indieopen.video.domain.Video;
import es.upct.cpcd.indieopen.video.domain.VideoRepository;
import es.upct.cpcd.indieopen.video.dto.VideoWithContent;
import lombok.extern.log4j.Log4j2;

@Service
@Validated
@Transactional(rollbackFor = INDIeException.class)
@Log4j2
public class VideoService {
	// Modules
	private final VideoCreator videoCreator;
	private final VideoPublisher videoPublisher;
	private final VideoRemover videoRemover;
	private final VideoFinder videoFinder;
	private final TokenParser tokenParser;

	@Autowired
	public VideoService(VideoRepository videoRepository, DocumentDBManager documentDbManager,
			UserRepository userRepository, TransformService transformService, TokenParser tokenParser) {
		this.videoFinder = new VideoFinder(videoRepository, documentDbManager);
		this.videoCreator = new VideoCreator(userRepository, videoRepository, documentDbManager, videoFinder);
		this.videoRemover = new VideoRemover(videoRepository, documentDbManager);
		this.videoPublisher = new VideoPublisher(videoRepository, documentDbManager, videoFinder, transformService);
		this.tokenParser = tokenParser;
	}

	public VideoWithContent findVideoWithContentById(String userId, int videoId) throws INDIeException {
		try {
			return this.videoFinder.findVideoWithContentById(userId, videoId);
		} catch (INDIeException e) {
			log(log, e);
			throw e;
		}
	}

	public Video createVideo(String userId, @Valid VideoBean videoBean) throws INDIeException {
		try {
			return this.videoCreator.createVideo(userId, videoBean);
		} catch (INDIeException e) {
			log(log, e);
			throw e;
		}
	}

	public Page<Video> findVideosByUser(String userId, Pageable page) {
		return this.videoFinder.findVideosByUser(userId, page);
	}

	public void saveVideoData(String userId, int videoId, JSONObject editorData) throws INDIeException {
		try {
			this.videoCreator.saveVideoData(userId, videoId, editorData);
		} catch (INDIeException e) {
			log(log, e);
			throw e;
		}
	}

	public void publishVideo(String userId, int videoId) throws INDIeException {
		try {
			this.videoPublisher.publishVideo(userId, videoId);
		} catch (INDIeException e) {
			log(log, e);
			throw e;
		}
	}

	public VideoWithContent findPublishedVideoByExternalId(String externalId) throws INDIeException {
		try {
			return this.videoFinder.findPublishedVideoByExternalId(externalId);
		} catch (INDIeException e) {
			log(log, e);
			throw e;
		}
	}

	public void setTransformService(TransformService transformService) {
		this.videoPublisher.setTransformService(transformService);
	}

	// MIGRATE
	public void createVideo(UserData user, Video video, Document document) throws INDIeException {
		try {
			this.videoCreator.createVideo(user, video, document);
		} catch (INDIeException e) {
			log(log, e);
			throw e;
		}
	}

	public String generateToken(String userId, int videoId) throws INDIeException {
		Video video = this.videoFinder.findVideo(videoId, userId);
		return tokenParser.generateToken(video.getId(), userId, ContentType.VIDEO);
	}

	public Video updateVideo(String userId, int videoId, String name) throws INDIeException {
		return this.videoCreator.updateVideo(userId, videoId, name);
	}

	public void deleteVideo(String userId, int videoId) throws INDIeException {
		this.videoRemover.removeVideo(userId, videoId);
	}
}
