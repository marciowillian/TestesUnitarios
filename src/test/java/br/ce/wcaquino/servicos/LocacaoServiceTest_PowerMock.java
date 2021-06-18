package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.builders.FilmeBuilder.umFilme;
import static br.ce.wcaquino.builders.UsuarioBuilder.umUsuario;
import static br.ce.wcaquino.matchers.MatchersProprios.caiEmUmaSegunda;
import static br.ce.wcaquino.matchers.MatchersProprios.ehHoje;
import static br.ce.wcaquino.matchers.MatchersProprios.ehHojeComDiferencaDias;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import br.ce.wcaquino.dao.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.utils.DataUtils;

@PowerMockIgnore("jdk.internal.reflect.*")
@RunWith(PowerMockRunner.class)
@PrepareForTest({LocacaoService.class, DataUtils.class})
public class LocacaoServiceTest_PowerMock {

	@InjectMocks
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
		service = PowerMockito.spy(service);
	}

	@Test
	public void deveTestarLocacaoComSucesso() throws Exception {
//		assumeFalse(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		
		PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(DataUtils.obterData(16, 06, 2021));

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

	@Test
	public void deveDevolverFilmeNaSegundaAoLocarNoSabado() throws Exception {
//		assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		
		// cenario
		usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 1, 4.0));

		PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(DataUtils.obterData(13, 06, 2021));
		
		// acao
		Locacao locacao = service.alugarFilme(usuario, filmes);

		// verificacao
		assertThat(locacao.getDataRetorno(), caiEmUmaSegunda());
		PowerMockito.verifyNew(Date.class, times(2)).withNoArguments();
	}

	@Test
	public void deveAlugarFilme_SemCalcularValor() throws Exception {
		//cenario
		usuario = umUsuario().agora();
		filmes = Arrays.asList(umFilme().agora());
		
		PowerMockito.doReturn(1.0).when(service, "calcularValorLocacao", filmes);
		
		//acao
		Locacao locacao = service.alugarFilme(usuario, filmes);
		
		//verificacao
		assertThat(locacao.getValor(), is(1.0));
		PowerMockito.verifyPrivate(service).invoke("calcularValorLocacao", filmes);
	}

	@Test
	public void deveCalcularValorLocacao() throws Exception {
		//cenario
		filmes = Arrays.asList(umFilme().agora());
		
		//acao
		Double valor = (Double)  Whitebox.invokeMethod(service, "calcularValorLocacao", filmes);
		
		//verificacao
		assertThat(valor, is(4.0));
	}
	
	
	
}
