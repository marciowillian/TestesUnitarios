package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.utils.DataUtils.isMesmaData;
import static br.ce.wcaquino.utils.DataUtils.obterDataComDiferencaDias;
import static junit.framework.Assert.fail;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeFalse;
import static org.junit.Assume.assumeTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocacaoException;
import br.ce.wcaquino.utils.DataUtils;

public class LocacaoServiceTest {

	private LocacaoService service = new LocacaoService();
	private Usuario usuario = new Usuario();
	private List<Filme> filmes = new ArrayList<Filme>();

	@Rule
	public ErrorCollector error = new ErrorCollector();

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Before
	public void setUp() {
		service = new LocacaoService();
		usuario = new Usuario("Usuario1");
	}

	@Test
	public void deveTestarLocacaoComSucesso() throws Exception {
		assumeFalse(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		
		// cenario
		usuario = new Usuario("Usuario 1");

		filmes = Arrays.asList(new Filme("Filme 1", 2, 4.0), new Filme("Filme 2", 3, 5.0),
				new Filme("Filme 3", 5, 7.0));

		// acao
		Locacao locacao;
		locacao = service.alugarFilme(usuario, filmes);

		// verificacao
		error.checkThat(locacao.getValor(), is(equalTo(14.25)));
		error.checkThat(isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
		error.checkThat(isMesmaData(locacao.getDataRetorno(), obterDataComDiferencaDias(1)), is(true));
	}

	/*
	 * Forma Elegante de tratamento de exceção
	 */
	@Test(expected = FilmeSemEstoqueException.class)
	public void deveTestarLocacaoDeFilmeSemEstoque() throws Exception {
		// cenario
		usuario = new Usuario("Usuario 1");

		Filme filme1 = new Filme("Filme 1", 0, 4.0);
		Filme filme2 = new Filme("Filme 2", 0, 5.0);
		Filme filme3 = new Filme("Filme 3", 0, 7.0);
		filmes.add(filme1);
		filmes.add(filme2);
		filmes.add(filme3);

		// acao
		service.alugarFilme(usuario, filmes);
	}

	@SuppressWarnings("deprecation")
	@Test
	public void deveTestarLocacaoDeFilmeSemUsuario() throws FilmeSemEstoqueException {
		// cenario
		Filme filme1 = new Filme("Filme 1", 0, 4.0);
		Filme filme2 = new Filme("Filme 2", 0, 5.0);
		Filme filme3 = new Filme("Filme 3", 0, 7.0);
		filmes.add(filme1);
		filmes.add(filme2);
		filmes.add(filme3);

		// acao
		try {
			service.alugarFilme(null, filmes);
			fail("Usuario deveria ser nulo");
		} catch (LocacaoException e) {
			assertThat(e.getMessage(), is("Usuario vazio"));
		}

	}

	@Test
	public void deveTestarLocacaoDeFilmeSemFilme() throws LocacaoException, FilmeSemEstoqueException {
		// cenario
		usuario = new Usuario("Usuario 1");

		exception.expect(LocacaoException.class);
		exception.expectMessage("Lista de filmes vazia");

		// acao
		service.alugarFilme(usuario, null);
	}
	
	@Test
	public void deveTestarAplicacaoDe25PctDeDescontoNoTerceiroFilme() throws LocacaoException, FilmeSemEstoqueException {
		//cenario
		usuario = new Usuario("Usuario 1");
		List<Filme> filmes = Arrays.asList(
				new Filme("Filme 1", 1, 4.0),
				new Filme("Filme 2", 1, 4.0),
				new Filme("Filme 3", 1, 4.0));
		
		//acao
		Locacao locacao = service.alugarFilme(usuario, filmes);
		
		//verificacao
		assertThat(11.0, is(locacao.getValor()));
	}
	
	@Test
	public void deveTestarAplicacaoDe50PctDeDescontoNoQuartoFilme() throws LocacaoException, FilmeSemEstoqueException {
		//cenario
		usuario = new Usuario("Usuario 1");
		List<Filme> filmes = Arrays.asList(
				new Filme("Filme 1", 1, 4.0),
				new Filme("Filme 2", 1, 4.0),
				new Filme("Filme 3", 1, 4.0),
				new Filme("Filme 4", 1, 4.0));
		
		//acao
		Locacao locacao = service.alugarFilme(usuario, filmes);
		
		//verificacao
		assertThat(13.0, is(locacao.getValor()));
	}
	
	@Test
	public void deveTestarAplicacaoDe75PctDeDescontoNoQuintoFilme() throws LocacaoException, FilmeSemEstoqueException {
		//cenario
		usuario = new Usuario("Usuario 1");
		List<Filme> filmes = Arrays.asList(
				new Filme("Filme 1", 1, 4.0),
				new Filme("Filme 2", 1, 4.0),
				new Filme("Filme 3", 1, 4.0),
				new Filme("Filme 4", 1, 4.0),
				new Filme("Filme 5", 1, 4.0));
		
		//acao
		Locacao locacao = service.alugarFilme(usuario, filmes);
		
		//verificacao
		assertThat(14.0, is(locacao.getValor()));
	}
	
	@Test
	public void deveTestarAplicacaoDe100PctDeDescontoNoSextoFilme() throws LocacaoException, FilmeSemEstoqueException {
		//cenario
		usuario = new Usuario("Usuario 1");
		List<Filme> filmes = Arrays.asList(
				new Filme("Filme 1", 1, 4.0),
				new Filme("Filme 2", 1, 4.0),
				new Filme("Filme 3", 1, 4.0),
				new Filme("Filme 4", 1, 4.0),
				new Filme("Filme 5", 1, 4.0),
				new Filme("Filme 6", 1, 4.0));
		
		//acao
		Locacao locacao = service.alugarFilme(usuario, filmes);
		
		//verificacao
		assertThat(14.0, is(locacao.getValor()));
	}
	
	@Test
	public void deveDevolverFilmeNaSegundaAoLocarNoSabado() throws LocacaoException, FilmeSemEstoqueException {
		assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SUNDAY));
		
		//cenario
		usuario = new Usuario("Usuario 1");
		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 1, 4.0));
		
		//acao
		Locacao locacao = service.alugarFilme(usuario, filmes);
		
		//verificacao
		boolean ehSegunda = DataUtils.verificarDiaSemana(locacao.getDataRetorno(), Calendar.MONDAY);
		assertTrue(ehSegunda);
	}
}
