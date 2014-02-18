package br.ufrgs.rmpestano.intrabundle.i18n;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.MessageFormat;

public class ResourceBundle extends java.util.PropertyResourceBundle implements Serializable {

  private static final long serialVersionUID = 1L;



  public ResourceBundle(InputStream stream) throws IOException {
    super(stream);
  }

  public String getString(String key, Object... params) {
    return MessageFormat.format(this.getString(key), params);
  }

}
