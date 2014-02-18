package br.ufrgs.rmpestano.intrabundle.i18n;

import br.ufrgs.rmpestano.intrabundle.annotation.CurrentLocale;
import br.ufrgs.rmpestano.intrabundle.event.LocaleChangeEvent;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.io.Serializable;

@Singleton
public class MessageProvider implements Serializable {


    @Inject
    @CurrentLocale
    Instance<String> localeLanguage;

    private ResourceBundle currentBundle;

    public String getMessage(String key) {
        return getCurrentBundle().getString(key);
    }

    public String getMessage(String key, Object... params) {
        return getCurrentBundle().getString(key, params);
    }

    public ResourceBundle getCurrentBundle() {
        if (currentBundle == null) {
            try {
                currentBundle = new ResourceBundle(getClass().getResourceAsStream("/messages_"+ localeLanguage.get()+".properties"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return currentBundle;
    }


    public void onLocaleChange(@Observes LocaleChangeEvent localeChangeEvent){
        currentBundle = null;
    }

}
