package es.upct.cpcd.indieopen.services.transform;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import es.upct.cpcd.indieopen.editor.model.ModelEditor;
import es.upct.cpcd.indieopen.token.ContentType;
import es.upct.cpcd.indieopen.unit.domain.Unit;
import es.upct.cpcd.indieopen.unit.domain.UnitMode;
import es.upct.cpcd.indieopen.unit.domain.UnitType;
import es.upct.cpcd.indieopen.unit.dto.UnitWithModel;
import es.upct.cpcd.indieopen.video.domain.Video;
import es.upct.cpcd.indieopen.video.dto.VideoWithContent;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class TransformService {

	@Value("${editor.base_url}")
	private String baseURL;

	private static final String ACTION_URL = "indieopen/transform";

	public JSONArray transformVideo(VideoWithContent videoWithContent) throws TransformServiceException {
		Video video = videoWithContent.getVideo();
		ModelEditor model = new ModelEditor(ContentType.VIDEO, video.getName(), video.getAuthor().getCompleteName(),
				videoWithContent.getJSONObjectFromDocument(), video.getAuthor().getEmail());
		JSONObject requestResponse = makeRequest(model);
		return requestResponse.getJSONArray("content");
	}

	public String transformUnit(UnitWithModel unitWithModel, TransformMode mode) throws TransformServiceException {
		Unit unit = unitWithModel.getUnit();
		ContentType type = unit.getUnitType() == UnitType.CONTENT ? ContentType.CONTENT : ContentType.EVALUATION;

		// In case of being a copied unit, the original author must be provided
		String author = unit.getMode() == UnitMode.COPIED ? unit.getOriginalUnitStamp().getAuthorName()
				: unit.getAuthor().getCompleteName();

		String email = unit.getMode() == UnitMode.COPIED ? unit.getOriginalUnitStamp().getAuthorEmail()
				: unit.getAuthor().getEmail();

		ModelEditor model = new ModelEditor(type, unit.getName(), author, unitWithModel.getJSONObjectFromDocument(),
				email);

		model.setLanguage(unit.getLanguage());
		model.setCreativeCommonsLicense(unit.getCreativeCommons());
		model.setInstitution(unit.getAuthor().getInstitution());
		model.setTheme(unit.getTheme());
		model.setResourceId(unit.getUnitResourceId());
		model.setAnalytics(unit.isAnalytics());
		model.setMode(mode);

		JSONObject requestResponse = makeRequest(model);
		return requestResponse.getString("content");
	}

	private JSONObject makeRequest(ModelEditor model) throws TransformServiceException {
		try (CloseableHttpClient client = HttpClients.createDefault()) {
			String url = baseURL + ACTION_URL;
			HttpPost httpPost = new HttpPost(url);
			String entity = getStringEntity(model);
			StringEntity requestEntity = new StringEntity(entity, org.apache.http.entity.ContentType.APPLICATION_JSON);
			httpPost.setEntity(requestEntity);
			CloseableHttpResponse response = client.execute(httpPost);
			int statusCodeResponse = response.getStatusLine().getStatusCode();

			if (isSuccess(statusCodeResponse)) {
				return jsonEntity(response.getEntity());
			} else {
				throw new TransformServiceException("Error code " + statusCodeResponse);
			}

		} catch (IOException e) {
			log.error("Error in makeRequest ", e);
			throw new TransformServiceException(e);
		}
	}

	private boolean isSuccess(int statusCodeResponse) {
		return (statusCodeResponse == HttpServletResponse.SC_CREATED
				|| statusCodeResponse == HttpServletResponse.SC_OK);
	}

	private JSONObject jsonEntity(HttpEntity entity) throws TransformServiceException {
		try {
			return new JSONObject(EntityUtils.toString(entity));
		} catch (Exception e) {
			log.error(e);
			throw new TransformServiceException(e);
		}
	}

	private String getStringEntity(ModelEditor model) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("type", model.getType().getValue());
		jsonObject.put("name", model.getName());
		jsonObject.put("author", model.getAuthor());
		jsonObject.put("email", model.getEmail());
		jsonObject.put("instance", model.getInstance());

		if (model.getLanguage().isPresent())
			jsonObject.put("language", model.getLanguage().get().getValue());

		if (model.getMode().isPresent())
			jsonObject.put("mode", model.getMode().get().getValue());

		if (model.getInstitution().isPresent())
			jsonObject.put("institution", model.getInstitution().get());

		if (model.getLicense().isPresent())
			jsonObject.put("license", model.getLicense().get().getValue());

		if (model.getTheme().isPresent())
			jsonObject.put("theme", model.getTheme().get());

		if (model.getResourceId().isPresent())
			jsonObject.put("resourceId", model.getResourceId().get());

		if (model.isAnalytics().isPresent())
			jsonObject.put("analytics", model.isAnalytics().get());

		return jsonObject.toString();
	}

}
