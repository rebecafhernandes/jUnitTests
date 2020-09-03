package br.ce.wcaquino.servicos;

import br.ce.wcaquino.exceptions.NaoPodeDividirPorZeroException;

public class Calculadora {

    public int somar(int a, int b) {
        return a + b;
    }

    public int subtrair(int a, int b) {
        return a - b;
    }

    public int dividir(int a, int b) throws NaoPodeDividirPorZeroException {
        if (b == 0) {
            throw new NaoPodeDividirPorZeroException();
        }
        return a / b;
    }

    public double desconto25Pct(double val) {
        return val - (val * 0.25);
    }

    public double desconto50Pct(double val) {
        return val * 0.5;
    }

    public double desconto75Pct(double val) {
        return val - (val * 0.75);
    }

    public double desconto100Pct(double val) {
        return 0;
    }
}
