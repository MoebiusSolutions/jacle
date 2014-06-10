package com.moesol.common.moesolcommons.time;

import java.util.Date;

public class DefaultTimeProvider implements TimeProvider {
	
	@Override
	public Date getTime() {
		return new Date();
	}
}
