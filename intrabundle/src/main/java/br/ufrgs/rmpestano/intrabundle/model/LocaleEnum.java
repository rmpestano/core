package br.ufrgs.rmpestano.intrabundle.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by rmpestano on 1/26/14.
 */
public enum LocaleEnum {
    PT("pt"), EN("en");

    private final String value;

    LocaleEnum(String value) {
        this.value = value;
    }


    public static String getByLocale(Locale locale) {
        for (String s : getAll()) {
            if (s.equals(locale.getDisplayLanguage())) {
                return s;
            }
        }
        //locale not supported, return 'EN'
        return EN.value;
    }

    public static List<String> getAll() {
        List<String> retorno = new ArrayList<String>();
        for (LocaleEnum l : LocaleEnum.values()) {
            retorno.add(l.value);
        }
        return retorno;
    }

    @Override
    public String toString() {
        return value;
    }

}
