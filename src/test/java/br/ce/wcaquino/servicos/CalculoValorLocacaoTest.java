package br.ce.wcaquino.servicos;

import br.ce.wcaquino.daos.LocacaoDao;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class CalculoValorLocacaoTest {
    @InjectMocks
    private LocacaoService locacaoService;
    private Usuario usuario;

    @Mock
    private SPCService spc;

    @Mock
    private LocacaoDao locacaoDao;

    @Parameterized.Parameter
    public List<Filme> filmes = new ArrayList<>();

    @Parameterized.Parameter(value = 1)
    public double valorLocacao = 0;

    @Parameterized.Parameter(value = 2)
    public String desc;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        usuario = new Usuario("Rebeca");
    }

    private static Filme filme1 = new Filme("Filme 1", 4, 1.0);
    private static Filme filme2 = new Filme("Filme 2", 3, 2.0);
    private static Filme filme3 = new Filme("Filme 3", 2, 3.0);
    private static Filme filme4 = new Filme("Filme 4", 1, 4.0);
    private static Filme filme5 = new Filme("Filme 5", 4, 1.0);
    private static Filme filme6 = new Filme("Filme 6", 3, 2.0);

    @Parameterized.Parameters(name = "Teste {index} = {2}")
    public static Collection<Object[]> getParametros() {
        return Arrays.asList(new Object[][] {
                {Arrays.asList(filme1, filme2), 3, "2 filmes Sem desconto!"},
                {Arrays.asList(filme1, filme2, filme3), 5.25, "3 filmes 25%"},
                {Arrays.asList(filme1, filme2, filme3, filme4), 7.25, "4 filmes 50%"},
                {Arrays.asList(filme1, filme2, filme3, filme4, filme5), 7.5, "5 filmes 75%"},
                {Arrays.asList(filme1, filme2, filme3, filme4, filme5, filme6), 7.5, "6 filmes 100%"},
        });
    }

    @Test
    public void deveCalcularValorLocacaoComDesconto() throws FilmeSemEstoqueException, LocadoraException {
        //Ação
        Locacao locacao = locacaoService.alugarFilme(usuario, filmes);

        //Verificaçao
        //2 + 3 + 3 = 8
        assertThat(locacao.getValor(), is(valorLocacao));
    }

    /*@Test
    public void print() {
        System.out.println("Valor: " + valorLocacao);
    }*/
}
