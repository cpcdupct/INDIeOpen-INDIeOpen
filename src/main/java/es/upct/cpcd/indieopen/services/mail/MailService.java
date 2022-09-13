package es.upct.cpcd.indieopen.services.mail;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import es.upct.cpcd.indieopen.common.Language;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class MailService {
	@Value("${mail.from}")
	private String from;

	/**
	 * Java email sender
	 */
	private final JavaMailSender mailSender;

	/**
	 * MailBuilder instance
	 */
	private final MailBuilder mailBuilder;
	/**
	 * Mail translator
	 **/
	private final MailTranslator mailTranslator;

	@Autowired
	public MailService(MessageSource messageSource, JavaMailSender mailSender) {
		this.mailSender = mailSender;
		this.mailTranslator = new MailTranslator(messageSource);
		this.mailBuilder = new MailBuilder();
	}

	public void sendEmailPasswordRecovery(String userEmail, String completeUser, String token, Language language)
			throws MailServiceException {
		try {
			String html = mailBuilder.getHTMLPasswordRecovery(completeUser, token, language);
			sendEmail(userEmail, html, mailTranslator.getLocalizedString("recovery.subject", language));
		} catch (MailException | IOException e) {
			log.error("Error en MailService.sendEmailPasswordRecovery.", e);
			throw new MailServiceException(e);
		}
	}

	public void sendEmailNewAccount(String token, String email, String name, Language language)
			throws MailServiceException {
		try {
			String html = mailBuilder.getHTMLNewAccount(token, email, name, language);
			sendEmail(email, html, mailTranslator.getLocalizedString("newAccount.subject", language));
		} catch (MailException | IOException e) {
			log.error("Error en MailService.sendEmailNewAccount.", e);
			throw new MailServiceException(e);
		}
	}

	public void sendEmailNewUser(String completeName, String email, Language language) throws MailServiceException {
		try {
			String html = mailBuilder.getHTMLNewUser(completeName, email, language);
			sendEmail(email, html, mailTranslator.getLocalizedString("newUser.subject", language));

		} catch (MailException | IOException e) {
			log.error("Error en MailService.sendEmailNewUser.", e);
			throw new MailServiceException(e);
		}
	}

	private void sendEmail(String toEmail, String htmlMessage, String subject) throws MailException {
		final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
		final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, StandardCharsets.UTF_8.name());

		try {
			message.setFrom(from);
			message.setTo(toEmail);
			message.setSubject(subject);
			message.setText(htmlMessage, true);
		} catch (MessagingException e) {
			log.error(e);
		}

		this.mailSender.send(mimeMessage);
	}

}
