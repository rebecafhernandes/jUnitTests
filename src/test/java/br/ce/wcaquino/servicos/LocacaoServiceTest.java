package br.ce.wcaquino.servicos;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.servicos.LocacaoService;
import br.ce.wcaquino.utils.DataUtils;
import org.hamcrest.CoreMatchers;
import org.junit.*;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;

import javax.xml.crypto.Data;
import java.util.*;

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
        Assume.assumeFalse(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));

        //Ação
        Locacao locacao = locacaoService.alugarFilme(usuario, filmes);

        //Resultado
        Assert.assertEquals(17.0, locacao.getValor(), 0.01);
        Assert.assertTrue(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()));
        Assert.assertTrue(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterDataComDiferencaDias(1)));

        error.checkThat(locacao.getValor(), is(17.0));
        error.checkThat(locacao.getValor(), is(not(6.0)));
        error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
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
    public void naoDevePagar25NoFilmeTres() throws FilmeSemEstoqueException, LocadoraException {
        //Cenário
        List<Filme> filmesTest = Arrays.asList(
                new Filme("Filme 1", 4, 2.0),
                new Filme("Filme 2", 3, 3.0),
                new Filme("Filme 3", 2, 4.0)
        );

        //Ação
        Locacao locacao = locacaoService.alugarFilme(usuario, filmesTest);

        //Verificaçao
        //2 + 3 + 3 = 8
        assertThat(locacao.getValor(), is(8.0));
    }

    @Test
    public void naoDevePagar50NoFilmeQuatro() throws FilmeSemEstoqueException, LocadoraException {
        //Cenário
        List<Filme> filmesTest = Arrays.asList(
                new Filme("Filme 1", 4, 2.0),
                new Filme("Filme 2", 3, 3.0),
                new Filme("Filme 3", 2, 4.0),
                new Filme("Filme 4", 1, 5.0)
        );

        //Ação
        Locacao locacao = locacaoService.alugarFilme(usuario, filmesTest);

        //Verificaçao
        //2 + 3 + 3 + 2.5
        assertThat(locacao.getValor(), is(10.5));
    }

    @Test
    public void naoDevePagar75NoFilmeCinco() throws FilmeSemEstoqueException, LocadoraException {
        //Cenário
        List<Filme> filmesTest = Arrays.asList(
                new Filme("Filme 1", 4, 2.0),
                new Filme("Filme 2", 3, 3.0),
                new Filme("Filme 3", 2, 4.0),
                new Filme("Filme 4", 1, 5.0),
                new Filme("Filme 5", 5, 1.0)
        );

        //Ação
        Locacao locacao = locacaoService.alugarFilme(usuario, filmesTest);

        //Verificaçao
        //2 + 3 + 3 + 2.5 + 0.25
        assertThat(locacao.getValor(), is(10.75));
    }

    @Test
    public void naoDeveDevolverFilmeDomingo() throws FilmeSemEstoqueException, LocadoraException {
        Assume.assumeFalse(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));

        List<Filme> filmesTest = Arrays.asList(new Filme("Desperados", 1, 5.50));

        //Ação
        Locacao locacao = locacaoService.alugarFilme(usuario, filmesTest);

        //Verificaçao
        boolean isMonday = DataUtils.verificarDiaSemana(locacao.getDataRetorno(), Calendar.MONDAY);
        assertFalse(isMonday);
    }
}
