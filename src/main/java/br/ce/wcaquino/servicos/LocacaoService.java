package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.utils.DataUtils.adicionarDias;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.utils.DataUtils;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;

public class LocacaoService {
    public String vPublica;
    protected String vProtegida;
    private String vPrivate;
    String vDefault;
    private double valorLocacao = 0;

    public Locacao alugarFilme(Usuario usuario, List<Filme> filmes) throws FilmeSemEstoqueException, LocadoraException {

        if (usuario == null) {
            throw new LocadoraException("Usuário vazio!");
        }

        if (filmes == null) {
            throw new LocadoraException("Filme vazio!");
        }

        Locacao locacao = new Locacao();
        Calculadora calc = new Calculadora();

        for (int i = 0; i < filmes.size(); i++) {
            Filme filme = filmes.get(i);

            if (filme.getEstoque() == 0) {
                throw new FilmeSemEstoqueException();
            }

            double filmeValue = filme.getPrecoLocacao();

            switch (i) {
                case 2:
                    filmeValue = calc.desconto25Pct(filme.getPrecoLocacao());
                    break;
                case 3:
                    filmeValue = calc.desconto50Pct(filme.getPrecoLocacao());
                    break;
                case 4:
                    filmeValue = calc.desconto75Pct(filme.getPrecoLocacao());
                    break;
                case 5:
                    filmeValue = calc.desconto100Pct(filme.getPrecoLocacao());
                    break;
            }

            valorLocacao += filmeValue;
        }


        locacao.setFilmes(filmes);
        locacao.setUsuario(usuario);
        locacao.setDataLocacao(new Date());
        locacao.setValor(valorLocacao);

        //Entrega no dia seguinte
        Date dataEntrega = new Date();
        dataEntrega = adicionarDias(dataEntrega, 1);

        if (DataUtils.verificarDiaSemana(dataEntrega, Calendar.SUNDAY)) {
            dataEntrega = adicionarDias(dataEntrega, 1);
        }

        locacao.setDataRetorno(dataEntrega);

        //Salvando a locacao...
        return locacao;
    }

}