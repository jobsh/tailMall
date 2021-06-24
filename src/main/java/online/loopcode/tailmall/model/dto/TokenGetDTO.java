package online.loopcode.tailmall.model.dto;

import lombok.Getter;
import lombok.Setter;
import online.loopcode.tailmall.core.enumeration.LoginType;
import online.loopcode.tailmall.model.dto.validators.TokenPassword;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class TokenGetDTO {
    @NotBlank(message = "account不允许为空")
    private String account;
    @TokenPassword(max=30, message = "{token.password}")
    private String password;

    private LoginType type;
}
