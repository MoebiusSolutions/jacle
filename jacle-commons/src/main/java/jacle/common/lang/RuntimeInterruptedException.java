package jacle.common.lang;

public class RuntimeInterruptedException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private InterruptedException interruptedException;

	public RuntimeInterruptedException(InterruptedException e) {
		super(e);
		this.interruptedException = e;
	}

	public InterruptedException getInterruptedException() {
		return this.interruptedException;
	}
}
