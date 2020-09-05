package suites;

import br.ce.wcaquino.servicos.CalculadoraTest;
import br.ce.wcaquino.servicos.CalculoValorLocacaoTest;
import br.ce.wcaquino.servicos.LocacaoServiceTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({/*LocacaoServiceTest.class,*/ CalculadoraTest.class, CalculoValorLocacaoTest.class})
public class SuiteExecucao {

    @BeforeClass
    public static void before() {
        System.out.println("Before!");
    }

    @AfterClass
    public static void after() {
        System.out.println("After!");
    }

}
