import com.banque.change.remote.ChangeRemote;

import javax.naming.Context;
import javax.naming.InitialContext;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Properties;

public class ChangeClient {
    public static void main(String[] args) throws Exception {
        Properties jndiProps = new Properties();
        jndiProps.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
        jndiProps.put(Context.PROVIDER_URL, "http-remoting://localhost:8081");
        jndiProps.put(Context.SECURITY_PRINCIPAL, "admin");
        jndiProps.put(Context.SECURITY_CREDENTIALS, "Admin#70365");

        Context ctx = new InitialContext(jndiProps);

        // Lookup EJB
        ChangeRemote change = (ChangeRemote) ctx.lookup(
                "java:global/Change/ChangeBean!com.banque.change.remote.ChangeRemote");

        System.out.println("Default currency: " + change.getDefaultCurrency());
        BigDecimal result = change.convert(BigDecimal.valueOf(100), "USD", "MGA", new Date());
        System.out.println("100 USD -> MGA = " + result);
    }
}
