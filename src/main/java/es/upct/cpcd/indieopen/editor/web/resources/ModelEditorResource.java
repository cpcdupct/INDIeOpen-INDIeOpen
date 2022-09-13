package es.upct.cpcd.indieopen.editor.web.resources;

import org.json.JSONWriter;

import es.upct.cpcd.indieopen.editor.model.ModelEditor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ModelEditorResource {
	private String instance;
	private String type;
	private String name;
	private String author;
	private String email;
	private String license;
	private String language;
	private String institution;
	private String theme;
	private boolean analytics;

	public static ModelEditorResource from(ModelEditor modelEditor) {
		ModelEditorResource resource = new ModelEditorResource();
		resource.author = modelEditor.getAuthor();
		resource.license = modelEditor.getLicense().isPresent() ? modelEditor.getLicense().get().getValue() : null;
		resource.email = modelEditor.getEmail();
		resource.name = modelEditor.getName();
		resource.institution = modelEditor.getInstitution().orElse(null);
		resource.type = modelEditor.getType().getValue();
		resource.instance = JSONWriter.valueToString(modelEditor.getInstance());
		resource.language = modelEditor.getLanguage().isPresent() ? modelEditor.getLanguage().get().getValue() : null;
		resource.theme = modelEditor.getTheme().orElse(null);
		resource.analytics = modelEditor.isAnalytics().get();
		return resource;
	}
}
