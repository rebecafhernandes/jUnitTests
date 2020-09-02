package br.ce.wcaquino.servicos;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.servicos.LocacaoService;
import br.ce.wcaquino.utils.DataUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;

import java.util.Date;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class LocacaoServiceTest {
    @Rule
    public ErrorCollector error = new ErrorCollector();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void teste() throws Exception {
        //Cenário
        LocacaoService locacaoService = new LocacaoService();
        Usuario usuario = new Usuario("Rebeca");
        Filme filme = new Filme("As Branquelas", 1, 10.00);

        //Ação
        Locacao locacao = locacaoService.alugarFilme(usuario, filme);

        //Resultado
        Assert.assertEquals(10.0, locacao.getValor(), 0.01);
        Assert.assertTrue(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()));
        Assert.assertTrue(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterDataComDiferencaDias(1)));

        error.checkThat(locacao.getValor(), is(10.0));
        error.checkThat(locacao.getValor(), is(not(6.0)));
        error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()), is(true));

    }

    @Test(expected = FilmeSemEstoqueException.class)
    public void testeFilmeSemEstoque() throws Exception {
        //Cenário
        LocacaoService locacaoService = new LocacaoService();
        Usuario usuario = new Usuario("Rebeca");
        Filme filme = new Filme("As Branquelas", 0, 10.00);

        //Ação
        locacaoService.alugarFilme(usuario, filme);
    }

    @Test
    public void testeFilmeSemEstoque2() {
        //Cenário
        LocacaoService locacaoService = new LocacaoService();
        Usuario usuario = new Usuario("Rebeca");
        Filme filme = new Filme("As Branquelas", 2, 10.00);

        //Ação
        try {
            locacaoService.alugarFilme(usuario, filme);
            Assert.fail("Deveria parar fio!");
        } catch (Exception e) {
            assertThat(e.getMessage(), is("Filme sem estoque!"));
        }
    }

    @Test
    public void testeFilmeSemEstoque3() throws Exception {
        //Cenário
        LocacaoService locacaoService = new LocacaoService();
        Usuario usuario = new Usuario("Rebeca");
        Filme filme = new Filme("As Branquelas", 0, 10.00);

        expectedException.expect(Exception.class);
        expectedException.expectMessage("Filme sem estoqueeeeeee!");

        //Ação
        locacaoService.alugarFilme(usuario, filme);

    }

    @Test
    public void testeUsuarioVazio() throws FilmeSemEstoqueException {
        //Cenário
        LocacaoService locacaoService = new LocacaoService();
        Filme filme = new Filme("As Branquelas", 1, 10.00);

        //Ação
        try {
            locacaoService.alugarFilme(null, filme);
            Assert.fail();
        } catch (LocadoraException e) {
            assertThat(e.getMessage(), is("Usuário vazio!"));
        }
    }

    @Test
    public void testeFilmeVazio() throws FilmeSemEstoqueException, LocadoraException {
        //Cenário
        LocacaoService locacaoService = new LocacaoService();
        Usuario usuario = new Usuario("Rebeca");
        Filme filme = new Filme("As Branquelas", 0, 10.00);

        expectedException.expect(LocadoraException.class);
        expectedException.expectMessage("Filme vazio!");
        //Ação
        locacaoService.alugarFilme(usuario, null);
    }

}
