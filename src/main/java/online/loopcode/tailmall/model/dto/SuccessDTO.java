package online.loopcode.tailmall.model.dto;

import lombok.*;
import online.loopcode.tailmall.util.HttpRequestProxy;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SuccessDTO {
    private Integer code = 0;
    private String message = "ok";
    private String request = HttpRequestProxy.getRequestUrl();
}
