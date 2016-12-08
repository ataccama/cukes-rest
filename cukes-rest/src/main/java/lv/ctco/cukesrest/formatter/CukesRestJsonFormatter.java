package lv.ctco.cukesrest.formatter;

import lv.ctco.cukescore.formatter.CukesCoreJsonFormatter;
import lv.ctco.cukescore.internal.context.ContextInflater;
import lv.ctco.cukesrest.internal.GuiceInjectorSource;

public class CukesRestJsonFormatter extends CukesCoreJsonFormatter {

    public CukesRestJsonFormatter(Appendable out) throws Exception {
        super(out);
        contextInflater = new GuiceInjectorSource().getInjector().getInstance(ContextInflater.class);
    }
}
