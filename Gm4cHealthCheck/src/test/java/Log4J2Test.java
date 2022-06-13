import java.util.UUID;

import com.gm4c.logging.domain.MessagesEnum;
import com.gm4c.logging.factories.ContextLoggerFactory;
import com.gm4c.logging.ports.IContextLogger;

public class Log4J2Test {

	private static final IContextLogger LOG = ContextLoggerFactory.getLogger(Log4J2Test.class);

	public static void main(String[] args) {

		String transactionId = UUID.randomUUID().toString();
		String correlationId = UUID.randomUUID().toString();
		
		LOG.clearContext();
		LOG.setContext(correlationId);
		LOG.setContext("TRANSACTION_ID", transactionId);
		LOG.error("Mensagem de Log pela interface do SL4J");
		LOG.error(MessagesEnum.GM4C_TEF0001I.getCodAndDescription());
		
		User user = new User(1, "Dennis");
		Item myItem = new Item(1, "LogItem", user);
				
        LOG.info((Object)myItem, MessagesEnum.GM4C_TEF0013I.getCodAndDescription());
		LOG.error(MessagesEnum.GM4C_TEF0013I.getCodAndDescription());
	}
}