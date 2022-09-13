package es.upct.cpcd.indieopen.media;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import es.upct.cpcd.indieopen.utils.ObjectUtils;
import org.json.JSONArray;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * This class is a content processor of a unit model with the goal of extract
 * all UPCT Media resources from the unit.
 *
 * @author MARIO
 */
public class ContentProcessor implements AutoCloseable {
    /**
     * Keys of elements that could have a UPCT Media link
     */
    private static final String[] MULTIMEDIA_ITEMS = new String[]{"image", "audio", "videourl"};
    /**
     * UPCT Media Link Resource regex
     */
    private static final String MEDIA_REGEX = "MY_MULTIMEDIA_REPOSITORY";
    /**
     * Pattern for using the regex
     */
    private static final Pattern pattern = Pattern.compile(MEDIA_REGEX);

    /**
     * JSONPath object query
     */
    private Object jsonDocument;

    /**
     * Private constructor for Content Processor
     *
     * @param jsonDocument JSONPath object query
     */
    private ContentProcessor(Object jsonDocument) {
        this.jsonDocument = jsonDocument;
    }

    /**
     * Closes a ContentProcessor instance.
     * <p>
     * {@inheritDoc}
     */
    @Override
    public void close() {
        jsonDocument = null;
    }

    /**
     * Creates a ContentProceso instance from a JSON Array containing the sections
     * of the unit model.
     *
     * @param arrayOfContent JSON Array of sections
     * @return ContentProcessor instance
     */
    public static ContentProcessor create(JSONArray arrayOfContent) {
        String jsonStringContent = arrayOfContent.toString();
        return new ContentProcessor(Configuration.defaultConfiguration().jsonProvider().parse(jsonStringContent));
    }

    /**
     * * Creates a ContentProceso instance from a filePath to a JSON file with a
     * JSON Array containing the sections of the unit model.
     *
     * @param filePath JSON file path
     * @return ContentProcessor instance
     * @throws IOException If the file in filePath cannot be found
     */
    public static ContentProcessor create(String filePath) throws IOException {
        File file = new File(filePath);
        FileInputStream fis = new FileInputStream(file);
        return new ContentProcessor(Configuration.defaultConfiguration().jsonProvider().parse(fis, "UTF-8"));
    }

    /**
     * Find UPCT Media URLs by widget key with a content unit.
     *
     * @param key Widget key
     * @return Set of Media URL
     */
    public Set<String> findMediaURLByKey(String key) {
        // 1 Find all media URL of widgets by given key
        List<String> listOfMedia = JsonPath.read(jsonDocument, "$..[?(@." + key + ")]." + key);

        // 2 Create a set to remove duplicates
        Set<String> setOfMedia = new HashSet<>(listOfMedia);

        // 3 Remove the whitspace value of possible widget without data inside
        trimWhiteSpaces(setOfMedia);

        // 4 Gather all only UPCT Media URLs
        setOfMedia = gatherOnlyUPCTMediaURL(setOfMedia);

        // 5 Extract the identifier from the URL
        setOfMedia = extractIdentifiers(setOfMedia);

        // 6 Return the media treated
        return setOfMedia;
    }

    /**
     * Extract the identifiers from a set UPCT Media URLs
     *
     * @param setOfMedia Set of UPCT Media URLs without whitespace
     * @return Set of Media identifiers
     */
    private Set<String> extractIdentifiers(Set<String> setOfMedia) {
        return setOfMedia.stream().map(ContentProcessor::extractMediaResourceFromLink).collect(Collectors.toSet());
    }

    /**
     * Remove the white space from the set of media URLs
     *
     * @param setOfMedia Set of media URLs
     */
    private void trimWhiteSpaces(Set<String> setOfMedia) {
        setOfMedia.remove("");
    }

    /**
     * Gather only UPCT Media urls from a set of media URLs
     *
     * @param setOfMedia Set of media URLs
     * @return Set of UPCT Media URLs
     */
    private Set<String> gatherOnlyUPCTMediaURL(Set<String> setOfMedia) {
        return setOfMedia.stream().filter(ContentProcessor::isUPCTMediaResource).collect(Collectors.toSet());
    }

    /**
     * Main method to begin with the extraction of UPCT Media resources
     *
     * @return Set of UPCT Media URLs
     */
    public Set<String> extractAllMediaURL() {
        Set<String> setOfMediaURL = new HashSet<>();

        for (String multimediaItem : MULTIMEDIA_ITEMS) setOfMediaURL.addAll(findMediaURLByKey(multimediaItem));

        return setOfMediaURL;
    }

    /**
     * Determine whether a link is point to a UPCT media resource
     *
     * @param resourceLink Resource link URL
     * @return true if a UPCT Media link, false otherwise
     */
    public static boolean isUPCTMediaResource(String resourceLink) {
        ObjectUtils.requireStringValid(resourceLink);
        return resourceLink.matches(MEDIA_REGEX);
    }

    /**
     * Exctract the idenfitier from a UPCT media link
     *
     * @param resourceLink UPCT Media resource link
     * @return Multimedia identifier
     */
    public static String extractMediaResourceFromLink(String resourceLink) {
        if (!isUPCTMediaResource(resourceLink))
            throw new IllegalArgumentException();

        Matcher matcher = pattern.matcher(resourceLink);
        matcher.find();
        return matcher.group(1);
    }

    public JSONArray find(String widgetType) {
        net.minidev.json.JSONArray array = JsonPath.read(jsonDocument, "$..[?(@.widget=='" + widgetType + "')]");
        return new JSONArray(array.toString());
    }
}
