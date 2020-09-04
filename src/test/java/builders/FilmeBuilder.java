package builders;

import br.ce.wcaquino.entidades.Filme;

public class FilmeBuilder {
    private Filme filme;

    private FilmeBuilder() {}

    public static FilmeBuilder umFilme() {
        FilmeBuilder filmeBuilder = new FilmeBuilder();
        filmeBuilder.filme = new Filme("As Branquelas", 1, 3.5);
        return filmeBuilder;
    }


    public static FilmeBuilder umFilmeSemEstoque() {
        FilmeBuilder filmeBuilder = new FilmeBuilder();
        filmeBuilder.filme = new Filme("As Branquelas", 0, 3.5);
        return filmeBuilder;
    }

    public Filme agora() {
        return filme;
    }

    public FilmeBuilder comValor(double valor) {
        filme.setPrecoLocacao(valor);
        return this;
    }
}
