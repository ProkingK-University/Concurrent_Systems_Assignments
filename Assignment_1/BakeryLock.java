import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.Condition;

public class BakeryLock implements Lock
{
	private int numberOfThreads;
    private volatile int[] label;
	private volatile boolean[] flag;

	public BakeryLock(int numberOfThreads)
	{
        label = new int[numberOfThreads];
		flag = new boolean[numberOfThreads];
        this.numberOfThreads = numberOfThreads;

        for (int i = 0; i < numberOfThreads; i++)
		{
            label[i] = 0;
        }
    }

	@Override
    public void lock()
	{
        int i = (int) (Thread.currentThread().threadId() % numberOfThreads);

        flag[i] = true;
        label[i] = max(label) + 1;
        
        for (int k = 0; k < numberOfThreads; k++)
		{
            while (flag[k] && ((label[k] < label[i]) || (label[k] == label[i] && k < i))) {}
        }
    }

	@Override
	public void unlock()
	{
		int i = (int) (Thread.currentThread().threadId() % numberOfThreads);
        flag[i] = false;
	}

	private int max(int[] array)
	{
        int max = array[0];

        for (int value : array)
		{
            if (value > max)
			{
                max = value;
            }
        }

        return max;
    }

	public boolean tryLock() { return false; }
	
	public Condition newCondition() { return null; }

	public void lockInterruptibly() throws InterruptedException {}

	public boolean tryLock(long time, TimeUnit unit) throws InterruptedException { return false; }
}