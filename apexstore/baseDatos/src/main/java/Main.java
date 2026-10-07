import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.Util;

public class Main {
    public static void main(String[] args) {
        try (Communicator communicator = Util.initialize(args)) {
            ObjectAdapter adapter = communicator.createObjectAdapterWithEndpoints(
                "RepositorioTransacciones", "tcp -h localhost -p 10002");

            String dbUrl = "jdbc:postgresql://localhost:5432/apexstore_db";
            String dbUser = "apexstore_user";
            String dbPassword = "apexstore_pass123";

            DBPostgreSQLTransacciones servant = new DBPostgreSQLTransacciones(dbUrl, dbUser, dbPassword);
            adapter.add(servant, Util.stringToIdentity("repositorioTransacciones"));

            adapter.activate();

            communicator.waitForShutdown();
        }
    }
}
