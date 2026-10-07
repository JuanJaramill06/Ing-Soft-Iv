import apexstore.*;

public class ControladorNotificacion {
    private ServicioRegistroTransaccionesPrx registroPrx;

    public ControladorNotificacion(ServicioRegistroTransaccionesPrx registroPrx) {
        this.registroPrx = registroPrx;
    }

    public void notificarResultadoPago(ResultadoPago resultado) {
        try {
            registroPrx.registrarResultado(resultado);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
