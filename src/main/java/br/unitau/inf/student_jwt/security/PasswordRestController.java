package br.unitau.inf.student_jwt.security;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import net.bytebuddy.utility.RandomString;

import br.unitau.inf.student_jwt.model.Usuario;
import br.unitau.inf.student_jwt.repository.UsuarioRepository;

@RestController
public class PasswordRestController {
	@Autowired
	private UsuarioRepository usuarioRepository;
	@Autowired
	private JavaMailSender sender;

	@PostMapping("/password/forgot")
	@Transactional
	public ResponseEntity<?> forgot(@RequestBody @Valid PasswordForgotDTO dto, UriComponentsBuilder uriBuilder) {
		ResponseEntity<?> ret = ResponseEntity.badRequest().build();
		String email = dto.getEmail();
		Optional<Usuario> search = usuarioRepository.findByEmail(email);
		if (search.isPresent()) {
			Usuario usuario = usuarioRepository.findById(search.get().getId()).get();
			String token = getToken();
			LocalDateTime limite = LocalDateTime.now().plusHours(12);
			usuario.setResetpassToken(token);
			usuario.setResetpassLimit(limite);
			System.out.println("Reset " + email);
			URI uri = uriBuilder.path("/password/read").buildAndExpand(token).toUri();
			String link = uri + "?token=" + token;
			String subject = "Reset da senha";
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss");
			String content = "<p>Ola " + usuario.getName() + ",</p>" + "<p>Voce solicitou o reset da sua senha.</p>"
					+ "<p>Click no link abaixo para alterar sua senha:</p>" + "<p><a href=\"" + link
					+ "\">Alterar senha</a></p>" + "<p>Esse link sera válido até " + limite.format(formatter) + "</p>"
					+ "<p>Ignore esse email se nao foi voce que solicitou o reset da senha.</p>";
			try {
				MimeMessage mail = sender.createMimeMessage();
				MimeMessageHelper helper = new MimeMessageHelper(mail);
				helper.setFrom(sender.getUsername());
				helper.setTo(email);
				helper.setSubject(subject);
				helper.setText(content, true);
				sender.send(mail);
				ret = ResponseEntity.ok().build();
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Erro ao enviar e-mail: " + e.getMessage());
			}
		} else {
			System.out.println("Usuario nao encontrado");
			ret = ResponseEntity.notFound().build();
		}
		return ret;
	}

	String getToken() {
		String ret = "";
		while (true) {
			ret = RandomString.make(30);
			Optional<Usuario> search = usuarioRepository.findByResetpassToken(ret);
			if (!search.isPresent()) {
				break;
			}
		}
		return ret;
	}
}
