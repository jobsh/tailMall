package online.loopcode.tailmall.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import online.loopcode.tailmall.exception.http.ParameterException;
import online.loopcode.tailmall.model.User;
import online.loopcode.tailmall.repository.UserRepository;
import online.loopcode.tailmall.util.JwtToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class WxAuthenticationService {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private UserRepository userRepository;

    @Value("${wx.code2session}")
    private String code2SessionUrl;
    @Value("${wx.appid}")
    private String appid;
    @Value("${wx.appsecret}")
    private String appsecret;

    public String code2Session(String code) {
        String url = MessageFormat.format(this.code2SessionUrl, this.appid, this.appsecret, code);
        RestTemplate rest = new RestTemplate();
        Map<String, Object> session = new HashMap<>();
        String sessionText = rest.getForObject(url, String.class);
        try {
            session = mapper.readValue(sessionText, Map.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return this.registerUser(session);
    }

    private String registerUser(Map<String, Object> session) {
        String openid = (String) session.get("openid");
        if (openid == null) {
            throw new ParameterException(20004);
        }
        Optional<User> userOptional = this.userRepository.findByOpenid(openid);
//        userOptional.ifPresentOrElse(Consummer, Runable)
        if (userOptional.isPresent()) {
            // 返回JWT令牌
            // 数字等级.
            return JwtToken.makeToken(userOptional.get().getId());
        }
        User user = User.builder()
                .openid(openid)
                .build();
        userRepository.save(user);
        // 返回JWT令牌
        Long uid = user.getId();
        return JwtToken.makeToken(uid);
    }
}
