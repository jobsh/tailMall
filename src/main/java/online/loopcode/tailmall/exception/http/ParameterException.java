/**
 * @作者 7七月
 * @微信公号 林间有风
 * @开源项目 $ http://talelin.com
 * @免费专栏 $ http://course.talelin.com
 * @我的课程 $ http://imooc.com/t/4294850
 * @创建时间 2020-01-10 16:14
 */
package online.loopcode.tailmall.exception.http;

public class ParameterException extends HttpException {
    public ParameterException(int code){
        this.code = code;
        this.httpStatusCode = 400;
    }
}
