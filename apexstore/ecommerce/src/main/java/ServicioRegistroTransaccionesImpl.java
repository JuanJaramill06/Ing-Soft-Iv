import apexstore.*;

public class ServicioRegistroTransaccionesImpl implements apexstore.ServicioRegistroTransacciones {
    private ServicioCheckoutImpl checkoutServicio;
    private RepositorioTransaccionesPrx repositorioPrx;

    public ServicioRegistroTransaccionesImpl(ServicioCheckoutImpl checkoutServicio,
                                         RepositorioTransaccionesPrx repositorioPrx) {
        this.checkoutServicio = checkoutServicio;
        this.repositorioPrx = repositorioPrx;
    }

    @Override
    public void registrarResultado(ResultadoPago resultado, com.zeroc.Ice.Current current) {
        checkoutServicio.actualizarEstadoPago(resultado);

        try {
            Transaccion transaccion = new Transaccion();
            transaccion.idTransaccion = resultado.idTransaccion;
            transaccion.estado = resultado.estado;
            transaccion.idExterno = resultado.idExterno;
            transaccion.motivo = resultado.motivo;
            transaccion.monto = 0;
            transaccion.moneda = "COP";
            transaccion.medioPago = "DESCONOCIDO";

            repositorioPrx.persistirTransaccion(transaccion);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
