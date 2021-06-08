package br.ce.wcaquino.servicos;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mockito;

import br.ce.wcaquino.builders.FilmeBuilder;
import br.ce.wcaquino.dao.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocacaoException;

@RunWith(Parameterized.class)
public class CalculoValorLocacaoTest {
	
	private LocacaoService service;
	private LocacaoDAO dao;
	
	@Parameter
	public List<Filme> filmes;
	
	@Parameter(value=1)
	public Double valorLocacao = 0.0;
	
	@Parameter(value=2)
	public String cenario;
	
	@Before
	public void setUp() {
		service = new LocacaoService();
		dao = mock(LocacaoDAO.class);
		service.setLocacaoDAO(dao);
		
	}
	
	private static Filme filme1 = FilmeBuilder.umFilme().agora();
	private static Filme filme2 = FilmeBuilder.umFilme().agora();
	private static Filme filme3 = FilmeBuilder.umFilme().agora();
	private static Filme filme4 = FilmeBuilder.umFilme().agora();
	private static Filme filme5 = FilmeBuilder.umFilme().agora();
	private static Filme filme6 = FilmeBuilder.umFilme().agora();
	private static Filme filme7 = FilmeBuilder.umFilme().agora();
	
	@Parameters(name="{2}")
	public static Collection<Object[]> getParametros(){
		return Arrays.asList(new Object [][] {
			{Arrays.asList(filme1, filme2), 8.0, "filme 1 e 2 nao tem desconto"},
			{Arrays.asList(filme1, filme2, filme3), 11.0, "25% de desconto"},
			{Arrays.asList(filme1, filme2, filme3, filme4), 13.0, "50% de desconto"},
			{Arrays.asList(filme1, filme2, filme3, filme4, filme5), 14.0, "75% de desconto"},
			{Arrays.asList(filme1, filme2, filme3, filme4, filme5, filme6), 14.0, "100% de desconto"},
			{Arrays.asList(filme1, filme2, filme3, filme4, filme5, filme6, filme7), 18.0, "O filme 7 nao possui desconto"}
		});
	}
	
	@Test
	public void deveCalcularValorLocacaoConsiderandoDescontos() throws LocacaoException, FilmeSemEstoqueException {
		//cenario
		Usuario usuario = new Usuario("Usuario 1");
				
		//acao
		Locacao locacao = service.alugarFilme(usuario, filmes);
		
		//verificacao
		assertThat(locacao.getValor(), is(valorLocacao));
	}
	
	@Test
	public void print(){
		System.out.println(valorLocacao);
	}
	
}
