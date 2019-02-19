package zwd.com.zuul.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.exception.ZuulException;


/**
 * @author zwd
 * @date 2018/10/30 08:58
 * @Email stephen.zwd@gmail.com
 */
public class MyFilter extends ZuulFilter{
    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 10;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        System.out.println("zuul filter");
        return null;
    }
}
