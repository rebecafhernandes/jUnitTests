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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class LocacaoServiceTest {
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
        locacaoService = new LocacaoService();

        filmes.add(new Filme("As Branquelas", 1, 10.00));
        filmes.add(new Filme("Queria ter sua vida", 1, 7.00));
    }

    @Test
    public void teste() throws Exception {
        //Cenário
        Usuario usuario = new Usuario("Rebeca");
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

    @Test
    public void testeFilmeSemEstoque2() {
        //Cenário
        Usuario usuario = new Usuario("Rebeca");

        //Ação
        try {
            locacaoService.alugarFilme(usuario, filmes);
//            Assert.fail("Deveria parar fio!");
        } catch (Exception e) {
            assertThat(e.getMessage(), is("Filme sem estoque!"));
        }
    }

    @Test
    public void testeUsuarioVazio() throws FilmeSemEstoqueException {
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
    public void testeFilmeVazio() throws FilmeSemEstoqueException, LocadoraException {
        //Cenário
        Usuario usuario = new Usuario("Rebeca");

        expectedException.expect(LocadoraException.class);
        expectedException.expectMessage("Filme vazio!");
        //Ação
        locacaoService.alugarFilme(usuario, null);
    }

}
