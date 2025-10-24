package client.src;

import javax.naming.Context;
import javax.naming.InitialContext;
import java.util.Properties;
import com.banque.change.remote.ChangeRemote;

public class Main {
    public static void main(String[] args) throws Exception {
        Properties props = new Properties();
        props.put("jboss.naming.client.ejb.context", "true");
        Context context = new InitialContext(props);

        ChangeRemote change = (ChangeRemote) context.lookup(
                "ejb:/ChangeBean/ChangeBean!com.banque.change.remote.ChangeRemote");

        System.out.println("Default Currency: " + change.getDefaultCurrency());
    }
}
