package es.upct.cpcd.indieopen.video.web;

import org.json.JSONArray;

import lombok.Getter;

@Getter
public class EmbedVideoResource {

	private final SourcesResource sources;
	private final JSONArray questions;

	public EmbedVideoResource(String src, String type, JSONArray questions) {
		this.sources = new SourcesResource(src, type);
		this.questions = questions;
	}

	@Getter
	static class SourcesResource {
		private final String src;
		private final String type;

		private SourcesResource(String src, String type) {
			this.src = src;
			this.type = type;
		}

	}

}
