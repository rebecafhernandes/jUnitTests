package matchers;

import br.ce.wcaquino.utils.DataUtils;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import javax.xml.crypto.Data;
import java.util.Date;

public class DiferencaDiasMatcher extends TypeSafeMatcher<Date> {

    private int diferenciaDias;

    public DiferencaDiasMatcher(int diferencaDias) {
        this.diferenciaDias = diferencaDias;
    }

    @Override
    protected boolean matchesSafely(Date data) {
        return DataUtils.isMesmaData(data, DataUtils.obterDataComDiferencaDias(diferenciaDias));
    }

    @Override
    public void describeTo(Description description) {

    }
}
