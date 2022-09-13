package es.upct.cpcd.indieopen.video.web;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.upct.cpcd.indieopen.common.exceptions.INDIeException;
import es.upct.cpcd.indieopen.video.VideoService;
import es.upct.cpcd.indieopen.video.dto.VideoWithContent;

@RestController
@RequestMapping("/video/embed")
public class EmbedController {

	private static final String VIDEO_TYPE = "application/vnd.ms-sstr+xml";

	private final VideoService videoService;

	@Autowired
	public EmbedController(VideoService videoService) {
		this.videoService = videoService;
	}

	@GetMapping(value = "/{externalId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> findVideoByExternalId(@PathVariable(name = "externalId") String externalId)
			throws INDIeException {
		VideoWithContent videoWithContent = videoService.findPublishedVideoByExternalId(externalId);
		EmbedVideoResource resource = getEntityFromVideo(videoWithContent);
		JSONObject resourceInJSON = buildJson(resource);
		return ResponseEntity.status(HttpStatus.OK).body(resourceInJSON.toString());
	}

	private EmbedVideoResource getEntityFromVideo(VideoWithContent videoWithContent) {
		return new EmbedVideoResource(videoWithContent.getVideo().getVideoURL(), VIDEO_TYPE,
				videoWithContent.getJSONObjectFromDocument().getJSONArray("interactiveData"));
	}

	private JSONObject buildJson(EmbedVideoResource resource) {
		JSONObject jsonResource = new JSONObject();
		JSONObject sources = new JSONObject();
		sources.put("src", resource.getSources().getSrc());
		sources.put("type", resource.getSources().getType());
		jsonResource.put("sources", sources);
		jsonResource.put("questions", resource.getQuestions());

		return jsonResource;
	}
}
