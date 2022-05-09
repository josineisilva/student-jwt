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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import net.bytebuddy.utility.RandomString;

import br.unitau.inf.student_jwt.model.Usuario;
import br.unitau.inf.student_jwt.repository.UsuarioRepository;

@RestController
@RequestMapping("/usuario")
public class UsuarioRestController {
	@Autowired
	private UsuarioRepository repository;
	@Autowired
	private JavaMailSender sender;

	@PostMapping
	@Transactional
	public ResponseEntity<?> post(@RequestBody @Valid UsuarioPostDTO body, UriComponentsBuilder uriBuilder) {
		ResponseEntity<?> ret = ResponseEntity.badRequest().build();
		boolean inserir = false;
		String password = body.getPassword();
		String confirm = body.getConfirm();
		if (password.equals(confirm)) {
			String email = body.getEmail();
			Optional<Usuario> search = repository.findByEmail(email);
			if (search.isPresent()) {
				Usuario usuario = search.get();
				if (!usuario.isVerified()) {
					repository.delete(usuario);
					inserir = true;
				} else
					ret = ResponseEntity.unprocessableEntity().build();
			} else
				inserir = true;
			if (inserir) {
				Usuario item = new Usuario();
				body.update(item);
				item.setVerified(false);
				BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
				String encodedPassword = passwordEncoder.encode(password);
				item.setPassword(encodedPassword);
				String token = getToken();
				LocalDateTime limite = LocalDateTime.now().plusHours(12);
				item.setVerifyToken(token);
				item.setVerifyLimit(limite);
				System.out.println("Inserindo " + email);
				repository.save(item);
				URI verifyUri = uriBuilder.path("/usuario/verify").buildAndExpand(token).toUri();
				String link = verifyUri + "?token=" + token;
				String subject = "Verificação de usuário";
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss");
				String content = "<p>Ola " + item.getName() + ",</p>"
						+ "<p>Voce solicitou a inclusão do seu e-mail.</p>"
						+ "<p>Click no link abaixo para verificar o e-mail:</p>" + "<p><a href=\"" + link
						+ "\">Verificar email</a></p>" + "<p>Esse link sera válido até " + limite.format(formatter)
						+ "</p>" + "<p>Ignore esse email se nao foi voce que solicitou a inclusão do usuário.</p>";
				try {
					MimeMessage mail = sender.createMimeMessage();
					MimeMessageHelper helper = new MimeMessageHelper(mail);
					helper.setFrom(System.getenv("STUDENT_SMTP_USER"));
					helper.setTo(email);
					helper.setSubject(subject);
					helper.setText(content, true);
					sender.send(mail);
					ret = ResponseEntity.ok().build();
					URI uri = uriBuilder.path("/usuario/{id}").buildAndExpand(item.getId()).toUri();
					ret = ResponseEntity.created(uri).body(new UsuarioGetDTO(item));
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("Erro ao enviar e-mail: " + e.getMessage());
				}
			}
		}
		return ret;
	}

	String getToken() {
		String ret = "";
		while (true) {
			ret = RandomString.make(30);
			Optional<Usuario> search = repository.findByVerifyToken(ret);
			if (!search.isPresent()) {
				break;
			}
		}
		return ret;
	}
}
