package br.unitau.inf.student_jwt.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import br.unitau.inf.student_jwt.model.Student;

public class StudentPostDTO {
	@NotBlank
	private String name;
	private Integer age;
	@Email
	private String email;

	public String getName() {
		return name;
	}

	public Integer getAge() {
		return age;
	}

	public String getEmail() {
		return email;
	}

	public void update(Student obj) {
		obj.setName(name);
		obj.setAge(age);
		obj.setEmail(email);
	}
}
