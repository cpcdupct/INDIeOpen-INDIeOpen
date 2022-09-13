package es.upct.cpcd.indieopen.unit;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.bson.Document;
import org.json.JSONObject;

import es.upct.cpcd.indieopen.category.domain.Category;
import es.upct.cpcd.indieopen.category.domain.CategoryRepository;
import es.upct.cpcd.indieopen.common.exceptions.INDIeException;
import es.upct.cpcd.indieopen.common.exceptions.INDIeExceptionFactory;
import es.upct.cpcd.indieopen.educationalcontext.domain.EducationalContext;
import es.upct.cpcd.indieopen.educationalcontext.domain.EducationalContextRepository;
import es.upct.cpcd.indieopen.services.document.DocumentDBManager;
import es.upct.cpcd.indieopen.services.document.DocumentDBManagerImpl;
import es.upct.cpcd.indieopen.services.document.DocumentDataException;
import es.upct.cpcd.indieopen.services.document.DocumentHelper;
import es.upct.cpcd.indieopen.unit.beans.CreateUnitBean;
import es.upct.cpcd.indieopen.unit.beans.UpdateUnitInfoBean;
import es.upct.cpcd.indieopen.unit.domain.AgeRange;
import es.upct.cpcd.indieopen.unit.domain.Unit;
import es.upct.cpcd.indieopen.unit.domain.UnitBuilder;
import es.upct.cpcd.indieopen.unit.domain.UnitBuilder.Build;
import es.upct.cpcd.indieopen.unit.domain.UnitType;
import es.upct.cpcd.indieopen.unit.domain.repository.UnitRepository;
import es.upct.cpcd.indieopen.user.domain.UserData;
import es.upct.cpcd.indieopen.user.domain.UserRepository;
import es.upct.cpcd.indieopen.utils.StringUtils;

class UnitCreator {
	private final UserRepository userRepository;
	private final UnitRepository unitRepository;
	private final CategoryRepository categoryRepository;
	private final DocumentDBManager documentDbManager;
	private final EducationalContextRepository educationalContextRepository;

	UnitCreator(UserRepository userRepository, UnitRepository unitRepository, CategoryRepository categoryRepository,
			DocumentDBManager documentDbManager, EducationalContextRepository educationalContextRepository) {
		this.userRepository = userRepository;
		this.unitRepository = unitRepository;
		this.categoryRepository = categoryRepository;
		this.documentDbManager = documentDbManager;
		this.educationalContextRepository = educationalContextRepository;
	}

	Unit createUnit(String userId, CreateUnitBean createUnitBean) throws INDIeException {
		UserData author = userRepository.findById(userId)
				.orElseThrow(() -> INDIeExceptionFactory.createUserNotFoundException(userId));

		Category category = categoryRepository.findById(createUnitBean.getCategory())
				.orElseThrow(() -> INDIeExceptionFactory.createCategoryNotFoundException(createUnitBean.getCategory()));

		List<EducationalContext> contexts = educationalContextRepository
				.findAllById(Arrays.asList(createUnitBean.getEducationalContext()));

		try {
			String documentId = documentDbManager.storeDocument(UnitType.collectionOf(createUnitBean.getUnitTypeEnum()),
					DocumentHelper.createEmptyUnitDocument(createUnitBean.getUnitTypeEnum()));

			// Create the unit with the builder
			Build unitBuilder = buildFromBean(author, category, contexts, documentId, createUnitBean);
			Unit newUnit = unitBuilder.build();

			unitRepository.save(newUnit);

			// Return the unit
			return newUnit;
		} catch (DocumentDataException exception) {
			throw INDIeExceptionFactory.createInternalException(exception);
		}
	}

