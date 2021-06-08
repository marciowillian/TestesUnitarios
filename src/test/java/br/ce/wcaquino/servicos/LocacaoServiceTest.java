package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.builders.FilmeBuilder.umFilme;
import static br.ce.wcaquino.builders.FilmeBuilder.umFilmeSemEstoque;
import static br.ce.wcaquino.builders.UsuarioBuilder.umUsuario;
import static br.ce.wcaquino.matchers.MatchersProprios.caiEmUmaSegunda;
import static br.ce.wcaquino.matchers.MatchersProprios.ehHoje;
import static br.ce.wcaquino.matchers.MatchersProprios.ehHojeComDiferencaDias;
import static junit.framework.Assert.fail;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeFalse;
import static org.junit.Assume.assumeTrue;
import static org.mockito.Mockito.mock;

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

import br.ce.wcaquino.dao.LocacaoDAO;
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
	private LocacaoDAO dao;

	@Rule
	public ErrorCollector error = new ErrorCollector();

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Before
	public void setUp() {
		service = new LocacaoService();
		dao = mock(LocacaoDAO.class);
		usuario = new Usuario("Usuario1");
		service.setLocacaoDAO(dao);
	}

	@Test
	public void deveTestarLocacaoComSucesso() throws Exception {
		assumeFalse(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));

		// cenario
		usuario = umUsuario().agora();

		filmes = Arrays.asList(umFilme().agora(), umFilme().agora(), umFilme().agora());

		// acao
		Locacao locacao;
		locacao = service.alugarFilme(usuario, filmes);

		// verificacao
		error.checkThat(locacao.getValor(), is(equalTo(11.0)));
		error.checkThat(locacao.getDataLocacao(), ehHoje());
		error.checkThat(locacao.getDataRetorno(), ehHojeComDiferencaDias(1));
	}

	/*
	 * Forma Elegante de tratamento de exce��o
	 */
	@Test(expected = FilmeSemEstoqueException.class)
	public void deveTestarLocacaoDeFilmeSemEstoque() throws Exception {
		// cenario
		usuario = umUsuario().agora();

		filmes = Arrays.asList(umFilmeSemEstoque().agora(), umFilmeSemEstoque().agora(), umFilmeSemEstoque().agora());

		// acao
		service.alugarFilme(usuario, filmes);
	}

	@SuppressWarnings("deprecation")
	@Test
	public void deveTestarLocacaoDeFilmeSemUsuario() throws FilmeSemEstoqueException {
		// cenario

		filmes = Arrays.asList(umFilme().comValor(4d).agora(), umFilme().comValor(5d).agora(),
				umFilme().comValor(7d).agora());

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
		usuario = umUsuario().agora();

		exception.expect(LocacaoException.class);
		exception.expectMessage("Lista de filmes vazia");

		// acao
		service.alugarFilme(usuario, null);
	}

	@Test
	public void deveDevolverFilmeNaSegundaAoLocarNoSabado() throws LocacaoException, FilmeSemEstoqueException {
		assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));

		// cenario
		usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 1, 4.0));

		// acao
		Locacao locacao = service.alugarFilme(usuario, filmes);

		// verificacao
		assertThat(locacao.getDataRetorno(), caiEmUmaSegunda());
	}

}
