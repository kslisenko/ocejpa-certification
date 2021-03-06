package net.slisenko.jpa.examples.ee.ejb;

import net.slisenko.jpa.examples.ee.model.EESimpleEntity;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.*;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/userTx")
@TransactionManagement(TransactionManagementType.BEAN)
@Stateless
public class TestBeanManagedTransactions {

    @Resource
    private UserTransaction tx;

    @PersistenceContext
    private EntityManager em;

    @EJB
    private AnotherBean anotherBean;

    @GET
    @Path("/do")
    @Produces(MediaType.APPLICATION_JSON)
    public EESimpleEntity testUserTransaction() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        tx.begin();
        EESimpleEntity result = new EESimpleEntity("entity stored in user transaction");
        em.persist(result);
        tx.commit();
        return result;
    }

    @GET
    @Path("/propagation")
    @Produces(MediaType.APPLICATION_JSON)
    public String testPropagation() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        tx.begin();
        EESimpleEntity result = new EESimpleEntity("entity stored in user transaction");
        em.persist(result);
        anotherBean.mandatoryTx(result.getId());
        System.out.println("Status = " + tx.getStatus());
        if (tx.getStatus() == Status.STATUS_ACTIVE) {
            System.out.println("Status = active");
        }

        tx.commit();
        return "ok";
    }
}