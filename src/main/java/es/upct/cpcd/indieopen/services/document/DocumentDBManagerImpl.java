package es.upct.cpcd.indieopen.services.document;

import static com.mongodb.client.model.Filters.eq;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

/**
 * Implementation of {@link DocumentDBManager}
 * 
 * @author MARIO
 *
 */
@Service
public class DocumentDBManagerImpl implements DocumentDBManager {
	// Mongo database
	private final MongoClient mongoClient;

	// Mongo access adapters
	private final MongoCollection<Document> contentUnitsCollection;
	private final MongoCollection<Document> coursesCollection;
	private final MongoCollection<Document> evaluationUnitsCollection;
	private final MongoCollection<Document> videoCollection;

	public DocumentDBManagerImpl(@Value("${documentdb.connectionString}") String connectionString,
			@Value("${documentdb.name}") String name) {
		// Database
		this.mongoClient = MongoClients.create(connectionString);
		MongoDatabase database = this.mongoClient.getDatabase(name);

		this.contentUnitsCollection = database.getCollection("models");
		this.evaluationUnitsCollection = database.getCollection("evaluation");
		this.videoCollection = database.getCollection("videos");
		this.coursesCollection = database.getCollection("courses");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<Document> findDocument(DocumentDBCollection collectionName, String documentId) {
		Document mongoDocument = collection(collectionName).find(eq("_id", new ObjectId(documentId))).first();
		return Optional.ofNullable(mongoDocument);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void replaceDocument(DocumentDBCollection collectionName, String documentId, Document document)
			throws DocumentDataException {
		Objects.requireNonNull(document);

		try {
			collection(collectionName).replaceOne(eq("_id", new ObjectId(documentId)), document);
		} catch (MongoException e) {
			throw new DocumentDataException("Cannot replace document with id:" + documentId, e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String storeDocument(DocumentDBCollection collectionName, Document document) throws DocumentDataException {
		Objects.requireNonNull(document);

		try {
			collection(collectionName).insertOne(document);
			ObjectId id = document.getObjectId("_id");
			return id.toHexString();
		} catch (MongoException e) {
			throw new DocumentDataException("Cannot store new document.", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteDocument(DocumentDBCollection collectionName, String documentid) throws DocumentDataException {
		try {
			collection(collectionName).deleteOne(eq("_id", new ObjectId(documentid)));
		} catch (Exception ex) {
			throw new DocumentDataException(ex);
		}
	}

	/**
	 * Transform a JSONArray into a List of Mongo Documents
	 * 
	 * @param array JSONArray array of JSONObjects
	 * 
	 * @return List of Documents
	 */
	public static List<Document> transformArrayIntoDBObjectList(JSONArray array) {
		ArrayList<Document> listOfDBObjects = new ArrayList<>();

		for (int i = 0; i < array.length(); i++) {
			JSONObject jObject = array.getJSONObject(i);
			Document dbObject = Document.parse(jObject.toString());
			listOfDBObjects.add(dbObject);
		}

		return listOfDBObjects;
	}

	public static JSONArray getJsonArrayFromString(String arrayString) {
		return new JSONArray(arrayString);
	}

	private MongoCollection<Document> collection(DocumentDBCollection collectionName) {
		switch (collectionName) {
		case EVALUATION_UNITS:
			return evaluationUnitsCollection;
		case VIDEOS:
			return videoCollection;
		case COURSES:
			return coursesCollection;
		default:
			return contentUnitsCollection;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void close() {
		mongoClient.close();
	}

}
