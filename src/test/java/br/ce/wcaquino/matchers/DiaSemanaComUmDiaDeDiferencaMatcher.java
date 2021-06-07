package br.ce.wcaquino.matchers;

import java.util.Date;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import br.ce.wcaquino.utils.DataUtils;

public class DiaSemanaComUmDiaDeDiferencaMatcher extends TypeSafeMatcher<Date> {

	private Integer dias;
	
	public DiaSemanaComUmDiaDeDiferencaMatcher(Integer dias) {
		this.dias = dias;
	}
	
	@Override
	public void describeTo(Description description) {
		// TODO Auto-generated method stub

	}

	@Override
	protected boolean matchesSafely(Date data) {
		return DataUtils.adicionarDias(data, dias).equals(DataUtils.obterDataComDiferencaDias(1));
	}

}
