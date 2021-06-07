package br.ce.wcaquino.servicos;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocacaoException;

@RunWith(Parameterized.class)
public class CalculoValorLocacaoTest {
	
	private LocacaoService service;
	
	@Parameter
	public List<Filme> filmes;
	
	@Parameter(value=1)
	public Double valorLocacao = 0.0;
	
	@Parameter(value=2)
	public String cenario;
	
	@Before
	public void setUp() {
		service = new LocacaoService();
		
	}
	
	private static Filme filme1 = new Filme("Filme 1", 1, 4.0);
	private static Filme filme2 = new Filme("Filme 2", 1, 4.0);
	private static Filme filme3 = new Filme("Filme 3", 1, 4.0);
	private static Filme filme4 = new Filme("Filme 4", 1, 4.0);
	private static Filme filme5 = new Filme("Filme 5", 1, 4.0);
	private static Filme filme6 = new Filme("Filme 6", 1, 4.0);
	private static Filme filme7 = new Filme("Filme 7", 1, 4.0);
	
	@Parameters(name="{2}")
	public static Collection<Object[]> getParametros(){
		return Arrays.asList(new Object [][] {
			{Arrays.asList(filme1, filme2), 8.0, "1º e 2º filme nao tem desconto"},
			{Arrays.asList(filme1, filme2, filme3), 11.0, "25% de desconto"},
			{Arrays.asList(filme1, filme2, filme3, filme4), 13.0, "50% de desconto"},
			{Arrays.asList(filme1, filme2, filme3, filme4, filme5), 14.0, "75% de desconto"},
			{Arrays.asList(filme1, filme2, filme3, filme4, filme5, filme6), 14.0, "100% de desconto"},
			{Arrays.asList(filme1, filme2, filme3, filme4, filme5, filme6, filme7), 18.0, "o 7º filme nao possui desconto"}
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
