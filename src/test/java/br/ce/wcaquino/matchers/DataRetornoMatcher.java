package br.ce.wcaquino.matchers;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import br.ce.wcaquino.utils.DataUtils;

public class DataRetornoMatcher extends TypeSafeMatcher<Date> {

	private Integer dias;

	public DataRetornoMatcher(Integer dias) {
		this.dias = dias;
	}

	public void describeTo(Description desc) {
		Calendar data = Calendar.getInstance();
		data.set(Calendar.DAY_OF_WEEK, dias);
		String dataExtenso = data.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, new Locale("pt", "BR"));
		desc.appendText(dataExtenso);
	}

	@Override
	protected boolean matchesSafely(Date data) {
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		c1.setTime(data);
		c2.setTime(DataUtils.obterDataComDiferencaDias(dias));

		if (c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH)
				&& c1.get(Calendar.DAY_OF_WEEK) == c2.get(Calendar.DAY_OF_WEEK)
				&& c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)) {
			return true;

		} else
			return false;
	}

}
