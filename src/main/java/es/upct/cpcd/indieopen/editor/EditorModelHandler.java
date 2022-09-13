package es.upct.cpcd.indieopen.editor;

import org.json.JSONObject;

import es.upct.cpcd.indieopen.common.exceptions.ErrorCodes;
import es.upct.cpcd.indieopen.common.exceptions.ErrorField;
import es.upct.cpcd.indieopen.common.exceptions.INDIeException;
import es.upct.cpcd.indieopen.common.exceptions.INDIeException.Status;
import es.upct.cpcd.indieopen.common.exceptions.INDIeExceptionBuilder;
import es.upct.cpcd.indieopen.course.CourseService;
import es.upct.cpcd.indieopen.course.domain.Course;
import es.upct.cpcd.indieopen.course.dto.CourseWithContent;
import es.upct.cpcd.indieopen.editor.model.ModelEditor;
import es.upct.cpcd.indieopen.services.userinfo.UserInfo;
import es.upct.cpcd.indieopen.services.userinfo.UserInfoService;
import es.upct.cpcd.indieopen.token.ContentType;
import es.upct.cpcd.indieopen.token.ModelToken;
import es.upct.cpcd.indieopen.token.TokenParser;
import es.upct.cpcd.indieopen.unit.UnitService;
import es.upct.cpcd.indieopen.unit.domain.Unit;
import es.upct.cpcd.indieopen.unit.domain.UnitType;
import es.upct.cpcd.indieopen.unit.dto.UnitWithModel;
import es.upct.cpcd.indieopen.video.VideoService;
import es.upct.cpcd.indieopen.video.domain.Video;
import es.upct.cpcd.indieopen.video.dto.VideoWithContent;

class EditorModelHandler {

	private final TokenParser tokenParser;
	private final UnitService unitService;
	private final VideoService videoService;
	private final UserInfoService userInfoService;
	private final CourseService courseService;

	EditorModelHandler(TokenParser tokenParser, UnitService unitService, VideoService videoService,
			UserInfoService userInfoService, CourseService courseService) {
		this.tokenParser = tokenParser;
		this.unitService = unitService;
		this.videoService = videoService;
		this.userInfoService = userInfoService;
		this.courseService = courseService;
	}

	ModelEditor getModelEditor(String token) throws INDIeException {
		ModelToken modelToken = tokenParser.parseToken(token);
		UserInfo userInfo = userInfoService.findById(modelToken.getUser()).orElseThrow();

		if (modelToken.getType() == ContentType.CONTENT || modelToken.getType() == ContentType.EVALUATION) {
			return getModellEditorFromUnit(modelToken, userInfo);
		} else if (modelToken.getType() == ContentType.VIDEO) {
			return getModelEditorFromVideo(modelToken, userInfo);
		} else if (modelToken.getType() == ContentType.COURSE)
			return getModelEditorFromCourse(modelToken, userInfo);

		return null;
	}

	private ModelEditor getModelEditorFromCourse(ModelToken modelToken, UserInfo userInfo) throws INDIeException {
		CourseWithContent courseWithContent = courseService.findCourseWithContentById(modelToken.getUser(),
				modelToken.getEntity());
		Course course = courseWithContent.getCourse();

		return new ModelEditor(ContentType.COURSE, course.getName(), userInfo.getCompleteName(),
				courseWithContent.getJSONObjectFromDocument().getJSONObject("editor"), course.getAuthor().getEmail());
	}

	private ModelEditor getModelEditorFromVideo(ModelToken modelToken, UserInfo userInfo) throws INDIeException {
		VideoWithContent videoWithContent = videoService.findVideoWithContentById(modelToken.getUser(),
				modelToken.getEntity());
		Video video = videoWithContent.getVideo();

		JSONObject instance = createVideoInstance(videoWithContent);

		return new ModelEditor(ContentType.VIDEO, video.getName(), userInfo.getCompleteName(), instance,
				video.getAuthor().getEmail());
	}

	private JSONObject createVideoInstance(VideoWithContent videoWithContent) {
		JSONObject instance = new JSONObject();

		JSONObject videoSources = new JSONObject();
		videoSources.put("type", "application/vnd.ms-sstr+xml");
		videoSources.put("src", videoWithContent.getVideo().getVideoURL());

		instance.put("editorData", videoWithContent.getJSONObjectFromDocument().getJSONArray("editorData"));
		instance.put("videosource", videoSources);

		return instance;
	}

	private ModelEditor getModellEditorFromUnit(ModelToken modelToken, UserInfo userInfo) throws INDIeException {
		UnitWithModel unitWithModel = this.unitService.getModelByUnitId(modelToken.getUser(), modelToken.getEntity());
		Unit unit = unitWithModel.getUnit();

		ModelEditor modelEditor = new ModelEditor(typeOf(unit.getUnitType()), unit.getName(),
				userInfo.getCompleteName(), unitWithModel.getJSONObjectFromDocument(), unit.getAuthor().getEmail());

		// Fields for unit
		modelEditor.setLanguage(unit.getLanguage());
		modelEditor.setInstitution(unit.getAuthor().getInstitution());
		modelEditor.setCreativeCommonsLicense(unit.getCreativeCommons());
		modelEditor.setTheme(unit.getTheme());
		modelEditor.setResourceId(unit.getUnitResourceId());

		return modelEditor;
	}

	private ContentType typeOf(UnitType unitType) {
		if (unitType == UnitType.CONTENT)
			return ContentType.CONTENT;

		return ContentType.EVALUATION;
	}

	void save(String token, String plainModel) throws INDIeException {
		ModelToken modelToken = this.tokenParser.parseToken(token);
		JSONObject model = getInstance(plainModel);

		if (isContentUnit(modelToken, model) || isEvaluationUnit(modelToken, model)) {
			this.unitService.saveModelOfUnit(modelToken.getUser(), modelToken.getEntity(), model);
		} else if (isVideo(modelToken, model)) {
			this.videoService.saveVideoData(modelToken.getUser(), modelToken.getEntity(), model);
		} else if (isCourse(modelToken)) {
			this.courseService.saveCourseData(modelToken.getUser(), modelToken.getEntity(), model);
		} else
			throw new INDIeExceptionBuilder(model + " is not valid").code(ErrorCodes.WRONG_PARAMS)
					.status(Status.USER_ERROR).errorFields(ErrorField.listOf("model", "Token is not valid")).build();
	}

	private boolean isCourse(ModelToken modelToken) {
		return modelToken.getType() == ContentType.COURSE;
	}

	private JSONObject getInstance(String plainModel) {
		JSONObject object = new JSONObject(plainModel);
		return object.getJSONObject("instance");
	}

	private boolean isVideo(ModelToken modelToken, JSONObject model) {
		if (modelToken.getType() != ContentType.VIDEO)
			return false;

		return (model.has("editorData"));
	}

	private boolean isEvaluationUnit(ModelToken modelToken, JSONObject model) {
		if (modelToken.getType() != ContentType.EVALUATION)
			return false;

		return (model.has("evaluation"));
	}

	private boolean isContentUnit(ModelToken modelToken, JSONObject model) {
		if (modelToken.getType() != ContentType.CONTENT)
			return false;

		return (model.has("sections") && model.has("version"));
	}

}
