package br.com.semalo.parceiro.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Company {
    private String name;
    private Simples simples;
    private Simei simei; // 🆕 Adicionado para ler o MEI do JSON

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Simples getSimples() { return simples; }
    public void setSimples(Simples simples) { this.simples = simples; }

    public Simei getSimei() { return simei; }
    public void setSimei(Simei simei) { this.simei = simei; }
}