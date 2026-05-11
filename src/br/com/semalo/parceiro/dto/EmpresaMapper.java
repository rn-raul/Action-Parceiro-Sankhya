package br.com.semalo.parceiro.dto;

public class EmpresaMapper {

    public static ParceiroDTO toParceiroDTO(EmpresaResponse empresa) {

        ParceiroDTO dto = new ParceiroDTO();

        if (empresa == null) return dto;

        if (empresa.getCompany() != null) {
            dto.setNome(empresa.getCompany().getName());

            // Lê o Simples
            if (empresa.getCompany().getSimples() != null) {
                dto.setOptante(empresa.getCompany().getSimples().getOptant());
                dto.setSince(empresa.getCompany().getSimples().getSince());
            }

            // 🆕 Lê o MEI
            if (empresa.getCompany().getSimei() != null) {
                dto.setIsMei(empresa.getCompany().getSimei().getOptant());
            }
        }

        if (empresa.getAddress() != null) {
            dto.setZip(empresa.getAddress().getZip());
            dto.setState(empresa.getAddress().getState());
            dto.setCity(empresa.getAddress().getCity());
            dto.setStreet(empresa.getAddress().getStreet());
            dto.setNumber(empresa.getAddress().getNumber());
            dto.setDistrict(empresa.getAddress().getDistrict());
            dto.setLatitude(empresa.getAddress().getLatitude());
            dto.setLongitude(empresa.getAddress().getLongitude());
        }

        if (empresa.getPhones() != null && !empresa.getPhones().isEmpty()) {
            Phone phone = empresa.getPhones().get(0);
            dto.setPhones(java.util.Collections.singletonList(phone));
        }

        if (empresa.getEmails() != null && !empresa.getEmails().isEmpty()) {
            dto.setEmail(empresa.getEmails().get(0).getAddress());
        }

        if (empresa.getRegistrations() != null && !empresa.getRegistrations().isEmpty()) {
            Registration reg = empresa.getRegistrations().get(0);
            // Só grava a IE se a flag "enabled" for true
            if (Boolean.TRUE.equals(reg.getEnabled())) {
                dto.setInscricaoEstadual(reg.getNumber());
            }
        }

        return dto;
    }
}