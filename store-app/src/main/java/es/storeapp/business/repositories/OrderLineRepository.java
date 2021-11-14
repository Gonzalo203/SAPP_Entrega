package es.storeapp.business.repositories;

import es.storeapp.business.entities.*;
import java.text.MessageFormat;
import javax.persistence.Query;
import org.springframework.stereotype.Repository;

@Repository
public class OrderLineRepository extends AbstractRepository<OrderLine>{

    private static final String FIND_BY_USER_AND_PRODUCT_QUERY = 
            "SELECT COUNT(*) FROM OrderLine o WHERE " +
            "o.order.state = es.storeapp.business.entities.OrderState.COMPLETED " + 
            "AND o.order.user.id = :userId AND o.product.id = productId";

    public boolean findIfUserBuyProduct(Long userId, Long productId) {
        try {
            Query query = entityManager.createQuery(FIND_BY_USER_AND_PRODUCT_QUERY);
            query.setParameter("userId", userId);
            query.setParameter("productId", productId);
            return ((Long) query.getSingleResult()) > 0;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }
}
