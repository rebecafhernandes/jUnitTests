package matchers;

import java.util.Calendar;

public class Matchers {
    public static DiaSemanaMatcher caiEm(int diaSemana) {
        return new DiaSemanaMatcher(diaSemana);
    }

    public static DiaSemanaMatcher caiSegunda() {
        return new DiaSemanaMatcher(Calendar.MONDAY);
    }

    public static DiferencaDiasMatcher isToday(int diferenciaDias) {
        return new DiferencaDiasMatcher(diferenciaDias);
    }

    public static DiaSemanaMatcher caiNumaSegunda(){
        return new DiaSemanaMatcher(Calendar.MONDAY);
    }
}
