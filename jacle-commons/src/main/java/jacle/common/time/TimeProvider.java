package jacle.common.time;

import java.util.Date;

public interface TimeProvider {
	
	public static final DefaultTimeProvider DEFAULT = new DefaultTimeProvider(); 
	
	public Date getTime();
}
