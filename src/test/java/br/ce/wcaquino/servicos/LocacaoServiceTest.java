package br.ce.wcaquino.servicos;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.servicos.LocacaoService;
import br.ce.wcaquino.utils.DataUtils;
import matchers.DiaSemanaMatcher;
import matchers.Matchers;
import org.hamcrest.CoreMatchers;
import org.junit.*;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;

import javax.xml.crypto.Data;
import java.util.*;

import static matchers.Matchers.*;
import static matchers.Matchers.caiEm;
import static matchers.Matchers.caiSegunda;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class LocacaoServiceTest {
    private Usuario usuario;
    private LocacaoService locacaoService;
    private List<Filme> filmes = new ArrayList<>();

    //Definição do contador
    private static int cont = 0;

    @Rule
    public ErrorCollector error = new ErrorCollector();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setup() {
        usuario = new Usuario("Rebeca");
        locacaoService = new LocacaoService();

        filmes.add(new Filme("As Branquelas", 1, 10.00));
        filmes.add(new Filme("Queria ter sua vida", 1, 7.00));
    }

    @Test
    public void deveAlugarFilme() throws Exception {
        Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));

        //Ação
        Locacao locacao = locacaoService.alugarFilme(usuario, filmes);

        //Resultado
        Assert.assertEquals(17.0, locacao.getValor(), 0.01);
        Assert.assertTrue(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()));
        Assert.assertFalse(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterDataComDiferencaDias(1)));

        error.checkThat(locacao.getValor(), is(17.0));
        error.checkThat(locacao.getValor(), is(not(6.0)));
        error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()), is(true));

        error.checkThat(locacao.getDataRetorno(), isToday(1));
    }

    @Test(expected = FilmeSemEstoqueException.class)
    public void naoDeveAlugarFilmeSemEstoque() throws FilmeSemEstoqueException, LocadoraException {
        //Cenário
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

        expectedException.expect(LocadoraException.class);
        expectedException.expectMessage("Filme vazio!");
        //Ação
        locacaoService.alugarFilme(usuario, null);
    }

    @Test
    public void deveDevolverSegundaAlugandoSabado() throws FilmeSemEstoqueException, LocadoraException {
//        Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));

        List<Filme> filmesTest = Arrays.asList(new Filme("Desperados", 1, 5.50));

        //Ação
        Locacao locacao = locacaoService.alugarFilme(usuario, filmesTest);

        //Verificaçao
        /*boolean isMonday = DataUtils.verificarDiaSemana(locacao.getDataRetorno(), Calendar.MONDAY);
        assertTrue(isMonday);*/

//        assertThat(locacao.getDataRetorno(), new DiaSemanaMatcher(Calendar.MONDAY));
//        assertThat(locacao.getDataRetorno(), caiEm(Calendar.SUNDAY));
        assertThat(locacao.getDataRetorno(), caiSegunda());
    }
}
