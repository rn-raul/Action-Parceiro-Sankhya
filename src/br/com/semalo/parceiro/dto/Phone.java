package br.com.semalo.parceiro.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Phone {

    private String area;
    private String number;

    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }

    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }

    public String getTelefoneCompleto() {

        if (area == null && number == null) {
            return null;
        }

        String ddd = area != null ? area.trim() : "";
        String num = number != null ? number.trim() : "";

        return (ddd + num).replaceAll("\\D", "");
    }
}