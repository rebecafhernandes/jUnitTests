package br.ce.wcaquino.servicos;

import br.ce.wcaquino.exceptions.NaoPodeDividirPorZeroException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CalculadoraTest {
    private Calculadora calc;
    private int a = 0;
    private int b = 0;

    @Before
    public void construcao() {
        a = 5;
        b = 3;

        calc = new Calculadora();
    }

    @Test
    public void somaDoisValores() {
        //Ação
        int resultado = calc.somar(a, b);

        //Verificação
        assertEquals(8, resultado);
    }

    @Test
    public void subtrairDoisValores() {
        //Ação
        int resultado = calc.subtrair(a, b);

        //Verificação
        assertEquals(2, resultado);
    }

    @Test(expected = NaoPodeDividirPorZeroException.class)
    public void excecaoDividirZero() throws NaoPodeDividirPorZeroException {
        int a = 10;
        int b = 0;

        Calculadora calc = new Calculadora();

        calc.dividir(a, b);
    }
}
