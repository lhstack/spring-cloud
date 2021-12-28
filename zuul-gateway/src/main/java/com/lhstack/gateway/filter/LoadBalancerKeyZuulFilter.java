package com.lhstack.gateway.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

/**
 * @author lhstack
 * @date 2021/12/12
 * @class LoadBalancerKeyZuulFilter
 * @since 1.8
 */
@Component
public class LoadBalancerKeyZuulFilter extends ZuulFilter {
    @Override
    public String filterType() {
        return FilterConstants.ROUTE_TYPE;
    }

    @Override
    public int filterOrder() {
        return -1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext currentContext = RequestContext.getCurrentContext();
        currentContext.set(FilterConstants.LOAD_BALANCER_KEY, currentContext.getRequest());
        return null;
    }
}
