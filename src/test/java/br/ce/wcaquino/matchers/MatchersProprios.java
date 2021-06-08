package br.ce.wcaquino.matchers;

import java.util.Calendar;

public class MatchersProprios {

	public static DiaSemanaMatcher caiEm(Integer diaSemana) {
		return new DiaSemanaMatcher(diaSemana);
	}
	
	public static DiaSemanaMatcher caiEmUmaSegunda() {
		return new DiaSemanaMatcher(Calendar.MONDAY);
	}
	
	public static DataRetornoMatcher ehHoje() {
		return ehHojeComDiferencaDias(0);
	}
	
	public static DataRetornoMatcher ehHojeComDiferencaDias(Integer diaSemana) {
		return new DataRetornoMatcher(diaSemana);
	}
}
