package br.com.semalo.parceiro.modal;

public class ApiResponse {
   public int status;
   public String body;

   public ApiResponse(int status, String body) {
        this.status = status;
        this.body = body;
    }
}
