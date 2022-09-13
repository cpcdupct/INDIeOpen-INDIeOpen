package es.upct.cpcd.indieopen.video.web;

import es.upct.cpcd.indieopen.utils.DateUtils;
import es.upct.cpcd.indieopen.video.domain.Video;
import es.upct.cpcd.indieopen.video.dto.VideoWithContent;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class VideoResource {

	private int id;
	private String name;
	private String createdAt;
	private String documentID;
	private String videoURL;
	private String externalID;
	private boolean draft;
	private String publishedAt;

	public static VideoResource fromVideoContent(VideoWithContent video) {
		return fromVideo(video.getVideo());
	}

	public static VideoResource fromVideo(Video video) {
		VideoResource resource = new VideoResource();

		resource.id = video.getId();
		resource.name = video.getName();
		resource.createdAt = DateUtils.dateToISOString(video.getCreatedAt());
		resource.documentID = video.getDocumentID();
		resource.videoURL = video.getVideoURL();
		resource.externalID = video.getExternalID();
		resource.draft = video.isDraft();
		resource.publishedAt = DateUtils.dateToISOString(video.getPublishedAt());

		return resource;
	}

}