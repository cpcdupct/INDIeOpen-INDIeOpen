package es.upct.cpcd.indieopen.editor;

import static es.upct.cpcd.indieopen.utils.LogUtils.log;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.upct.cpcd.indieopen.common.exceptions.INDIeException;
import es.upct.cpcd.indieopen.course.CourseService;
import es.upct.cpcd.indieopen.editor.model.ModelEditor;
import es.upct.cpcd.indieopen.services.publish.PublishService;
import es.upct.cpcd.indieopen.services.userinfo.UserInfoService;
import es.upct.cpcd.indieopen.token.ModelToken;
import es.upct.cpcd.indieopen.token.TokenParser;
import es.upct.cpcd.indieopen.unit.UnitService;
import es.upct.cpcd.indieopen.video.VideoService;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
@Transactional(rollbackFor = INDIeException.class)
public class EditorService {
	private final TokenParser tokenParser;
	private final EditorModelHandler editorModelHandler;
	private final EditorPublisher editorPublisher;

	@Autowired
	public EditorService(TokenParser tokenParser, UnitService unitService, VideoService videoService,
			PublishService publishService, UserInfoService userInfoService, CourseService courseService) {
		this.editorModelHandler = new EditorModelHandler(tokenParser, unitService, videoService, userInfoService,
				courseService);
		this.tokenParser = tokenParser;
		this.editorPublisher = new EditorPublisher(tokenParser, publishService, unitService);
	}

	public ModelToken getTokenInfo(String token) throws INDIeException {
		try {
			return this.tokenParser.parseToken(token);
		} catch (INDIeException e) {
			log(log, e);
			throw e;
		}
	}

	public ModelEditor getModelEditor(String token) throws INDIeException {
		try {
			return this.editorModelHandler.getModelEditor(token);
		} catch (INDIeException e) {
			log(log, e);
			throw e;
		}
	}

	public String previewUnit(String token, String model) throws INDIeException {
		try {
			return this.editorPublisher.preview(token, model);
		} catch (INDIeException e) {
			log(log, e);
			throw e;
		}
	}

	public void save(String token, String model) throws INDIeException {
		try {
			this.editorModelHandler.save(token, model);
		} catch (INDIeException e) {
			log(log, e);
			throw e;
		}
	}

}
