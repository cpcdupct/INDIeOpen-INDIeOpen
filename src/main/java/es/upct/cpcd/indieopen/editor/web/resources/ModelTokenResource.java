package es.upct.cpcd.indieopen.editor.web.resources;

import es.upct.cpcd.indieopen.token.ModelToken;
import es.upct.cpcd.indieopen.utils.DateUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ModelTokenResource {
	private String expireAt;
	private String type;

	public static ModelTokenResource fromModelToken(ModelToken modelToken) {
		ModelTokenResource resource = new ModelTokenResource();
		resource.expireAt = DateUtils.dateToISOString(modelToken.getExpireAt());
		resource.type = modelToken.getType().getValue();
		return resource;
	}

}
