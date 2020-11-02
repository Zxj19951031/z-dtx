package org.zipper.plugins.common.tunnels;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zipper.plugins.common.records.Record;
import org.zipper.plugins.common.records.TerminateRecord;

import java.util.Collection;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 基于阻塞队列进行缓冲
 *
 * @author zhuxj
 */
public class BufferQueue {
    private ArrayBlockingQueue<Record> queue = null;

    private final ReentrantLock lock;

    private final Condition notInsufficient;
    private final Condition notEmpty;

    private static final Logger logger = LoggerFactory.getLogger(BufferQueue.class);

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
                notInsufficient.await(200L, TimeUnit.MILLISECONDS);
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
                notEmpty.await(200L, TimeUnit.MILLISECONDS);
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
