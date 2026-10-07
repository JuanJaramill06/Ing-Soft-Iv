import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.Util;
import apexstore.*;

public class Main {
    public static void main(String[] args) {
        try (Communicator communicator = Util.initialize(args)) {
            ObjectAdapter adapter = communicator.createObjectAdapterWithEndpoints(
                "Ecommerce", "tcp -h localhost -p 10000");

            String stripeProxy = "estrategiaStripe:tcp -h localhost -p 10001";
            String pseProxy = "estrategiaPSE:tcp -h localhost -p 10001";
            String criptoProxy = "estrategiaCripto:tcp -h localhost -p 10001";

            EstrategiaPagoPrx stripe = EstrategiaPagoPrx.uncheckedCast(communicator.stringToProxy(stripeProxy));
            EstrategiaPagoPrx pse = EstrategiaPagoPrx.uncheckedCast(communicator.stringToProxy(pseProxy));
            EstrategiaPagoPrx cripto = EstrategiaPagoPrx.uncheckedCast(communicator.stringToProxy(criptoProxy));

            ServicioCheckoutImpl checkout = new ServicioCheckoutImpl(stripe, pse, cripto);
            adapter.add(checkout, Util.stringToIdentity("ServicioCheckout"));
            adapter.activate();

            communicator.waitForShutdown();
        }
    }
}
