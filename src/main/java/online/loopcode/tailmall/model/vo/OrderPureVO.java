package online.loopcode.tailmall.model.vo;

import lombok.Getter;
import lombok.Setter;
import online.loopcode.tailmall.model.Order;
import org.springframework.beans.BeanUtils;

import java.util.Date;

@Getter
@Setter
public class OrderPureVO extends Order {
    private Long period;
    private Date createTime;

    public OrderPureVO(Order order, Long period) {
        BeanUtils.copyProperties(order, this);
        this.period = period;
    }
}

