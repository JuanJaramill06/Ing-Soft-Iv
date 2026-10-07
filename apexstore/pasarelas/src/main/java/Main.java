import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.Util;
import apexstore.*;

public class Main {
    public static void main(String[] args) {
        try (Communicator communicator = Util.initialize(args)) {
            ObjectAdapter adapter = communicator.createObjectAdapterWithEndpoints(
                "Pasarelas", "tcp -h localhost -p 10001");

            String checkoutProxy = "ServicioCheckout:tcp -h localhost -p 10000";
            ServicioCheckoutPrx checkoutPrx = ServicioCheckoutPrx.uncheckedCast(communicator.stringToProxy(checkoutProxy));

            ControladorNotificacion controlador = new ControladorNotificacion(checkoutPrx);

            adapter.add(new EstrategiaStripe(controlador), Util.stringToIdentity("estrategiaStripe"));
            adapter.add(new EstrategiaPSE(controlador), Util.stringToIdentity("estrategiaPSE"));
            adapter.add(new EstrategiaCripto(controlador), Util.stringToIdentity("estrategiaCripto"));

            adapter.activate();
            communicator.waitForShutdown();
        }
    }
}
