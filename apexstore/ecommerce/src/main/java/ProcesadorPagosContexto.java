import apexstore.*;
import java.util.*;

public class ProcesadorPagosContexto {
    private Map<String, EstrategiaPagoPrx> estrategias;

    public ProcesadorPagosContexto(Map<String, EstrategiaPagoPrx> estrategias) {
        this.estrategias = estrategias;
    }

    public void procesarConEstrategia(SolicitudPago solicitud, String medioPago) {
        EstrategiaPagoPrx estrategia = estrategias.get(medioPago);
        if (estrategia != null) {
            try {
                estrategia.procesarPago(solicitud);
            } catch (Exception e) {}
        }
    }
}
