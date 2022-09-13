package es.upct.cpcd.indieopen.video.web;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.upct.cpcd.indieopen.common.BaseController;
import es.upct.cpcd.indieopen.common.exceptions.INDIeException;
import es.upct.cpcd.indieopen.unit.web.resources.TokenResource;
import es.upct.cpcd.indieopen.video.VideoService;
import es.upct.cpcd.indieopen.video.beans.EditVideoInfoBean;
import es.upct.cpcd.indieopen.video.beans.VideoBean;
import es.upct.cpcd.indieopen.video.domain.Video;
import es.upct.cpcd.indieopen.video.dto.VideoWithContent;

@RestController
@RequestMapping("/videos")
public class VideoController extends BaseController {

	private final VideoService videoService;

	@Autowired
	public VideoController(VideoService videoService) {
		this.videoService = videoService;
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public Page<VideoResource> findVideosByUser(
			@PageableDefault(sort = "createdAt", direction = Direction.DESC) Pageable page) {
		return videoService.findVideosByUser(getCurrentUserId(), page).map(VideoResource::fromVideo);
	}

	@PostMapping()
	public ResponseEntity<?> createVideo(@RequestBody VideoBean videoBean) throws INDIeException {
		Video video = videoService.createVideo(getCurrentUserId(), videoBean);
		return ResponseEntity.created(URI.create("/api/videos/" + video.getId())).build();
	}

	@DeleteMapping(value = "/{videoId}")
	public ResponseEntity<?> deleteVideo(@PathVariable int videoId) throws INDIeException {
		videoService.deleteVideo(getCurrentUserId(), videoId);
		return ResponseEntity.ok().build();
	}

	@PutMapping(value = "/{videoId}/info", produces = MediaType.APPLICATION_JSON_VALUE)
	public VideoResource editVideoInfo(@PathVariable int videoId, @RequestBody EditVideoInfoBean bean)
			throws INDIeException {
		Video video = videoService.updateVideo(getCurrentUserId(), videoId, bean.getName());
		return VideoResource.fromVideo(video);
	}

	@PostMapping(value = "/{videoId}/publish", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> publishVideo(@PathVariable int videoId) throws INDIeException {
		videoService.publishVideo(getCurrentUserId(), videoId);
		return ResponseEntity.ok().build();
	}

	@GetMapping(value = "/{videoId}/details")
	public VideoResource findVideo(@PathVariable(name = "videoId") int videoId) throws INDIeException {
		VideoWithContent video = videoService.findVideoWithContentById(getCurrentUserId(), videoId);
		return VideoResource.fromVideoContent(video);
	}

	@GetMapping(value = "/{videoId}/edit")
	public TokenResource editVideo(@PathVariable(name = "videoId") int videoId) throws INDIeException {
		String token = videoService.generateToken(getCurrentUserId(), videoId);
		return TokenResource.from(token);
	}
}
