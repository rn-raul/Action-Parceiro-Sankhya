package br.com.semalo.parceiro.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Simples {

    private Boolean optant;
    private String since;

    public Boolean getOptant() { return optant; }
    public void setOptant(Boolean optant) { this.optant = optant; }

    public String getSince() { return since; }
    public void setSince(String since) { this.since = since; }
}