package es.upct.cpcd.indieopen.services.publish;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.cpcd.microservices.app.servicescommons.models.requests.LearningUnitRequest;
import com.cpcd.microservices.app.servicescommons.models.requests.LearningUnitRequest.UnitTypes;

import es.upct.cpcd.indieopen.unit.domain.UnitType;
import feign.FeignException;

@Service
public class PublishServiceImpl implements PublishService {

	/**
	 * Base URL of the units hosting service
	 */
	@Value("${publish.private_url}")
	private String privateServerUrl;

	@Value("${publish.public_url}")
	private String publicServerUrl;

	private static final String PREVIEW_MODE = "preview";

	private final GeneratorFeignClient generatorFeignClient;
	private final TimestampGenerator timestampGenerator;

	@Autowired
	public PublishServiceImpl(GeneratorFeignClient generatorFeignClient, TimestampGenerator timestampGenerator) {
		this.generatorFeignClient = generatorFeignClient;
		this.timestampGenerator = timestampGenerator;
	}

	/**
	 * {@inheritDoc}
	 */
	public String previewUnit(String unitModel, String userBase, UnitType unitType) throws PublishServiceException {
		try {
			LearningUnitRequest unitRequest = new LearningUnitRequest(unitModel, PREVIEW_MODE, userBase,
					UnitTypes.PRIVATE);

			if (unitType == UnitType.CONTENT)
				this.generatorFeignClient.generateContentUnit(unitRequest);
			else
				this.generatorFeignClient.generateEvaluationUnit(unitRequest);

			String timestamp = this.timestampGenerator.generateTimestamp(getPreviewExpirationDate(), userBase);

			return buildPreviewURL(userBase, timestamp);
		} catch (FeignException fe) {
			throw new PublishServiceException(fe);
		}
	}

	private LocalDateTime getPreviewExpirationDate() {
		return LocalDateTime.now(ZoneId.of("Europe/Madrid")).plusHours(1);
	}

	/**
	 * {@inheritDoc}
	 */
	public String publishUnit(String unitModel, String userBase, String resourceURI, UnitType unitType)
			throws PublishServiceException {
		try {
			LearningUnitRequest unitRequest = new LearningUnitRequest(unitModel, resourceURI, userBase,
					UnitTypes.PRIVATE);

			if (unitType == UnitType.CONTENT)
				this.generatorFeignClient.generateContentUnit(unitRequest);
			else
				this.generatorFeignClient.generateEvaluationUnit(unitRequest);

			return buildPublishedURL(userBase, resourceURI);
		} catch (FeignException fe) {
			throw new PublishServiceException(fe);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public String openPublishUnit(String unitModel, String userBase, String resourceURI, UnitType unitType)
			throws PublishServiceException {
		try {
			LearningUnitRequest unitRequest = new LearningUnitRequest(unitModel, resourceURI, userBase,
					UnitTypes.PUBLIC);

			if (unitType == UnitType.CONTENT)
				this.generatorFeignClient.generateContentUnit(unitRequest);
			else
				this.generatorFeignClient.generateEvaluationUnit(unitRequest);

			return buildOpenPublishedURL(userBase, resourceURI);
		} catch (FeignException fe) {
			throw new PublishServiceException(fe);
		}
	}

	@Override
	public void unpublishUnit(String userBase, String unitResource, boolean isUnitPublic)
			throws PublishServiceException {
		try {
			UnitTypes type = isUnitPublic ? UnitTypes.PUBLIC : UnitTypes.PRIVATE;
			this.generatorFeignClient.deleteUnit(unitResource, userBase, type);
		} catch (FeignException fe) {
			throw new PublishServiceException(fe);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String buildPreviewURL(String userBase, String timestamp) {
		return (privateServerUrl + "/" + userBase + "/preview/?vt=" + timestamp);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String buildPublishedURL(String userBase, String resourceURI) {
		return (privateServerUrl + "/" + userBase + "/" + resourceURI + "/");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String buildOpenPublishedURL(String userBase, String resourceURI) {
		return (publicServerUrl + "/" + userBase + "/" + resourceURI + "/");
	}
}