	private Build buildFromBean(UserData author, Category category, List<EducationalContext> context, String documentId,
			CreateUnitBean createUnitBean) {
		Build unitBuilder = UnitBuilder.createBuilder().withAuthor(author).withType(createUnitBean.getUnitTypeEnum())
				.withUnitInfo(createUnitBean.getName(), createUnitBean.getShortDescription(),
						createUnitBean.getLanguageEnum())
				.withCategory(category).withDefaultLicense().withDefaultMode().withEducationalContext(context)
				.withDefaultCreativeCommons().withDocumentId(documentId)
				.withAgeRange(createUnitBean.getAgeRangeMin(), createUnitBean.getAgeRangeMax());

		if (createUnitBean.getTags().length > 0)
			unitBuilder.withTags(createUnitBean.getTags());

		if (StringUtils.isStringValid(createUnitBean.getCover()))
			unitBuilder.withCover(createUnitBean.getCover());

		if (StringUtils.isStringValid(createUnitBean.getLongDescription()))
			unitBuilder.withLongDescription(createUnitBean.getLongDescription());

		if (StringUtils.isStringValid(createUnitBean.getTheme()))
			unitBuilder.withTheme(createUnitBean.getTheme());

		return unitBuilder;
	}

	Unit updateUnit(String userId, int unitId, UpdateUnitInfoBean updateUnitBean) throws INDIeException {
		Unit unit = unitRepository.findByIdAndAuthorId(unitId, userId)
				.orElseThrow(() -> INDIeExceptionFactory.createUnitNotFound(unitId));

		Category category = categoryRepository.findById(updateUnitBean.getCategory())
				.orElseThrow(() -> INDIeExceptionFactory.createCategoryNotFoundException(updateUnitBean.getCategory()));

		List<EducationalContext> educationalContexts = educationalContextRepository
				.findAllById(Arrays.asList(updateUnitBean.getEducationalContext()));

		// Check which fields are going to be changed
		if (needsRepuiblish(unit, updateUnitBean))
			unit.setDraft(true);

		// Update unit fields
		unit.setCategory(category);
		unit.setCover(updateUnitBean.getCover());
		unit.setLanguage(updateUnitBean.getLanguageEnum());
		unit.setShortDescription(updateUnitBean.getShortDescription());
		unit.setLongDescription(updateUnitBean.getLongDescription());
		unit.setName(updateUnitBean.getName());
		unit.setAgeRange(new AgeRange(updateUnitBean.getAgeRangeMin(), updateUnitBean.getAgeRangeMax()));

		if (updateUnitBean.getTags() != null && (updateUnitBean.getTags().length > 0))
			unit.setRawTagsFromArray(updateUnitBean.getTags());

		unit.setEducationalContexts(educationalContexts);
		unit.setTheme(updateUnitBean.getTheme());

		return unit;
	}

	private boolean needsRepuiblish(Unit unit, UpdateUnitInfoBean updateUnitBean) {
		return (unit.getLanguage() != updateUnitBean.getLanguageEnum()
				|| !unit.getTheme().equals(updateUnitBean.getTheme())
				|| !unit.getName().equals(updateUnitBean.getName()));
	}

	Unit createSampleUnit(String userId, Unit unit) throws INDIeException, IOException {
		userRepository.findById(userId).orElseThrow(() -> INDIeExceptionFactory.createUserNotFoundException(userId));

		try {
			// Create the document in the document db
			String documentId = documentDbManager.storeDocument(UnitType.collectionOf(unit.getUnitType()),
					DocumentHelper.createDocument(unit.getUnitType(), DocumentDBManagerImpl
							.transformArrayIntoDBObjectList(dummyEditorData().getJSONArray("sections"))));

			// Update the relations
			unit.setDocumentId(documentId);

			unitRepository.save(unit);

			// Return the unit
			return unit;
		} catch (DocumentDataException exception) {
			throw INDIeExceptionFactory.createInternalException(exception);
		}
	}

	private JSONObject dummyEditorData() throws IOException {
		File file = new File("dummyTests/unit/content-dummEditorData.json");
		FileInputStream fis = new FileInputStream(file);
		return new JSONObject(IOUtils.toString(fis, StandardCharsets.UTF_8));
	}

	Unit createUnit(Unit unit, Document document) throws INDIeException {
		try {
			documentDbManager.storeDocument(UnitType.collectionOf(unit.getUnitType()), document);

			// Save to the repositories
			unitRepository.save(unit);

			// Return the unit
			return unit;
		} catch (DocumentDataException e) {
			throw INDIeExceptionFactory.createInternalException(e);
		}
	}
}
