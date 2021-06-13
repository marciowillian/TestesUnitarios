package br.ce.wcaquino.builders;

import java.util.Arrays;
import java.util.Date;

import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.utils.DataUtils;

public class LocacaoBuilder {

	private Locacao locacao;
	
	private LocacaoBuilder() {};
	
	public static LocacaoBuilder umaLocacao() {
		LocacaoBuilder builder = new LocacaoBuilder();
		builder.locacao = new Locacao();
		builder.locacao.setUsuario(UsuarioBuilder.umUsuario().agora());
		builder.locacao.setFilmes(Arrays.asList(FilmeBuilder.umFilme().agora()));
		builder.locacao.setValor(4d);
		builder.locacao.setDataLocacao(new Date());
		builder.locacao.setDataRetorno(DataUtils.adicionarDias(new Date(), 1));
		
		return builder;
	}
	
	public Locacao agora() {
		return locacao;
	}
	
	public LocacaoBuilder comDataRetorno(Date dataRetorno) {
		locacao.setDataRetorno(dataRetorno);
		return this;
	}
	
	public LocacaoBuilder comUsuario(Usuario usuario){
		locacao.setUsuario(usuario);
		return this;
	}
	
	public LocacaoBuilder atrasado() {
		locacao.setDataLocacao(DataUtils.obterDataComDiferencaDias(-4));
		locacao.setDataRetorno(DataUtils.obterDataComDiferencaDias(-2));
		return this;
	}
	
}
