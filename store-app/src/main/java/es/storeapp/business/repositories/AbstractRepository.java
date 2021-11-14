package es.storeapp.business.repositories;

import es.storeapp.business.exceptions.InstanceNotFoundException;
import es.storeapp.business.utils.ExceptionGenerationUtils;
import es.storeapp.common.Constants;
import java.text.MessageFormat;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.GenericTypeResolver;

public abstract class AbstractRepository<T> {

    protected final Logger logger;
    
    private static final String FIND_ALL_QUERY = "SELECT t FROM :name t";
    private static final String FIND_ALL_ORDERED_QUERY = "SELECT t FROM :name t ORDER BY t.:orderColumn";
    private static final String FIND_BY_TEXT_ATTRIBUTE_QUERY = "SELECT t FROM :name t WHERE t.:attribute = :value ORDER BY t.:orderColumn";
    
    private final Class<T> genericType;

    @PersistenceContext
    protected EntityManager entityManager;

    @Autowired
    ExceptionGenerationUtils exceptionGenerationUtils;
    
    public AbstractRepository() {
        this.genericType = (Class<T>) GenericTypeResolver.resolveTypeArgument(getClass(), AbstractRepository.class);
        this.logger = LoggerFactory.getLogger(this.genericType);
    }

    public T create(T entity) {
        entityManager.persist(entity);
        return entity;
    }

    public T update(T entity) {
        entityManager.merge(entity);
        return entity;
    }
    
    public void remove(T entity) {
        entityManager.remove(entity);
    }

    public T findById(Long id) throws InstanceNotFoundException {
        try{
            T t = entityManager.find(genericType, id);
            if(t == null) {
                throw new NoResultException(Long.toString(id));
            }
            return t;
        } catch(NoResultException e) {
            logger.error(e.getMessage(), e);
            throw exceptionGenerationUtils.toInstanceNotFoundException(id, genericType.getSimpleName(), 
                    Constants.INSTANCE_NOT_FOUND_MESSAGE);
        }
    }
    
    public List<T> findAll() {
        try {
            Query query = entityManager.createQuery(FIND_ALL_QUERY);
            query.setParameter("name", genericType.getSimpleName());
            return query.getResultList();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }
    
    public List<T> findAll(String orderColumn) {
        try {
            Query query = entityManager.createQuery(MessageFormat.format(FIND_ALL_ORDERED_QUERY,
                    genericType.getSimpleName(), orderColumn));
            query.setParameter("name", genericType.getSimpleName());
            query.setParameter("orderColumn", orderColumn);
            return query.getResultList();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }
    
    public List<T> findByStringAttribute(String attribute, String value, String orderColumn) {
        try {
            Query query = entityManager.createQuery(FIND_BY_TEXT_ATTRIBUTE_QUERY);
            query.setParameter("name", genericType.getSimpleName());
            query.setParameter("attribute", attribute);
            query.setParameter("value", value);
            query.setParameter("orderColumn", orderColumn);
            return query.getResultList();
        } catch (Exception e){
            logger.error(e.getMessage(), e);
            return null;
        }
    }
}
