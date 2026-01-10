package me.vladislav.information_systems_1.cache.interceptor;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import me.vladislav.information_systems_1.cache.HibernateStatisticsProvider;
import me.vladislav.information_systems_1.cache.LogCacheStats;
import me.vladislav.information_systems_1.cache.config.CacheLoggingConfig;
import org.hibernate.stat.Statistics;

@Interceptor
@LogCacheStats
@Priority(Interceptor.Priority.APPLICATION)
public class CacheStatisticsInterceptor {

    @Inject
    CacheLoggingConfig config;

    @Inject
    HibernateStatisticsProvider statsProvider;

    @AroundInvoke
    public Object logCacheStats(InvocationContext ctx) throws Exception {

        if (!config.isEnabled()) {
            return ctx.proceed();
        }

        Statistics stats = statsProvider.getStatistics();

        long hitsBefore = stats.getSecondLevelCacheHitCount();
        long missesBefore = stats.getSecondLevelCacheMissCount();

        Object result = ctx.proceed();

        long hitsAfter = stats.getSecondLevelCacheHitCount();
        long missesAfter = stats.getSecondLevelCacheMissCount();

        System.out.println(
                "[L2 CACHE] " + ctx.getMethod().getName() +
                        " hits=" + (hitsAfter - hitsBefore) +
                        " misses=" + (missesAfter - missesBefore)
        );

        return result;
    }
}

