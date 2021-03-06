package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.builders.FilmeBuilder.umFilme;
import static br.ce.wcaquino.builders.FilmeBuilder.umFilmeSemEstoque;
import static br.ce.wcaquino.builders.LocacaoBuilder.umaLocacao;
import static br.ce.wcaquino.builders.UsuarioBuilder.umUsuario;
import static br.ce.wcaquino.matchers.MatchersProprios.caiEmUmaSegunda;
import static br.ce.wcaquino.matchers.MatchersProprios.ehHoje;
import static br.ce.wcaquino.matchers.MatchersProprios.ehHojeComDiferencaDias;
import static junit.framework.Assert.fail;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.reflect.Whitebox;

import br.ce.wcaquino.dao.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocacaoException;
import br.ce.wcaquino.utils.DataUtils;

public class LocacaoServiceTest {

	@InjectMocks @Spy
	private LocacaoService service = new LocacaoService();

	private Usuario usuario;
	private List<Filme> filmes;

	@Mock
	private LocacaoDAO dao;
	@Mock
	private SPCService spc;
	@Mock
	private EmailService email;

	@Rule
	public ErrorCollector error = new ErrorCollector();

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Before
	public void setUp() {
		initMocks(this);
	}

	@Test
	public void deveTestarLocacaoComSucesso() throws Exception {
//		assumeFalse(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		
		Mockito.doReturn(DataUtils.obterData(16, 06, 2021)).when(service).obterData();

		// cenario
		usuario = umUsuario().agora();

		filmes = Arrays.asList(umFilme().agora(), umFilme().agora(), umFilme().agora());

		// acao
		Locacao locacao;
		locacao = service.alugarFilme(usuario, filmes);

		// verificacao
		error.checkThat(locacao.getValor(), is(equalTo(11.0)));
//		error.checkThat(locacao.getDataLocacao(), ehHoje());
//		error.checkThat(locacao.getDataRetorno(), ehHojeComDiferencaDias(1));
		error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(), DataUtils.obterData(16, 06, 2021)), is(true));
		error.checkThat(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterData(17, 06, 2021)), is(true));
	}

	/*
	 * Forma Elegante de tratamento de excecao
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
	public void deveDevolverFilmeNaSegundaAoLocarNoSabado() throws Exception {
//		assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		
		// cenario
		usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 1, 4.0));

		Mockito.doReturn(DataUtils.obterData(12, 06, 2021)).when(service).obterData();
		
		// acao
		Locacao locacao = service.alugarFilme(usuario, filmes);

		// verificacao
		assertThat(locacao.getDataRetorno(), caiEmUmaSegunda());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void deveTestarLocacaoComUsuarioNegativado() throws Exception {
		// cenario
		usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());

		when(spc.isNegativado(usuario)).thenReturn(true);

		// acao
		try {
			service.alugarFilme(usuario, filmes);
			// verificacao
			fail();
		} catch (LocacaoException e) {
			Assert.assertThat(e.getMessage(), is("Usuario esta negativado"));
		}
		verify(spc).isNegativado(usuario);
	}

	@Test
	public void deveEnviarEmailParaLocacoesAtrasadas() {
		// cenario
		Usuario usuario = umUsuario().agora();
		Usuario usuario2 = umUsuario().comNome("Usuario em dia").agora();
		List<Locacao> locacoes = Arrays.asList(umaLocacao().atrasado().comUsuario(usuario).agora(),
				umaLocacao().comUsuario(usuario2).agora());
		when(dao.obterLocacoesPendentes()).thenReturn(locacoes);

		// acao
		service.notificarAtrasos();

		// verificacao
		verify(email).notificarAtraso(usuario);
		verify(email, never()).notificarAtraso(usuario2);
		verifyNoMoreInteractions(email);
	}
	
	@Test
	public void deveTratarErroNoSPC() throws Exception {
		//cenario
		usuario = umUsuario().agora();
		filmes = Arrays.asList(umFilme().agora());
		
		when(spc.isNegativado(usuario)).thenThrow(new Exception("Falha catastrofica"));
		
		//verificacao
		exception.expect(LocacaoException.class);
		exception.expectMessage("Problema com SPC, tente novamente");
		
		//acao
		service.alugarFilme(usuario, filmes);
		
	}
	
	@Test
	public void deveProrrogarUmaLocacao() {
		//cenario
		Locacao locacao = umaLocacao().agora();
		
		
		//acao
		service.prrogarLocacao(locacao, 3);
		
		//verificacao
		ArgumentCaptor<Locacao> argCapt = ArgumentCaptor.forClass(Locacao.class);
		verify(dao).salvar(argCapt.capture());
		Locacao locacaoRetornada = argCapt.getValue();
		
		error.checkThat(locacaoRetornada.getValor(), is(12.0));
		error.checkThat(locacaoRetornada.getDataLocacao(), is(ehHoje()));
		error.checkThat(locacaoRetornada.getDataRetorno(), is(ehHojeComDiferencaDias(3)));
		
	}
	
	/**
	 * Forma de invocar metodos privados sem uso do PowerMock
	 * Essa forma ?? uma alternativa a ser usada em casos de metodos muito complexos de serem testados
	 * Exemplo: Projetos/Sistemas Legados
	 */
	@Test
	public void deveCalcularValorLocacao() throws Exception {
		//cenario
		filmes = Arrays.asList(umFilme().agora());
		
		//acao
		Class<LocacaoService> clazz = LocacaoService.class;
		Method metodo = clazz.getDeclaredMethod("calcularValorLocacao", List.class);
		metodo.setAccessible(true);
		Double valor = (Double) metodo.invoke(service, filmes);
		
		//verificacao
		assertThat(valor, is(4.0));
	}
	
	
	
}
