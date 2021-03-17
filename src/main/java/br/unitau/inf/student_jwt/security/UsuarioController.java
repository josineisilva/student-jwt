package br.unitau.inf.student_jwt.security;

import java.time.LocalDateTime;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.unitau.inf.student_jwt.model.Usuario;
import br.unitau.inf.student_jwt.repository.UsuarioRepository;

@Controller
public class UsuarioController {
	@Autowired
	private UsuarioRepository repository;

	@GetMapping("/usuario/verify")
	@Transactional
	public String read(@RequestParam String token, Model model) {
		String ret = "verifymessage";
		model.addAttribute("token", token);
		Optional<Usuario> search = repository.findByVerifyToken(token);
		if (search.isPresent()) {
			Usuario usuario = search.get();
			if (LocalDateTime.now().isBefore(usuario.getVerifyLimit())) {
				usuario.setVerifyToken(null);
                usuario.setVerifyLimit(null);
                usuario.setVerified(true);
                model.addAttribute("success", "Email verificado com sucesso");	
			} else
				model.addAttribute("error", "Requisição expirada");
		} else
			model.addAttribute("error", "Requisição inválida");
		return ret;
	}
}