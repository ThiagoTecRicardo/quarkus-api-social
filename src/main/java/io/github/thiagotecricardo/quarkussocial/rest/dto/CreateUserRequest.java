package io.github.thiagotecricardo.quarkussocial.rest.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class CreateUserRequest {

    @NotBlank(message = "Nome é obrigatório")
    private String name;
    @NotNull(message = "Idade é obrigatório")
    private Integer age;


}
