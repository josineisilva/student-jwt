package br.unitau.inf.student_jwt.security;

public class JwtDTO {
	private String token;
	private String tipo;

	public JwtDTO(String token, String tipo) {
		this.token = token;
		this.tipo = tipo;
	}

	public String getToken() {
		return token;
	}

	public String getTipo() {
		return tipo;
	}
}