package br.com.semalo.parceiro.repository;

import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import br.com.semalo.parceiro.dto.ParceiroDTO;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class dbSankhya {

    public void insertDb(ContextoAcao ctx, ParceiroDTO dto, String cnpj, String founded) throws Exception {
        JdbcWrapper jdbc = EntityFacadeFactory.getDWFFacade().getJdbcWrapper();
        jdbc.openSession();

        try {
            LocalDate dataFundacao;
            try {
                dataFundacao = LocalDate.parse(founded);
            } catch (DateTimeParseException e) {
                throw new Exception("Data de fundação inválida recebida da API: '" + founded + "'. Formato esperado: yyyy-MM-dd.", e);
            }

            Registro registro = ctx.novaLinha("TGFPAR");

            // Validação para quantidade de caracteres.
            String nome = dto.getNome();
            String avisoNomeTruncado = "";
            if (nome.length() > 40) {
                nome = nome.substring(0, 40);
                avisoNomeTruncado = "\nAviso: O nome da empresa excedia 40 caracteres e foi truncado para salvar no Sankhya.";
            }
            registro.setCampo("NOMEPARC", nome);
            registro.setCampo("RAZAOSOCIAL", nome);
            registro.setCampo("TIPPESSOA", "J");
            registro.setCampo("CGC_CPF", cnpj);
            registro.setCampo("FAX", dto.getTelefoneFormatado());
            registro.setCampo("EMAIL", dto.getEmail());
            registro.setCampo("LATITUDE", dto.getLatitude());
            registro.setCampo("LONGITUDE", dto.getLongitude());
            registro.setCampo("NUMEND", dto.getNumber());
            registro.setCampo("CODCTACTB", 12201); // Conta Contábil Padrão
            registro.setCampo("CODCTACTB2", 21201);
            registro.setCampo("DTNASC", Date.valueOf(dataFundacao));
            registro.setCampo("CLIENTE", "S");
            // ==========================================
            // 🛡️ ESCUDO 1: Vínculos de Endereço (CEP)
            // ==========================================
            // Definimos 0 (Não Informado) como padrão caso o CEP falhe
            int codCid = 0;
            int codBai = 0;
            int codEnd = 0;
            String codCep = "";
            if (dto.getZip() != null && !dto.getZip().trim().isEmpty()) {
                CepVinculos vinc = buscarVinculosCep(jdbc, dto.getZip());
                if (vinc != null) {
                    codCid = vinc.codCid;
                    codBai = vinc.codBai;
                    codEnd = vinc.codEnd;
                    codCep = vinc.codCep;
                }
            }

            // Grava os códigos (sejam eles da base ou o 0 de segurança)
            registro.setCampo("CODCID", codCid);
            registro.setCampo("CODBAI", codBai);
            registro.setCampo("CODEND", codEnd);
            registro.setCampo("CEP", codCep);
            // ==========================================
            // 🛡️ ESCUDO 2: Vínculo de Estado (UF)
            // ==========================================
            Integer codUf = buscarCodUf(jdbc, dto.getState());
            if (codUf != null) {
                registro.setCampo("AD_TSIUFS", codUf);
            }
            // Se codUf for null, simplesmente não enviamos o campo, evitando o erro.

            // ==========================================
            // 🧠 REGRAS DE NEGÓCIO FISCAIS
            // ==========================================
            String ie = dto.getInscricaoEstadual();
            if (ie == null || ie.trim().isEmpty()) {
                registro.setCampo("IDENTINSCESTAD", "ISENTO");
                registro.setCampo("CLASSIFICMS", "C");
            } else {
                registro.setCampo("IDENTINSCESTAD", ie);
                registro.setCampo("CLASSIFICMS", "R");
            }

            int grupoIcms = 2;

            if (Boolean.TRUE.equals(dto.getIsMei())) {
                registro.setCampo("MEIRJ", "S");
                registro.setCampo("SIMPLES", "N");
                grupoIcms = 6;
            } else if (Boolean.TRUE.equals(dto.getOptante())) {
                registro.setCampo("SIMPLES", "S");
                registro.setCampo("MEIRJ", "N");
                grupoIcms = 6;
            } else {
                registro.setCampo("SIMPLES", "N");
                registro.setCampo("MEIRJ", "N");
            }

            // ==========================================
            // 🛡️ ESCUDO 3: Salvamento e Validação do ID
            // ==========================================
            registro.save();

            BigDecimal codParcGerado = (BigDecimal) registro.getCampo("CODPARC");
            if (codParcGerado == null) {
                throw new Exception("Parceiro foi salvo, mas o Sankhya não retornou o CODPARC para vincular à empresa.");
            }

            // 6. Insere as Configurações por Empresa (TGFPAEM)
            Registro addIcms = ctx.novaLinha("TGFPAEM");
            addIcms.setCampo("CODPARC", codParcGerado);
            addIcms.setCampo("CODEMP", 2);
            addIcms.setCampo("GRUPOICMS", grupoIcms);
            addIcms.save();

            String mensagem = "Parceiro cadastrado com sucesso!\n" +
                    "Código do Parceiro: " + codParcGerado + "\n" +
                    "Atenção: Se a Cidade estiver como 'Não Informado', verifique o cadastro de CEP." +
                    avisoNomeTruncado;
            ctx.setMensagemRetorno(mensagem);

        } catch (Exception e) {
            ctx.mostraErro("Erro ao cadastrar parceiro: " + e.getMessage());
        } finally {
            try { JdbcWrapper.closeSession(jdbc); } catch (Exception ignored) {}
        }
    }

    private static Integer buscarCodUf(JdbcWrapper jdbc, String uf) throws Exception {
        if (uf == null || uf.trim().isEmpty()) return null;
        NativeSql ns = new NativeSql(jdbc);
        ResultSet rs = null;
        try {
            ns.appendSql("SELECT CODUF FROM TSIUFS WHERE UF = :UF");
            ns.setNamedParameter("UF", uf.trim().toUpperCase());
            rs = ns.executeQuery();
            if (!rs.next()) return null;
            return rs.getInt("CODUF");
        } finally {
            if (rs != null) { try { rs.close(); } catch (Exception ignored) {} }
            try { NativeSql.releaseResources(ns); } catch (Exception ignored) {}
        }
    }

    private static CepVinculos buscarVinculosCep(JdbcWrapper jdbc, String cep) throws Exception {
        NativeSql ns = new NativeSql(jdbc);
        ResultSet rs = null;
        try {
            ns.appendSql("SELECT CODCID, CODBAI, CODEND, CEP FROM TSICEP WHERE CEP = :CEP");
            ns.setNamedParameter("CEP", cep);
            rs = ns.executeQuery();
            if (!rs.next()) return null;
            CepVinculos v = new CepVinculos();
            v.codCid = rs.getInt("CODCID");
            v.codBai = rs.getInt("CODBAI");
            v.codEnd = rs.getInt("CODEND");
            v.codCep = rs.getString("CEP");
            return v;
        } finally {
            if (rs != null) { try { rs.close(); } catch (Exception ignored) {} }
            try { NativeSql.releaseResources(ns); } catch (Exception ignored) {}
        }
    }

    private static class CepVinculos {
        int codCid;
        int codBai;
        int codEnd;
        String codCep;
    }
}
