package online.loopcode.tailmall.service;

import online.loopcode.tailmall.exception.http.ServerErrorException;
import online.loopcode.tailmall.model.Order;
import online.loopcode.tailmall.model.bo.OrderMessageBO;
import online.loopcode.tailmall.repository.OrderRepository;
import online.loopcode.tailmall.repository.SkuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class OrderCancelService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private SkuRepository skuRepository;

    @Transactional
    public void cancel(OrderMessageBO messageBO) {
        if (messageBO.getOrderId() <= 0) {
            throw new ServerErrorException(9999);
        }
        this.cancel(messageBO.getOrderId());
    }

    private void cancel(Long oid) {
        Optional<Order> orderOptional = orderRepository.findById(oid);
        Order order = orderOptional.orElseThrow(() ->
                new ServerErrorException(9999));
        int res = orderRepository.cancelOrder(oid);
        if (res != 1) {
            return;
        }
        order.getSnapItems().forEach(i -> {
            skuRepository.recoverStock(i.getId(), i.getCount().longValue());
        });
    }
}
