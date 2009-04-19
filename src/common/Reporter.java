package common;

public interface Reporter<T>
{
	boolean report(T content);
	boolean isStopped();
}
