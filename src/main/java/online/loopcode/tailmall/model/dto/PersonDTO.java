package online.loopcode.tailmall.model.dto;

import lombok.Builder;
import lombok.Getter;
import online.loopcode.tailmall.model.dto.validators.PasswordEqual;
import org.hibernate.validator.constraints.Length;

@Builder
@Getter
@PasswordEqual(message = "两次密码不相同")
public class PersonDTO {
    @Length(min=2, max=10, message = "xxxxx")
    private String name;
    private Integer age;

//@PasswordEqual

    private String password1;
    private String password2;
}



