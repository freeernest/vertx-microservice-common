package com.bigpanda.commons.streams;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.streams.WriteStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

public class ThrottleStreamImpl<T> extends BackPressuredWriteStreamImpl<T> implements ThrottledStream<T> {
    private int quota;
    private int leftQuota;
    private long quotaPeriod;
    private Vertx vertx;
    private long quotaTime;
    private Path persistentQuotaTimeFile;

    private Logger logger = LoggerFactory.getLogger(getClass());

    public ThrottleStreamImpl(Handler<T> writeHandler, long quotaPeriod, int quota, String persistentQuotaTimeFile, Vertx vertx) {
        super(writeHandler);
        this.quotaPeriod = quotaPeriod;
        this.quota = quota;
        this.persistentQuotaTimeFile = Paths.get(persistentQuotaTimeFile);
        this.vertx = vertx;
        quotaTime = 0;
        handler = null;
        recycleQuota();
    }

    @Override
    public boolean writeQueueFull() {
        if (logger.isDebugEnabled() && leftQuota < 1) {
            logger.debug("quota finished waiting for next recycle, next recycle={}", new Date(quotaTime));
        }
        return super.writeQueueFull() || leftQuota < 1;
    }

    @Override
    public void end() {
        super.end();
        writeQuotaTime();
    }

    @Override
    public synchronized WriteStream<T> write(T data) {
        WriteStream<T> stream = super.write(data);
        leftQuota--;

        return stream;
    }

    protected void recycleQuota() {
        logger.debug("recycleQuota");
        if (quotaTime == 0)
            readQuotaTime();

        long currentTimeMillis = System.currentTimeMillis();
        if (quotaTime < currentTimeMillis) {
            leftQuota = quota;
            quotaTime =  currentTimeMillis + quotaPeriod;
            writeQuotaTime();
            if (handler != null) {
                releaseStream();
            }
        }
        long nextRecycle = quotaTime - currentTimeMillis;
        if (nextRecycle < 1) {
            logger.warn("nextRecycle is < 1ms, nextRecycle={}, leftQuota={}, quotaPeriod={}", nextRecycle, leftQuota, quotaPeriod);
            nextRecycle = 1;
        }

        vertx.setTimer(nextRecycle, t -> recycleQuota());

        if (logger.isDebugEnabled()) {
            Date quotaTimeDate = new Date(quotaTime);
            Date now = new Date();
            logger.debug("quota = {}, quota time={}, current time={}, next recycle in milliseconds={}, quota period in minutes={}"
                    , leftQuota, quotaTimeDate, now, quotaTimeDate.getTime() - now.getTime(), quotaPeriod / 60000);
        }
    }


    private void writeQuotaTime() {
        try {
            JsonObject throttle = new JsonObject();
            throttle.put("quotaTime", quotaTime);
            throttle.put("leftQuota", leftQuota);
            Files.write(persistentQuotaTimeFile, throttle.toString().getBytes());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void readQuotaTime() {
        try {
            String throttleStr = Files.exists(persistentQuotaTimeFile) ? new String(Files.readAllBytes(persistentQuotaTimeFile)) : null;
            if (throttleStr != null) {
                JsonObject throttle = new JsonObject(throttleStr);
                quotaTime = throttle.getLong("quotaTime");
                leftQuota = throttle.getInteger("leftQuota");
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            quotaTime = 0;
        }
    }

    @Override
    public void incrementLeftQuota() {
        if (leftQuota++ == 0 && handler != null) {
            releaseStream();
        }
    }

    @Override
    public void breakQuota() {
        leftQuota = 0;
        writeQuotaTime();
    }

}
