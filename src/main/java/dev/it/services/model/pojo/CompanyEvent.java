package dev.it.services.model.pojo;

public class CompanyEvent {

    private String company;

    //It is either save or remove operation
    private boolean saveOperation;

    public CompanyEvent(String company, boolean saveOperation) {
        this.company = company;
        this.saveOperation = saveOperation;
    }

    public String getCompany() {
        return company;
    }

    public boolean isSaveOperation() {
        return saveOperation;
    }
}
