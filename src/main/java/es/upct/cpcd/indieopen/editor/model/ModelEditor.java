package es.upct.cpcd.indieopen.editor.model;

import java.util.Optional;

import org.json.JSONObject;

import es.upct.cpcd.indieopen.common.Language;
import es.upct.cpcd.indieopen.services.transform.TransformMode;
import es.upct.cpcd.indieopen.token.ContentType;
import es.upct.cpcd.indieopen.unit.domain.CreativeCommons;
import lombok.AccessLevel;
import lombok.Getter;

@Getter
public class ModelEditor {
	private final ContentType type;
	private final String name;
	private final String author;
	private String email;

	@Getter(value = AccessLevel.NONE)
	private Language language;
	private final JSONObject instance;
	@Getter(value = AccessLevel.NONE)
	private TransformMode mode;
	@Getter(value = AccessLevel.NONE)
	private CreativeCommons license;
	@Getter(value = AccessLevel.NONE)
	private String institution;
	@Getter(value = AccessLevel.NONE)
	private String theme;
	@Getter(value = AccessLevel.NONE)
	private String resourceId;
	@Getter(value = AccessLevel.NONE)
	private boolean analytics;

	public ModelEditor(ContentType type, String name, String author, JSONObject instance, String email) {
		this.type = type;
		this.name = name;
		this.author = author;
		this.instance = instance;
		this.email = email;
	}

	public void setMode(TransformMode mode) {
		this.mode = mode;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	public void setInstitution(String institution) {
		this.institution = institution;
	}

	public void setCreativeCommonsLicense(CreativeCommons license) {
		this.license = license;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

	public Optional<TransformMode> getMode() {
		return Optional.ofNullable(mode);
	}

	public Optional<CreativeCommons> getLicense() {
		return Optional.ofNullable(license);
	}

	public Optional<String> getInstitution() {
		return Optional.ofNullable(institution);
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	public Optional<Language> getLanguage() {
		return Optional.ofNullable(language);
	}

	public Optional<String> getTheme() {
		return Optional.ofNullable(theme);
	}

	public Optional<String> getResourceId() {
		return Optional.ofNullable(resourceId);
	}

	public Optional<Boolean> isAnalytics() {
		return Optional.of(analytics);
	}

	public void setAnalytics(boolean analytics) {
		this.analytics = analytics;
	}

}
