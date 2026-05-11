package br.com.semalo.parceiro;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.semalo.parceiro.client.CnpjaClient;
import br.com.semalo.parceiro.dto.ParceiroDTO;
import br.com.semalo.parceiro.dto.EmpresaMapper;
import br.com.semalo.parceiro.modal.ApiResponse;
import br.com.semalo.parceiro.dto.EmpresaResponse;
import br.com.semalo.parceiro.repository.dbSankhya;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Parceiro implements AcaoRotinaJava {

    @Override
    public void doAction(ContextoAcao ctx) throws Exception {

        String cnpj = (String) ctx.getParam("CNPJ");

        // 🧪 Validação básica
        if (cnpj == null || cnpj.trim().isEmpty()) {
            ctx.mostraErro("CNPJ não informado.");
            return;
        }

        cnpj = cnpj.replaceAll("\\D", "");

        if (cnpj.length() != 14) {
            ctx.mostraErro("CNPJ inválido.");
            return;
        }

        ApiResponse response;

        // 🌐 Chamada da API protegida
        try {
            CnpjaClient client = new CnpjaClient();
            response = client.send(cnpj);
        } catch (Exception e) {
            ctx.mostraErro("Não foi possível consultar a API de CNPJ.\nDetalhes: " + e.getMessage());
            return;
        }

        // 📡 Tratamento por status HTTP
        if (response.status >= 200 && response.status < 300) {

            EmpresaResponse empresa;

            try {
                ObjectMapper mapper = new ObjectMapper();
                empresa = mapper.readValue(response.body, EmpresaResponse.class);
            } catch (Exception e) {
                ctx.mostraErro("Erro ao interpretar os dados retornados pela API.");
                return;
            }

            try {
                ParceiroDTO parceiro = EmpresaMapper.toParceiroDTO(empresa);
                String foundedStr = empresa.getFounded();
                dbSankhya db = new dbSankhya();
                db.insertDb(ctx, parceiro, cnpj, foundedStr);

            } catch (Exception e) {
                ctx.mostraErro("Erro ao cadastrar parceiro no Sankhya.\nDetalhes: " + e.getMessage());
            }

        } else if (response.status == 400) {

            ctx.mostraErro("Empresa não encontrada para o CNPJ: " + cnpj);

        } else if (response.status == 401) {

            ctx.mostraErro("Token da API inválido ou expirado.");

        } else if (response.status == 429) {

            ctx.mostraErro("Limite de consultas da API atingido. Tente novamente mais tarde.");

        } else {

            ctx.mostraErro(
                    "Erro ao consultar API de CNPJ.\n" +
                            "Status: " + response.status + "\n" +
                            "Resposta: " + response.body
            );
        }
    }
}
