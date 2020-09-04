package br.ce.wcaquino.daos;

import br.ce.wcaquino.entidades.Locacao;

import java.util.List;

public interface LocacaoDao {
    public void salvar(Locacao locacao);

    List<Locacao> obterLocacoesPendentes();
}
