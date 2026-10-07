import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.Util;
import apexstore.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        try (Communicator communicator = Util.initialize(args)) {
            ObjectAdapter adapter = communicator.createObjectAdapterWithEndpoints(
                "Ecommerce", "tcp -h localhost -p 10000");

            String stripeProxy = "EstrategiaStripe:tcp -h localhost -p 10001";
            String pseProxy = "EstrategiaPSE:tcp -h localhost -p 10001";
            String criptoProxy = "EstrategiaCripto:tcp -h localhost -p 10001";
            String bdProxy = "RepositorioTransacciones:tcp -h localhost -p 10002";

            EstrategiaPagoPrx stripe = EstrategiaPagoPrx.uncheckedCast(
                communicator.stringToProxy(stripeProxy));
            EstrategiaPagoPrx pse = EstrategiaPagoPrx.uncheckedCast(
                communicator.stringToProxy(pseProxy));
            EstrategiaPagoPrx cripto = EstrategiaPagoPrx.uncheckedCast(
                communicator.stringToProxy(criptoProxy));
            RepositorioTransaccionesPrx repositorio = RepositorioTransaccionesPrx.uncheckedCast(
                communicator.stringToProxy(bdProxy));

            Map<String, EstrategiaPagoPrx> estrategias = new HashMap<String, EstrategiaPagoPrx>();
            estrategias.put("STRIPE", stripe);
            estrategias.put("PSE", pse);
            estrategias.put("CRIPTO", cripto);

            ProcesadorPagosContexto procesador = new ProcesadorPagosContexto(estrategias);
            ServicioCheckoutImpl checkout = new ServicioCheckoutImpl(procesador);
            ServicioRegistroTransaccionesImpl registro =
                new ServicioRegistroTransaccionesImpl(checkout, repositorio);

            adapter.add(checkout, Util.stringToIdentity("ServicioCheckout"));
            adapter.add(registro, Util.stringToIdentity("ServicioRegistroTransacciones"));


            adapter.activate();

            communicator.waitForShutdown();
        }
    }
}
