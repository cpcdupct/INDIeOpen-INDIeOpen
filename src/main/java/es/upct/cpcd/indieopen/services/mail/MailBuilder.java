package es.upct.cpcd.indieopen.services.mail;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.net.URL;
import java.util.HashMap;

import org.springframework.core.io.ClassPathResource;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

import es.upct.cpcd.indieopen.common.Language;
import es.upct.cpcd.indieopen.utils.ObjectUtils;

class MailBuilder {
	private static final String PASSWORD_RECOVERY_URL = "https://MY_URL/resetPassword";
	private static final String NEW_ACCOUNT_URL = "https://MY_URL/newAccount";
	private static final String EMAIL_FOLDER = "emails/";
	private final MustacheFactory mf;

	public MailBuilder() {
		this.mf = new DefaultMustacheFactory();
	}

	String getHTMLPasswordRecovery(String completeUser, String token, Language language) throws IOException {
		ObjectUtils.requireStringsValid(token, completeUser);

		HashMap<String, Object> values = new HashMap<>();
		values.put("name", completeUser);
		values.put("URL", buildURLPasswordRecovery(token));

		StringWriter stWriter = new StringWriter();
		Mustache mustache = mf.compile(getEmailFromProperties("recover", language), "recover");
		mustache.execute(stWriter, values);

		return stWriter.toString();
	}

	public String getHTMLNewAccount(String token, String email, String name, Language language) throws IOException {
		ObjectUtils.requireStringsValid(email, token);

		HashMap<String, Object> values = new HashMap<>();
		values.put("URL", buildURLNewAccount(token));
		values.put("name", name);

		StringWriter stWriter = new StringWriter();
		Mustache mustache = mf.compile(getEmailFromProperties("invitation", language), "invitation");
		mustache.execute(stWriter, values);

		return stWriter.toString();
	}

	public String getHTMLNewUser(String completeName, String username, Language language) throws IOException {
		ObjectUtils.requireStringsValid(completeName, username);

		HashMap<String, Object> values = new HashMap<>();
		values.put("name", completeName);
		values.put("user", username);

		StringWriter stWriter = new StringWriter();
		Mustache mustache = mf.compile(getEmailFromProperties("welcome", language), "welcome");
		mustache.execute(stWriter, values);

		return stWriter.toString();
	}

	private String buildURLPasswordRecovery(String token) {
		return PASSWORD_RECOVERY_URL + "?t=" + token;
	}

	private String buildURLNewAccount(String token) {
		return NEW_ACCOUNT_URL + "?t=" + token;
	}

	private Reader getEmailFromProperties(String filename, Language language) throws IOException {
		return getFileFromString(EMAIL_FOLDER + filename + getLangExtension(language));
	}

	private String getLangExtension(Language language) {
		switch (language) {
		case SPANISH:
			return ".es.html";
		case FRENCH:
			return ".fr.html";
		case GREEK:
			return ".gr.html";
		case LITHUANIAN:
			return ".li.html";
		case ENGLISH:
		default:
			return ".html";
		}
	}

	private Reader getFileFromString(String filePath) throws IOException {
		ClassPathResource cl = new ClassPathResource(filePath);
		URL url = cl.getURL();
		return new InputStreamReader(url.openStream());
	}
}
