package br.com.semalo.parceiro.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;



import java.util.List;
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmpresaResponse {


    private String founded;
    private Company company;
    private Address address;
    private List<Phone> phones;
    private List<Email> emails;
    private List<Registration> registrations;


    public String getFounded() { return founded; }
    public void setFounded(String founded) { this.founded = founded; }

    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }

    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }

    public List<Phone> getPhones() { return phones; }
    public void setPhones(List<Phone> phones) { this.phones = phones; }

    public List<Email> getEmails() { return emails; }
    public void setEmails(List<Email> emails) { this.emails = emails; }

    public List<Registration> getRegistrations() { return registrations; }
    public void setRegistrations(List<Registration> registrations) { this.registrations = registrations; }

}