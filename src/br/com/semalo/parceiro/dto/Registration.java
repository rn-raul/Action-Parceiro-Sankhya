package br.com.semalo.parceiro.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Registration {
    private String number;
    private String state;
    private Boolean enabled; // 🆕 Importante para saber se a IE está ativa

    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }
}