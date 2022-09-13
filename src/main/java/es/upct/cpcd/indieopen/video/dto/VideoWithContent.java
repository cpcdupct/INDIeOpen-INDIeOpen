package es.upct.cpcd.indieopen.video.dto;

import org.bson.Document;
import org.json.JSONObject;

import es.upct.cpcd.indieopen.video.domain.Video;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VideoWithContent {
	private Video video;
	private Document document;

	public JSONObject getJSONObjectFromDocument() {
		return new JSONObject(document.toJson());
	}

}