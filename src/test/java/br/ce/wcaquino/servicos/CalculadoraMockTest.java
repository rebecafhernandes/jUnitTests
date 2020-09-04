package br.ce.wcaquino.servicos;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static org.mockito.Mockito.when;

public class CalculadoraMockTest {
    @Test
    public void teste() {
        Calculadora calc = Mockito.mock(Calculadora.class);

        ArgumentCaptor<Integer> argumentCaptor = ArgumentCaptor.forClass(Integer.class);

        when(calc.somar(argumentCaptor.capture(), argumentCaptor.capture())).thenReturn(5);


        System.out.println(calc.somar(1, 8));
        System.out.println(argumentCaptor.getAllValues());
    }
}
