package br.com.semalo.parceiro.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ParceiroDTO {

    private String nome;
    private Boolean optante;
    private String since;
    private String zip;
    private String state;
    private String city;
    private String number;
    private String street;
    private String district;
    private String latitude;
    private String longitude;
    private List<Phone> phones;
    private String email;
    private String inscricaoEstadual;
    private Boolean isMei;




    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public Boolean getOptante() { return optante; }
    public void setOptante(Boolean optante) { this.optante = optante; }

    public String getSince() { return since; }
    public void setSince(String since) { this.since = since; }

    public Boolean getIsMei() { return isMei; }
    public void setIsMei(Boolean isMei) { this.isMei = isMei; }

    public String getZip() { return zip; }
    public void setZip(String zip) { this.zip = zip; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public String getLatitude() { return latitude; }
    public void setLatitude(String latitude) { this.latitude = latitude; }

    public String getLongitude() { return longitude; }
    public void setLongitude(String longitude) { this.longitude = longitude; }


    public void setPhones(List<Phone> phones) {
        this.phones = phones;
    }
    public String getTelefoneFormatado() {

        if (phones == null || phones.isEmpty()) {
            return null;
        }

        Phone principal = phones.get(0); // pega o primeiro telefone

        return principal.getTelefoneCompleto();
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getInscricaoEstadual() { return inscricaoEstadual; }
    public void setInscricaoEstadual(String inscricaoEstadual) { this.inscricaoEstadual = inscricaoEstadual; }
}