package es.upct.cpcd.indieopen.unit.dto;

import org.bson.Document;
import org.json.JSONObject;

import es.upct.cpcd.indieopen.unit.domain.Unit;
import lombok.Getter;

@Getter
public class UnitWithModel {
	private final Unit unit;
	private final Document document;
	private final JSONObject documentJSONObject;

	public UnitWithModel(Unit unit, Document document) {
		this.unit = unit;
		this.document = document;
		this.documentJSONObject = new JSONObject(document.toJson());

	}

	public JSONObject getJSONObjectFromDocument() {
		return documentJSONObject;
	}

}