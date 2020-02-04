package cn.com.citydo.dtx.core.spi.tunnels;

import cn.com.citydo.dtx.core.spi.records.Record;
import cn.com.citydo.dtx.core.spi.records.TerminateRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 基于阻塞队列进行缓冲
 */
public class BufferQueue {
    private ArrayBlockingQueue<Record> queue = null;

    private ReentrantLock lock;

    private Condition notInsufficient, notEmpty;

    private static Logger logger = LoggerFactory.getLogger(BufferQueue.class);

    /**
     * 初始化阻塞队列和条件锁
     *
     * @param capacity 容量
     */
    public BufferQueue(int capacity) {
        this.queue = new ArrayBlockingQueue<>(capacity);
        lock = new ReentrantLock();
        notInsufficient = lock.newCondition();
        notEmpty = lock.newCondition();
    }

    public void terminate() {
        try {
            this.queue.put(TerminateRecord.get());
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void doPushAll(Collection<Record> rs) {
        try {
            lock.lockInterruptibly();
            while (this.queue.size() > 0) {
                notInsufficient.await(100L, TimeUnit.MILLISECONDS);
            }
            this.queue.addAll(rs);
            notEmpty.signalAll();
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        } finally {
            lock.unlock();
        }
    }

    public void doPullAll(Collection<Record> rs) {
        assert rs != null;
        rs.clear();
        try {
            lock.lockInterruptibly();
            while (this.queue.drainTo(rs) <= 0) {
                notEmpty.await(100L, TimeUnit.MILLISECONDS);
            }
            notInsufficient.signalAll();
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        } finally {
            lock.unlock();
        }
    }

    public void clear() {
        this.queue.clear();
    }

    public boolean isEmpty() {
        return this.queue.isEmpty();
    }
}
