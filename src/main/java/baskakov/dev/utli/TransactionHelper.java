package baskakov.dev.utli;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
public class TransactionHelper {
    private final SessionFactory sessionFactory;

    public TransactionHelper(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public<T> T executeInTransaction(Supplier<T> action) {
        Session session = sessionFactory.getCurrentSession();
        Transaction transaction = session.getTransaction();

        if (!transaction.getStatus().equals(TransactionStatus.NOT_ACTIVE)) {
            return action.get();
        }

        try {
            session.beginTransaction();
            T result = action.get();
            transaction.commit();
            return result;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }
}
