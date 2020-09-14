package dev.it.services.service;

import dev.it.services.model.Company;
import dev.it.services.model.pojo.CompanyEvent;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.ObservesAsync;
import javax.transaction.Transactional;

@ApplicationScoped
public class CompanyService {

    protected Logger logger = Logger.getLogger(getClass());

    @Transactional
    public void onEvent(@ObservesAsync CompanyEvent event) {
        if (event.isSaveOperation()) {
            logger.error("ADDED");
            saveOrUpdateCompany(event.getCompany());
        } else {
            logger.error("REMOVED");
            removeOrUpdateCompany(event.getCompany());
        }
    }

    private void saveOrUpdateCompany(String companyName) {
        Company company = Company.find("name", companyName).firstResult();
        if (company == null) {
            createCompany(companyName);
        } else {
            updateCompany(company);
        }
    }

    private void createCompany(String companyName) {
        logger.info("persist");
        Company company = new Company(companyName);
        company.persist();
        if (company == null) {
            logger.error("Failed to create resource: " + company);
        }
    }

    private void updateCompany(Company company) {
        logger.info("update");
        company.number_of++;
    }

    private void removeOrUpdateCompany(String companyName) {
        Company company = Company.find("name", companyName).firstResult();
        company.number_of--;
        if (company.number_of == 0) {
            company.delete();
        }
    }
}
