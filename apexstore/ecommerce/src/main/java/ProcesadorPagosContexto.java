import apexstore.*;
import java.util.*;

public class ProcesadorPagosContexto {
    private Map<String, EstrategiaPagoPrx> estrategias;

    public ProcesadorPagosContexto(Map<String, EstrategiaPagoPrx> estrategias) {
        this.estrategias = estrategias;
    }

    public void iniciarPagoOrden(SolicitudPago solicitudPago, String medioPago) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EstrategiaPagoPrx estrategia = estrategias.get(medioPago);
                    if (estrategia == null) {
                        return;
                    }

                    estrategia.procesarPago(solicitudPago);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
