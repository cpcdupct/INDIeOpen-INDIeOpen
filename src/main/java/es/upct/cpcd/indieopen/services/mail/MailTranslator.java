package es.upct.cpcd.indieopen.services.mail;

import java.util.Locale;

import org.springframework.context.MessageSource;

import es.upct.cpcd.indieopen.common.Language;

class MailTranslator {

    private final MessageSource messageSource;

    MailTranslator(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    String getLocalizedString(String key, Language language) {
        return messageSource.getMessage(key, null, getLocale(language));
    }

    private Locale getLocale(Language language) {
        switch (language) {
            case SPANISH:
                return Locale.forLanguageTag("es-ES");
            case FRENCH:
                return Locale.FRENCH;
            case GREEK:
                return Locale.forLanguageTag("el-GR");
            case LITHUANIAN:
                return Locale.forLanguageTag("lt-LT");
            case ENGLISH:
            default:
                return Locale.ENGLISH;
        }
    }

}
