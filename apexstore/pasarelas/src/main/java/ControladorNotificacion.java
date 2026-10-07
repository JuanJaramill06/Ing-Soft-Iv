import apexstore.*;

public class ControladorNotificacion {
    private ServicioCheckoutPrx checkoutPrx;

    public ControladorNotificacion(ServicioCheckoutPrx checkoutPrx) {
        this.checkoutPrx = checkoutPrx;
    }

    public void setCheckoutPrx(ServicioCheckoutPrx checkoutPrx) {
        this.checkoutPrx = checkoutPrx;
    }

    public void notificarResultadoPago(ResultadoPago resultado) {
        if (checkoutPrx != null) {
            try {
                checkoutPrx.actualizarEstadoPago(resultado.idTransaccion, resultado.estado,
                    resultado.idExterno, resultado.motivo);
            } catch (Exception e) {
                System.err.println("Error notificando resultado: " + e.getMessage());
            }
        }
    }
}
