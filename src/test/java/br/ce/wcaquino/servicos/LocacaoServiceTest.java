package br.ce.wcaquino.servicos;

import br.ce.wcaquino.daos.LocacaoDao;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.utils.DataUtils;
import builders.UsuarioBuilder;
import matchers.Matchers;
import org.junit.*;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.mockito.*;

import java.util.*;

import static builders.FilmeBuilder.umFilme;
import static builders.LocacaoBuilder.umLocacao;
import static builders.UsuarioBuilder.umUsuario;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class LocacaoServiceTest {
    @InjectMocks
    private LocacaoService locacaoService;

    private List<Filme> filmes = new ArrayList<>();

    @Mock
    private SPCService spc;

    @Mock
    private LocacaoDao locacaoDao;

    @Mock
    private EmailService emailService;

    //Definição do contador
    private static int cont = 0;

    @Rule
    public ErrorCollector error = new ErrorCollector();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        filmes.add(umFilme().agora());
    }

    @Test
    public void deveAlugarFilme() throws Exception {
        Assume.assumeFalse(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));

        //Cenário
        List<Filme> filmes = Arrays.asList(umFilme().comValor(17.0).agora());
        Usuario usuario = umUsuario().agora();

        //Ação
        Locacao locacao = locacaoService.alugarFilme(usuario, filmes);

        error.checkThat(locacao.getValor(), is(17.0));
        error.checkThat(locacao.getValor(), is(not(6.0)));
        error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()), is(true));

//        error.checkThat(locacao.getDataRetorno(), isToday(1));
    }

    @Test(expected = FilmeSemEstoqueException.class)
    public void naoDeveAlugarFilmeSemEstoque() throws FilmeSemEstoqueException, LocadoraException {
        //Cenário
        Usuario usuario = umUsuario().agora();
        List<Filme> filmesEstoque = new ArrayList<>();
        filmesEstoque.add(new Filme("As Branquelas", 0, 10.00));

        locacaoService.alugarFilme(usuario, filmesEstoque);
    }

    @Test
    public void naoDeveAlugarFilmeSemUsuario() throws FilmeSemEstoqueException {
        //Cenário
        LocacaoService locacaoService = new LocacaoService();

        //Ação
        try {
            locacaoService.alugarFilme(null, filmes);
            Assert.fail();
        } catch (LocadoraException e) {
            assertThat(e.getMessage(), is("Usuário vazio!"));
        }
    }

    @Test
    public void naoDeveAlugarFilmeSemFilme() throws FilmeSemEstoqueException, LocadoraException {
        //Cenário
        Usuario usuario = umUsuario().agora();

        expectedException.expect(LocadoraException.class);
        expectedException.expectMessage("Filme vazio!");
        //Ação
        locacaoService.alugarFilme(usuario, null);
    }

    @Test
    public void deveDevolverSegundaAlugandoSabado() throws FilmeSemEstoqueException, LocadoraException {
//        Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
        //Cenário
        Usuario usuario = umUsuario().agora();

        List<Filme> filmesTest = Arrays.asList(new Filme("Desperados", 1, 5.50));

        //Ação
        Locacao locacao = locacaoService.alugarFilme(usuario, filmesTest);

        //Verificaçao
        /*boolean isMonday = DataUtils.verificarDiaSemana(locacao.getDataRetorno(), Calendar.MONDAY);
        assertTrue(isMonday);*/

//        assertThat(locacao.getDataRetorno(), new DiaSemanaMatcher(Calendar.MONDAY));
//        assertThat(locacao.getDataRetorno(), caiEm(Calendar.SUNDAY));
//        assertThat(locacao.getDataRetorno(), caiSegunda());
    }

    @Test
    public void naoDeveAlugarFilmeNegativado() throws Exception {
        //Cenário
        Usuario usuario = umUsuario().agora();

        List<Filme> filmes = Arrays.asList(umFilme().agora());

        //Se passar pelo método possuiNegativacao com qualquer usuário, retorna true!
        when(spc.possuiNegativacao(Mockito.any(Usuario.class))).thenReturn(true);

//        expectedException.expect(LocadoraException.class);
//        expectedException.expectMessage("Usuário negativado.");

        //Ação
        try {
            locacaoService.alugarFilme(usuario, filmes);
            Assert.fail();
        } catch (LocadoraException e) {
            Assert.assertThat(e.getMessage(), is("SPC fora do ar!"));
        }

        //Verificação
        verify(spc).possuiNegativacao(usuario);
    }

    @Test
    public void deveEnviarEmailLocacoesAtrasadas() {
        //Cenário
        Usuario usuario = umUsuario().agora();

        Usuario usuario1 = umUsuario().comNome("Usuário em dia").agora();
        Usuario usuario2 = umUsuario().comNome("Atrasadinho").agora();

        List<Locacao> locacoesPendentes = Arrays.asList(
                umLocacao().atrasado().comUsuario(usuario).agora(),
                umLocacao().comUsuario(usuario1).agora(),
                umLocacao().atrasado().comUsuario(usuario2).agora(),
                umLocacao().atrasado().comUsuario(usuario2).agora()
                );

        //Quando o 'obterLocacoesPendentes' for chamado, retornar a lista de locacoes pendentes
        when(locacaoDao.obterLocacoesPendentes()).thenReturn(locacoesPendentes);

        //Ação
        locacaoService.notificarAtrasos();


        //Verificação
        verify(emailService, times( 3)).notificarAtraso(Mockito.any(Usuario.class));

        verify(emailService).notificarAtraso(usuario);
        verify(emailService, never()).notificarAtraso(usuario1);
        verify(emailService, atLeastOnce()).notificarAtraso(usuario2);

        verifyNoMoreInteractions(emailService);
    }

    @Test
    public void deveTratarErroNoSPC() throws Exception {
        //Cenário
        Usuario usuario = UsuarioBuilder.umUsuario().agora();
        List<Filme> filmes = Arrays.asList(umFilme().agora());

        when(spc.possuiNegativacao(Mockito.any(Usuario.class))).thenThrow(new Exception("SPC fora do ar!"));

        //Verificação
        expectedException.expect(LocadoraException.class);
        expectedException.expectMessage("SPC fora do ar!");

        //Ação
        locacaoService.alugarFilme(usuario, filmes);

    }

    @Test
    public void deveProrrogarLocacao() {
        //Cenário
        Locacao locacao = umLocacao().agora();

        //Ação
        locacaoService.prorrogarLocacao(locacao, 3);

        //Verificação
        ArgumentCaptor<Locacao> argumentCaptor = ArgumentCaptor.forClass(Locacao.class);
        Mockito.verify(locacaoDao).salvar(argumentCaptor.capture());
        Locacao locacaoRetornada = argumentCaptor.getValue();

        error.checkThat(locacaoRetornada.getValor(), is(10.5));
        error.checkThat(locacaoRetornada.getDataLocacao(), Matchers.isToday(0));
        error.checkThat(locacaoRetornada.getDataRetorno(), Matchers.isToday(3));
    }
}
