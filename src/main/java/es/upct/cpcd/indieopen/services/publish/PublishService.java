package es.upct.cpcd.indieopen.services.publish;

import es.upct.cpcd.indieopen.unit.domain.UnitType;

public interface PublishService {

	/**
	 * Send a preview request to the remote publishing service with the
	 * evaluation/content model and the user identifier.
	 * 
	 * @param unitModel String representation of the unit model
	 * @param userBase  User identifier
	 * 
	 * @return String URL containing the preview resource
	 * 
	 * @throws PublishServiceException If an error occurs in the remote publishing
	 *                                 service
	 */
	String previewUnit(String unitModel, String userBase, UnitType unitType) throws PublishServiceException;

	/**
	 * Send a publish request to the remote publishing service with the evaluation
	 * model, the user identifier and the resource URI.
	 * 
	 * @param unitModel   String representation of the unit model
	 * @param userBase    User identifier
	 * @param resourceURI URI that the published resource already has or will have
	 * 
	 * @return String URL containing the published resource
	 * 
	 * @throws PublishServiceException If an error occurs in the remote publishing
	 *                                 service
	 */
	String publishUnit(String unitModel, String userBase, String resourceURI, UnitType unitType)
			throws PublishServiceException;

	/**
	 * Send an open-publish request to the remote publishing service with the model,
	 * the user identifier and the resource URI.
	 * 
	 * @param model       String representation of the content model
	 * @param userBase    User identifier
	 * @param resourceURI URI that the published resource already has or will have
	 * 
	 * @return String URL containing the published resource
	 * 
	 * @throws PublishServiceException If an error occurs in the remote publishing
	 *                                 service
	 */
	String openPublishUnit(String model, String userBase, String resourceURI, UnitType unitType)
			throws PublishServiceException;

	/**
	 * Send an unpublish request to the remote publishing service with user base and
	 * unit resource.
	 * 
	 * @param userBase     User base
	 * @param unitResource Unit resource id
	 * 
	 * @throws PublishServiceException
	 */
	void unpublishUnit(String userBase, String unitResource, boolean isUnitPublic) throws PublishServiceException;

	/**
	 * Builds a preview URL given a user identifier
	 * 
	 * @param userBase User identifier
	 * 
	 * @return String containing the preview URL
	 */
	String buildPreviewURL(String userBase, String timestamp);

	/**
	 * Builds a published URL resource given a user identifier and the resource URI
	 * 
	 * @param userBase    User identifier
	 * @param resourceURI Resource URI
	 * 
	 * @return String containing the published URL
	 */
	String buildPublishedURL(String userBase, String resourceURI);

	/**
	 * Builds an open-published URL resource given a user identifier and the
	 * resource URI
	 * 
	 * @param userBase    User identifier
	 * @param resourceURI Resource URI
	 * 
	 * @return String containing the open-published URL
	 */
	String buildOpenPublishedURL(String userBase, String resourceURI);

}
