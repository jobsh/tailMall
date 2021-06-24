
package online.loopcode.tailmall.api.v1;

import online.loopcode.tailmall.core.LocalUser;
import online.loopcode.tailmall.core.interceptors.ScopeLevel;
import online.loopcode.tailmall.lib.LinWxNotify;
import online.loopcode.tailmall.service.WxPaymentNotifyService;
import online.loopcode.tailmall.service.WxPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Positive;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@RequestMapping("payment")
@RestController
@Validated
public class PaymentController {

    @Autowired
    private WxPaymentService wxPaymentService;

    @Autowired
    private WxPaymentNotifyService wxPaymentNotifyService;


    /**
     * 小程序支付接口
     * @param oid 订单id
     * @return 微信服务器返回来的参数，需要把这些参数组装发给前端小程序，前端小程序才能向微信服务器拉起支付
     */
    @PostMapping("/pay/order/{id}")
    @ScopeLevel
    public Map<String,String> preWxOrder(@PathVariable(name = "id") @Positive Long oid) {
        Long uid = LocalUser.getUser().getId();
        return this.wxPaymentService.preOrder(oid,uid);
    }

    /**
     * 支付回调接口
     * @param request 微信服务的请求信息
     * @param response 返回给微信小程序的response
     * @return
     */
    @RequestMapping("/wx/notify")
    public String payCallback(HttpServletRequest request,
                              HttpServletResponse response) {
        InputStream s;
        try {
            s = request.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
            return LinWxNotify.fail();
        }
        String xml;
        xml = LinWxNotify.readNotify(s);
        try{
            this.wxPaymentNotifyService.processPayNotify(xml);
        }
        catch (Exception e){
            return LinWxNotify.fail();
        }
        return LinWxNotify.success();
    }
}
