package bgu.spl.mics;

import org.apache.commons.lang3.time.StopWatch;

import java.util.concurrent.TimeUnit;


/**
 * A Future object represents a promised result - an object that will
 * eventually be resolved to hold a result of some operation. The class allows
 * Retrieving the result once it is available.
 * <p>
 * Only private methods may be added to this class.
 * No public constructor is allowed except for the empty constructor.
 */
public class Future<T> {
    private T result;
    private boolean isOver;

    /**
     * This should be the the only public constructor in this class.
     */
    public Future() {

    }

    /**
     * retrieves the result the Future object holds if it has been resolved.
     * This is a blocking method! It waits for the computation in case it has
     * not been completed.
     * <p>
     *
     * @return return the result of type T if it is available, if not wait until it is available.
     */
    public T get() {
        // waiting is always synched in such manner
        synchronized (this) {
            try {
                while (!isDone()) {
                    this.wait();
                }
            } catch (InterruptedException e) {
                System.out.println("!!!!! was interupted while waiting for future to resolve!!!");
            }
            return result;
        }
    }

    /**
     * Resolves the result of this Future object.
     */
    public void resolve(T result) {
        // notify all should be synched
        synchronized (this) {
            this.result = result;
            this.isOver = true;
            this.notifyAll();
        }
    }

    /**
     * @return true if this object has been resolved, false otherwise
     */
    public boolean isDone() {
        return isOver;
    }

    /**
     * retrieves the result the Future object holds if it has been resolved,
     * This method is non-blocking, it has a limited amount of time determined
     * by {@code timeout}
     * <p>
     *
     * @param timeout the maximal amount of time units to wait for the result.
     * @param unit    the {@link TimeUnit} time units to wait.
     * @return return the result of type T if it is available, if not,
     * wait for {@code timeout} TimeUnits {@code unit}. If time has
     * elapsed, return null.
     */
    public T get(long timeout, TimeUnit unit) {
        // surround wait with synch
        synchronized (this) {
            try {
                StopWatch stopWatch = new StopWatch();
                stopWatch.start();
                long milli = unit.toMillis(timeout);
                while (!isDone() && stopWatch.getTime(TimeUnit.MILLISECONDS) < milli) {
                    this.wait(milli);
                }
            } catch (InterruptedException e) {
                System.out.println("!!!!! was interupted while waiting for future timeout to resolve!!!");
            }
            return result;
        }
    }

}
