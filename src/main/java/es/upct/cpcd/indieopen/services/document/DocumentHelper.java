package es.upct.cpcd.indieopen.services.document;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

import es.upct.cpcd.indieopen.unit.domain.UnitType;

/**
 * Helper which creates or updates Documents empty and initial instances of the
 * Document Database model for Units, Questions...etc
 *
 * @author Mario
 */
public class DocumentHelper {

	private DocumentHelper() {

	}

	/**
	 * Creates an empty unit document in DocumentDB
	 *
	 * @param type - Type of the unit to be created
	 * @return Document instance
	 */
	public static Document createEmptyUnitDocument(UnitType type) {
		return createDocument(type, new ArrayList<>());
	}

	public static Document createDocument(UnitType type, List<Document> data) {
		Document mongoDocument = new Document();

		if (type == UnitType.CONTENT) {
			mongoDocument.append("sections", data);
			mongoDocument.append("version", 1);
		} else if (type == UnitType.EVALUATION) {
			mongoDocument.append("evaluation", data);
		}

		return mongoDocument;
	}

	public static Document fromJSONObject(JSONObject jsonObject) {
		return Document.parse(jsonObject.toString());
	}

	public static void updateContentDocument(Document mongoDocument, JSONArray sections, int version) {
		List<Document> dbObjects = DocumentDBManagerImpl.transformArrayIntoDBObjectList(sections);

		mongoDocument.put("sections", dbObjects);
		mongoDocument.append("version", version);
	}

	public static void updateEvaluationDocument(Document mongoDocument, JSONArray questions) {
		mongoDocument.put("evaluation", DocumentDBManagerImpl.transformArrayIntoDBObjectList(questions));
	}

	public static Document createEmptyVideoDocument() {
		Document mongoDocument = new Document();
		mongoDocument.append("interactiveData", new ArrayList<Document>());
		mongoDocument.append("editorData", new ArrayList<Document>());

		return mongoDocument;
	}

	public static void updateVideoDocument(Document document, JSONArray editorData) {
		List<Document> editorDataDocuments = DocumentDBManagerImpl.transformArrayIntoDBObjectList(editorData);
		document.put("editorData", editorDataDocuments);
	}

	public static void updatePublishedVideoDocument(Document document, JSONArray interactiveData) {
		List<Document> editorDataDocuments = DocumentDBManagerImpl.transformArrayIntoDBObjectList(interactiveData);
		document.put("interactiveData", editorDataDocuments);
	}

	public static void updateCourseEditorDataDocument(Document document, JSONObject editorData) {
		JSONObject data = new JSONObject();

		data.put("drawflow", Document.parse(editorData.getJSONObject("drawflow").toString()));
		data.put("rules", DocumentDBManagerImpl.transformArrayIntoDBObjectList(editorData.getJSONArray("rules")));
		data.put("unitNodes",
				DocumentDBManagerImpl.transformArrayIntoDBObjectList(editorData.getJSONArray("unitNodes")));

		document.put("editor", Document.parse(data.toString()));
	}

	public static Document createEmptyCourseDocument() {
		Document mongoDocument = new Document();
		mongoDocument.append("editor", Document.parse("{}"));
		mongoDocument.append("published", Document.parse("{}"));
		return mongoDocument;
	}
}
