package br.com.semalo.parceiro.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Email {

    private String address;

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}