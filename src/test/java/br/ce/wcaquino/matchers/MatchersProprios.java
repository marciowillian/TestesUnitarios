package br.ce.wcaquino.matchers;

import java.util.Calendar;

public class MatchersProprios {

	public static DiaSemanaMatcher caiEm(Integer diaSemana) {
		return new DiaSemanaMatcher(diaSemana);
	}
	
	public static DiaSemanaMatcher caiEmUmaSegunda() {
		return new DiaSemanaMatcher(Calendar.MONDAY);
	}
	
//	public static DiaSemanaMatcher ehHoje() {
//		return new DiaSemanaMatcher();
//	}
	
	public static DiaSemanaMatcher ehHojeComDiferencaDias(Integer diaSemana) {
		return new DiaSemanaMatcher(diaSemana);
	}
}
